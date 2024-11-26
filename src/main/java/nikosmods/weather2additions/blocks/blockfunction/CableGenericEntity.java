package nikosmods.weather2additions.blocks.blockfunction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import nikosmods.weather2additions.blocks.blockentityreg.BlockEntityTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CableGenericEntity extends BlockEntity {

    public int capacity;
    public int throughputIn;
    public int throughputOut;

    public void setVariables(int capacity, int throughputIn, int throughputOut) {
        this.capacity = capacity;
        this.throughputIn = throughputIn;
        this.throughputOut = throughputOut;
    }

    EnergyNetwork energyNetwork = new EnergyNetwork(this, capacity, throughputIn, throughputOut);

    public void setCableEntities() {
        energyNetwork.setCableEntities(this);
    }


    public static void tick(Level level, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {

    }

    public void mergeNetworks(EnergyNetwork otherNetwork) {
        int stored = energyNetwork.getEnergyStored() + otherNetwork.getEnergyStored();
        int capacity = energyNetwork.getMaxEnergyStored() + otherNetwork.getMaxEnergyStored();
        EnergyNetwork combinedNetwork = new EnergyNetwork(this, capacity, throughputIn, throughputOut);
        combinedNetwork.setEnergyStored(stored);
        combinedNetwork.setCableEntities(energyNetwork.getCableEntities());
        energyNetwork = combinedNetwork;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putInt("Energy", energyNetwork.getEnergyStored());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        energyNetwork.setEnergyStored(tag.getInt("Energy"));
        super.load(tag);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return LazyOptional.of(() -> energyNetwork).cast();
        }
        return super.getCapability(cap, side);
    }

    public CableGenericEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityTypes.CABLE_BLOCK_ENTITY.get(), blockPos, blockState);
    }
}
