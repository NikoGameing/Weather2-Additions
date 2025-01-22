package nikosmods.weather2additions.blocks.blockfunction;

import net.minecraftforge.energy.IEnergyStorage;

public class BlockEnergySides implements IEnergyStorage {

    private final BlockEnergyStorage blockEnergyStorage;
    private final boolean extracting;
    private final boolean receiving;

    public BlockEnergySides(BlockEnergyStorage blockEnergyStorage, boolean receiving, boolean extracting) {
        this.blockEnergyStorage = blockEnergyStorage;
        this.extracting = extracting;
        this.receiving = receiving;
    }

    @Override
    public int receiveEnergy(int i, boolean b) {
        if (!receiving) {
            return 0;
        }
        return blockEnergyStorage.receiveEnergy(i, b);
    }

    @Override
    public int extractEnergy(int i, boolean b) {
        if (!extracting) {
            return 0;
        }
        return blockEnergyStorage.extractEnergy(i, b);
    }

    @Override
    public int getEnergyStored() {
        return blockEnergyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return blockEnergyStorage.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return extracting;
    }

    @Override
    public boolean canReceive() {
        return receiving;
    }
}