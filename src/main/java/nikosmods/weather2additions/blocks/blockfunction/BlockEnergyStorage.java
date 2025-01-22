package nikosmods.weather2additions.blocks.blockfunction;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;

public class BlockEnergyStorage implements IEnergyStorage {

    private final int capacity;
    private final int throughputIn;
    private final int throughputOut;
    private int stored = 0;
    List<BlockEntity> blockEntity;

    public BlockEnergyStorage(BlockEntity blockEntity, int capacity, int throughputIn, int throughtputOut) {
        this.capacity = capacity;
        this.throughputIn = throughputIn;
        this.throughputOut = throughtputOut;
        this.blockEntity = List.of(blockEntity);
    }

    public BlockEnergyStorage(List<BlockEntity> blockEntity, int capacity, int throughputIn, int throughtputOut) {
        this.capacity = capacity;
        this.throughputIn = throughputIn;
        this.throughputOut = throughtputOut;
        this.blockEntity = blockEntity;
    }


    @Override
    public int receiveEnergy(int energy, boolean simulate) {
        int transferred = Math.min(energy, capacity-stored);
        transferred = Math.min(transferred, throughputIn);
        if (!simulate) {
            stored += transferred;
            blockEntity.forEach(BlockEntity::setChanged);
        }
        return transferred;
    }

    @Override
    public int extractEnergy(int energy, boolean simulate) {
        int transferred = Math.min(energy, stored);
        transferred = Math.min(transferred, throughputOut);
        if (!simulate) {
            stored -= transferred;
            blockEntity.forEach(BlockEntity::setChanged);
        }
        return transferred;
    }

    public int consumeEnergy(int energy, boolean simulate) {
        int transferred = Math.min(energy, stored);
        if (!simulate) {
            stored -= transferred;
            blockEntity.forEach(BlockEntity::setChanged);
        }
        return transferred;
    }

    public void setEnergyStored(int energy) {
        stored = energy;
    }

    @Override
    public int getEnergyStored() {
        return stored;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    public int getThroughputIn() {
        return throughputIn;
    }
    
    public int getThroughputOut() {
        return throughputOut;
    }

}
