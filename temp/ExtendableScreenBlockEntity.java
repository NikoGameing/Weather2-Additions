package nikosmods.weather2additions.blocks.blockfunction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import nikosmods.weather2additions.blocks.ExtendableScreenBlock;

public class ExtendableScreenBlockEntity extends BlockEntity {
    public ExtendableScreenBlockEntity(BlockEntityType<?> blockType, BlockPos blockPos, BlockState blockState) {
        super(blockType, blockPos, blockState);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
        if (!level.isClientSide()){
            BlockEntity above = level.getBlockEntity(blockPos.above());
            BlockEntity below = level.getBlockEntity(blockPos.below());
            BlockEntity left = level.getBlockEntity(state.getValue());
            BlockEntity right = level.getBlockEntity(blockPos.east());
            connectToSide(level, blockPos, state, blockEntity, above, ExtendableScreenBlock.ABOVE, Direction.UP);
            connectToSide(level, blockPos, state, blockEntity, below, ExtendableScreenBlock.BELOW, Direction.DOWN);
        }
    }

    public static void connectToSide(Level level, BlockPos blockPos, BlockState state, BlockEntity blockEntity, BlockEntity blockAdjacent, EnumProperty<ExtendableScreenBlock.SideConnections> side, Direction sideDirectional) {
        ExtendableScreenBlockEntity screenBlock = (ExtendableScreenBlockEntity) blockEntity;

    }

    public static void setState(BlockState previousState, Level level, BlockState newState, BlockPos blockPos) {
        if (previousState != newState) {
            level.setBlock(blockPos, newState, 2);
        }
    }

}
