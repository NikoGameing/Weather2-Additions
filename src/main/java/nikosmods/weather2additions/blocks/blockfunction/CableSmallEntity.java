package nikosmods.weather2additions.blocks.blockfunction;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CableSmallEntity extends CableGenericEntity {

    @Override
    public void setVariables(int capacity, int throughputIn, int throughputOut) {
        super.setVariables(2000, 2000, 2000);
    }

    public CableSmallEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
    }
}
