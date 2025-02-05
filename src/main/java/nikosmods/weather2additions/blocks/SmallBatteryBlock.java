package nikosmods.weather2additions.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import nikosmods.weather2additions.blocks.blockentityreg.BlockEntityTypes;
import nikosmods.weather2additions.blocks.blockfunction.SmallBatteryBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmallBatteryBlock extends Block implements EntityBlock {

    public SmallBatteryBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SmallBatteryBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        return SmallBatteryBlockEntity::tick;
    }

    @Override
    public @NotNull MapColor defaultMapColor() {
        return MapColor.COLOR_GRAY;
    }

    @Override
    public @NotNull InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            NetworkHooks.openScreen((ServerPlayer) player, (SmallBatteryBlockEntity) level.getBlockEntity(blockPos), blockPos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos blockPos, BlockState futureState, boolean isMoving) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (state.getBlock() != futureState.getBlock()) {
                if (blockEntity instanceof SmallBatteryBlockEntity batteryBlockEntity) {
                    ItemStackHandler inventory = batteryBlockEntity.getStackHandler();
                    for (int index = 0; index < inventory.getSlots(); index++) {
                        ItemStack item = inventory.getStackInSlot(index);
                        ItemEntity droppedItem = new ItemEntity(level, blockPos.getX() + 0.5D, blockPos.getY() + 0.5D, blockPos.getZ() + 0.5D, item);
                        level.addFreshEntity(droppedItem);
                    }
                }
            }
        }
        super.onRemove(state, level, blockPos, futureState, isMoving);
    }
}
