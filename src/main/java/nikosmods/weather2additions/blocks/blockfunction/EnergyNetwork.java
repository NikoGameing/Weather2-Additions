package nikosmods.weather2additions.blocks.blockfunction;

import com.mojang.logging.LogUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import nikosmods.weather2additions.Weather2Additions;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class EnergyNetwork {

    Logger logger = Weather2Additions.LOGGER;

    public List<CableGenericEntity> cableEntities = new ArrayList<>();
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

    public void removeCableEntity(CableGenericEntity cableEntity) {
        cableEntities.remove(cableEntity);
    }

    public BlockEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public EnergyNetwork(BlockEntity cableEntity, int capacity, int throughputIn, int throughputOut) {
        cableEntities.add((CableGenericEntity) cableEntity);
        energyStorage = new BlockEnergyStorage(cableEntity, capacity, throughputIn, throughputOut);
    }
}
