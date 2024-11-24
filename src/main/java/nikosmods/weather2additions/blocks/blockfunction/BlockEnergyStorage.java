package nikosmods.weather2additions.blocks.blockfunction;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;

public class BlockEnergyStorage implements IEnergyStorage, EnergyNetworkMerge {

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
    public int receiveEnergy(int i, boolean b) {
        int transferred = Math.min(i, capacity-stored);
        transferred = Math.min(transferred, throughputIn);
        if (!b) {
            stored += transferred;
            blockEntity.forEach(BlockEntity::setChanged);
        }
        return transferred;
    }

    @Override
    public int extractEnergy(int i, boolean b) {
        int transferred = Math.min(i, stored);
        transferred = Math.min(transferred, throughputOut);
        if (!b) {
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

    @Override
    public BlockEnergyStorage mergeNetworkEnergy(BlockEnergyStorage energyNetwork) {
        int capacity = this.capacity + energyNetwork.getMaxEnergyStored();
        int stored = this.stored + energyNetwork.getEnergyStored();
        BlockEnergyStorage blockEnergyStorage = new BlockEnergyStorage(blockEntity, capacity, throughputIn, throughputOut);
        blockEnergyStorage.setEnergyStored(stored);
        return blockEnergyStorage;
    }
}
