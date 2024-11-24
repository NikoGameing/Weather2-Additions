package nikosmods.weather2additions.blocks.blockfunction;

import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class EnergyNetwork {

    public List<CableGenericEntity> cableEntities;

    public EnergyNetwork(BlockEntity cableEntity, int capacity, int throughputIn, int throughputOut) {
        BlockEnergyStorage energyStorage = new BlockEnergyStorage(cableEntity, capacity, throughputIn, throughputOut);
    }
}
