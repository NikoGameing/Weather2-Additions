package nikosmods.weather2additions.blocks.blockfunction;

import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class EnergyNetwork {

    public List<CableGenericEntity> cableEntities;
    public BlockEnergyStorage energyStorage;

    public List<CableGenericEntity> getCableEntities() {
        return cableEntities;
    }

    public void setCableEntities(CableGenericEntity cableEntity) {
        cableEntities.add(cableEntity);
    }

    public void setCableEntities(List<CableGenericEntity> cableEntities) {
        this.cableEntities.addAll(cableEntities);
    }

    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    public void setEnergyStored(int stored) {
        energyStorage.setEnergyStored(stored);
    }

    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }

    public EnergyNetwork(BlockEntity cableEntity, int capacity, int throughputIn, int throughputOut) {
        energyStorage = new BlockEnergyStorage(cableEntity, capacity, throughputIn, throughputOut);
    }

}
