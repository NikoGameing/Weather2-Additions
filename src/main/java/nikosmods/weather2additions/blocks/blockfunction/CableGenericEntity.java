package nikosmods.weather2additions.blocks.blockfunction;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import nikosmods.weather2additions.blocks.blockentityreg.BlockEntityTypes;

public abstract class CableGenericEntity extends BlockEntity {

    EnergyNetwork energyNetwork = new EnergyNetwork(this, 2000, 2000, 2000);

    public static void tick(Level level, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {

    }

    public CableGenericEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityTypes.CABLE_BLOCK_ENTITY.get(), blockPos, blockState);
    }
}
