package nikosmods.weather2additions.blocks.blockfunction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import nikosmods.weather2additions.blocks.CableSmall;
import nikosmods.weather2additions.blocks.blockentityreg.BlockEntityTypes;

public class CableSmallEntity extends CableGenericEntity {

    public CableSmallEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityTypes.CABLE_BLOCK_ENTITY.get(), blockPos, blockState, 2000, 2000, 2000);
    }

    @Override
    public void doTick(Level level, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
        if (!level.isClientSide()) {
            BlockEntity above = level.getBlockEntity(blockPos.above());
            BlockEntity below = level.getBlockEntity(blockPos.below());
            BlockEntity north = level.getBlockEntity(blockPos.north());
            BlockEntity east = level.getBlockEntity(blockPos.east());
            BlockEntity south = level.getBlockEntity(blockPos.south());
            BlockEntity west = level.getBlockEntity(blockPos.west());
            connectToSide(level, blockPos, state, blockEntity, above, CableSmall.UP, Direction.UP);
            connectToSide(level, blockPos, state, blockEntity, below, CableSmall.DOWN, Direction.DOWN);
            connectToSide(level, blockPos, state, blockEntity, north, CableSmall.NORTH, Direction.NORTH);
            connectToSide(level, blockPos, state, blockEntity, east, CableSmall.EAST, Direction.EAST);
            connectToSide(level, blockPos, state, blockEntity, south, CableSmall.SOUTH, Direction.SOUTH);
            connectToSide(level, blockPos, state, blockEntity, west, CableSmall.WEST, Direction.WEST);
        }
    }

}
