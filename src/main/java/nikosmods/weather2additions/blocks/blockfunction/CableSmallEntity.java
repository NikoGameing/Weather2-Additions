package nikosmods.weather2additions.blocks.blockfunction;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CableSmallEntity extends CableGenericEntity {

    public CableSmallEntity(BlockPos blockPos, BlockState blockState, int capacity, int throughputIn, int throughputOut) {
        super(blockPos, blockState, capacity, throughputIn, throughputOut);
    }
}
