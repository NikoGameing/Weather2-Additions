package nikosmods.weather2additions.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import nikosmods.weather2additions.blocks.blockfunction.CableSmallEntity;
import org.jetbrains.annotations.Nullable;

public class CableSmall extends Block implements EntityBlock {

    public CableSmall(Properties p_49795_) {
        super(p_49795_);
    }



    @Nullable

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CableSmallEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        return CableSmallEntity::tick;
    }
}
