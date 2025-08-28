package nikosmods.weather2additions.blocks.blockfunction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import nikosmods.weather2additions.Weather2Additions;
import nikosmods.weather2additions.blocks.Connections;
import nikosmods.weather2additions.blocks.blockfunction.blockgui.NetworkInfoMenu;
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
        CableGenericEntity cableGeneric = (CableGenericEntity) blockEntity;
        cableGeneric.doTick(level, blockPos, state, blockEntity);
    }

    public abstract void doTick(Level level, BlockPos blockPos, BlockState state, BlockEntity blockEntity);

    public void connectToSide(Level level, BlockPos blockPos, BlockState state, BlockEntity blockEntity, BlockEntity blockAdjacent, EnumProperty<Connections> side, Direction sideDirectional) {
        CableGenericEntity cableEntity = (CableGenericEntity) blockEntity;
        if (blockAdjacent == null) {
            setState(state, level, state.setValue(side, Connections.NONE), blockPos);
        }
        else if (blockAdjacent instanceof CableGenericEntity genericEntity) {
            cableEntity.mergeNetworks(genericEntity.energyNetwork);
            setState(state, level, state.setValue(side, Connections.SIMILAR), blockPos);
        }
        else {
            LazyOptional<IEnergyStorage> potentialConsumer = blockAdjacent.getCapability(ForgeCapabilities.ENERGY, sideDirectional.getOpposite());
            potentialConsumer.ifPresent(consumer -> cableEntity.energyNetwork.getEnergyStorage().extractEnergy(consumer.receiveEnergy(cableEntity.energyNetwork.getEnergyStorage().extractEnergy(cableEntity.energyNetwork.getEnergyStorage().getThroughputOut(), true), false), false));
            if (!potentialConsumer.isPresent()) {
                setState(state, level, state.setValue(side, Connections.NONE), blockPos);
            }
            else {
                setState(state, level, state.setValue(side, Connections.CONNECTED), blockPos);
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

    public CableGenericEntity(BlockEntityType blockEntity, BlockPos blockPos, BlockState blockState, int capacity, int throughputIn, int throughputOut) {
        super(blockEntity, blockPos, blockState);
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
