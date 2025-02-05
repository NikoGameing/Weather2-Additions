package nikosmods.weather2additions.blocks.blockfunction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import nikosmods.weather2additions.Weather2Additions;
import nikosmods.weather2additions.blocks.CableSmall;
import nikosmods.weather2additions.blocks.blockentityreg.BlockEntityTypes;
import nikosmods.weather2additions.blocks.blockfunction.blockgui.NetworkInfoMenu;
import nikosmods.weather2additions.network.AnalyserPacket;
import nikosmods.weather2additions.network.Messages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class CableGenericEntity extends BlockEntity implements MenuProvider {

    public static Logger logger = Weather2Additions.LOGGER;

    public int capacity;
    public int throughputIn;
    public int throughputOut;

    EnergyNetwork energyNetwork;

    public static void tick(Level level, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
        if (!level.isClientSide()){
            BlockEntity above = level.getBlockEntity(blockPos.above());
            BlockEntity below = level.getBlockEntity(blockPos.below());
            BlockEntity north = level.getBlockEntity(blockPos.north());
            BlockEntity east = level.getBlockEntity(blockPos.east());
            BlockEntity south = level.getBlockEntity(blockPos.south());
            BlockEntity west = level.getBlockEntity(blockPos.west());
            connectToSide(level, blockPos, state, blockEntity, above, CableSmall.UP, Direction.UP);
            connectToSide(level, blockPos, state, blockEntity, below, CableSmall.DOWN, Direction.DOWN);
            connectToSide(level, blockPos, state, blockEntity, north, CableSmall.NORTH, Direction.NORTH);
            connectToSide(level, blockPos, state, blockEntity, east, CableSmall.EAST, Direction.EAST);
            connectToSide(level, blockPos, state, blockEntity, south, CableSmall.SOUTH, Direction.SOUTH);
            connectToSide(level, blockPos, state, blockEntity, west, CableSmall.WEST, Direction.WEST);

        }
    }

    public static void connectToSide(Level level, BlockPos blockPos, BlockState state, BlockEntity blockEntity, BlockEntity blockAdjacent, EnumProperty<CableSmall.Connection> side, Direction sideDirectional) {
        CableGenericEntity cableEntity = (CableGenericEntity) blockEntity;
        if (blockAdjacent == null) {
            setState(state, level, state.setValue(side, CableSmall.Connection.NONE), blockPos);
        }
        else if (blockAdjacent instanceof CableGenericEntity genericEntity) {
            cableEntity.mergeNetworks(genericEntity.energyNetwork);
            setState(state, level, state.setValue(side, CableSmall.Connection.CABLE), blockPos);
        }
        else {
            LazyOptional<IEnergyStorage> potentialConsumer = blockAdjacent.getCapability(ForgeCapabilities.ENERGY, sideDirectional.getOpposite());
            potentialConsumer.ifPresent(consumer -> cableEntity.energyNetwork.getEnergyStorage().extractEnergy(consumer.receiveEnergy(cableEntity.energyNetwork.getEnergyStorage().extractEnergy(cableEntity.energyNetwork.getEnergyStorage().getThroughputOut(), true), false), false));
            if (!potentialConsumer.isPresent()) {
                setState(state, level, state.setValue(side, CableSmall.Connection.NONE), blockPos);
            }
            else {
                setState(state, level, state.setValue(side, CableSmall.Connection.CONNECTED), blockPos);
            }
        }
    }

    public static void setState(BlockState previousState, Level level, BlockState newState, BlockPos blockPos) {
        if (previousState != newState) {
             level.setBlock(blockPos, newState, 2);
        }
    }

    public void mergeNetworks(EnergyNetwork otherNetwork) {
        if (energyNetwork != otherNetwork) {
            int stored = energyNetwork.getEnergyStorage().getEnergyStored() + otherNetwork.getEnergyStorage().getEnergyStored();
            int capacity = energyNetwork.getEnergyStorage().getMaxEnergyStored() + otherNetwork.getEnergyStorage().getMaxEnergyStored();
            EnergyNetwork combinedNetwork = new EnergyNetwork(this, capacity, throughputIn, throughputOut);
            combinedNetwork.getEnergyStorage().setEnergyStored(stored);
            combinedNetwork.setCableEntities(energyNetwork.getCableEntities());
            combinedNetwork.setCableEntities(otherNetwork.getCableEntities());
            for (CableGenericEntity cableGeneric : energyNetwork.getCableEntities()) {
                cableGeneric.energyNetwork = combinedNetwork;
            }
            for (CableGenericEntity cableGeneric : otherNetwork.getCableEntities()) {
                cableGeneric.energyNetwork = combinedNetwork;
            }
        }
    }

    public void splitNetwork() {
        energyNetwork.getCableEntities().forEach(cableGeneric -> cableGeneric.createNetwork(cableGeneric.capacity, cableGeneric.throughputIn, cableGeneric.throughputOut, ((int) ((double) energyNetwork.getEnergyStorage().getEnergyStored() / (double) energyNetwork.getEnergyStorage().getMaxEnergyStored() * cableGeneric.capacity))));
    }

    public void createNetwork(int capacity, int throughputIn, int throughputOut, int stored) {
        energyNetwork = new EnergyNetwork(this, capacity, throughputIn, throughputOut);
        energyNetwork.getEnergyStorage().setEnergyStored(stored);
    }

    public EnergyNetwork getEnergyNetwork() {
        return energyNetwork;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putInt("Energy", energyNetwork.getEnergyStorage().getEnergyStored());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        energyNetwork.getEnergyStorage().setEnergyStored(tag.getInt("Energy"));
        super.load(tag);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return LazyOptional.of(() -> energyNetwork.getEnergyStorage()).cast();
        }
        return super.getCapability(cap, side);
    }

    public CableGenericEntity(BlockPos blockPos, BlockState blockState, int capacity, int throughputIn, int throughputOut) {
        super(BlockEntityTypes.CABLE_BLOCK_ENTITY.get(), blockPos, blockState);
        this.capacity = capacity;
        this.throughputIn = throughputIn;
        this.throughputOut = throughputOut;
        this.energyNetwork = new EnergyNetwork(this, capacity, throughputIn, throughputOut);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("menu.weather2_additions.networkinfoscreen");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new NetworkInfoMenu(i, inventory, this, true);
    }
}
