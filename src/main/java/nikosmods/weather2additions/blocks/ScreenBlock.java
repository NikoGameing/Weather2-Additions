package nikosmods.weather2additions.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import nikosmods.weather2additions.blocks.blockfunction.ScreenBlockEntity;
import nikosmods.weather2additions.mapdata.BlockMapDataList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScreenBlock extends Block implements EntityBlock {

    public static BooleanProperty UP = BooleanProperty.create("up");
    public static BooleanProperty DOWN = BooleanProperty.create("down");
    public static BooleanProperty LEFT = BooleanProperty.create("left");
    public static BooleanProperty RIGHT = BooleanProperty.create("right");

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public ScreenBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(UP, false)
                        .setValue(DOWN, false)
                        .setValue(LEFT, false)
                        .setValue(RIGHT, false)
                        .setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    public @NotNull MapColor defaultMapColor() {
        return MapColor.COLOR_GRAY;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ScreenBlockEntity(blockPos, blockState);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> entityType) {
        return ScreenBlockEntity::tick;
    }


    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public void onRemove(@NotNull BlockState state, Level level, @NotNull BlockPos blockPos, @NotNull BlockState futureState, boolean p_60519_) {
        if (state != futureState && state.getBlock() != futureState.getBlock()) {
            if (!level.isClientSide()) {
                ScreenBlockEntity blockEntity = (ScreenBlockEntity) level.getBlockEntity(blockPos);
                assert blockEntity != null;
                blockEntity.collectiveScreen.removeScreenBlock(blockEntity);
            } else {
                BlockMapDataList.removeBlock(blockPos);
            }
        }
        super.onRemove(state, level, blockPos, futureState, p_60519_);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        p_49915_.add(UP, DOWN, LEFT, RIGHT, FACING);
    }
}
