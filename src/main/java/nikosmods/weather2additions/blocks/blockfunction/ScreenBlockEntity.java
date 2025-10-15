package nikosmods.weather2additions.blocks.blockfunction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import nikosmods.weather2additions.Config;
import nikosmods.weather2additions.blocks.ScreenBlock;
import nikosmods.weather2additions.blocks.blockentityreg.BlockEntityTypes;
import nikosmods.weather2additions.blocks.blockreg.Blocks;
import nikosmods.weather2additions.mapdata.BlockMapDataList;
import nikosmods.weather2additions.mapdata.ServerMapRendering;

public class ScreenBlockEntity extends CableGenericEntity {

    private static int refreshNumber;
    float offsetX = 0;
    float offsetY = 0;

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    @Override
    public void onLoad() {
        assert level != null;
        if (level.isClientSide()) {
            BlockMapDataList.addBlock(getBlockPos());
        }
        super.onLoad();
    }

    public ScreenBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(BlockEntityTypes.SCREEN_BLOCK_ENTITY.get(), p_155229_, p_155230_, 5000, 5000, 5000);
    }

    @Override
    public void doTick(Level level, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
        int refreshRate = Config.SCREEN_REFRESH_RATE.get();
        refreshNumber += 1;
        if (level.isClientSide()) {
            if (refreshNumber >= refreshRate) {
                refreshNumber = 0;
            }
        }
        if (!level.isClientSide()) {
            BlockEntity above = level.getBlockEntity(blockPos.above());
            BlockEntity below = level.getBlockEntity(blockPos.below());
            BlockEntity left = level.getBlockEntity(blockPos.relative(state.getValue(ScreenBlock.FACING).getCounterClockWise()));
            BlockEntity right = level.getBlockEntity(blockPos.relative(state.getValue(ScreenBlock.FACING).getClockWise()));
            connectToSide(level, blockPos, state, blockEntity, above, ScreenBlock.UP, Direction.UP);
            connectToSide(level, blockPos, state, blockEntity, below, ScreenBlock.DOWN, Direction.DOWN);
            if (left != null && left.getBlockState().getBlock() == Blocks.SCREEN_BLOCK.get()) {
                connectToSide(level, blockPos, state, blockEntity, left, ScreenBlock.LEFT, left.getBlockState().getValue(ScreenBlock.FACING));
            }
            else {
                setState(state, level, state.setValue(ScreenBlock.LEFT, false), blockPos);
            }
            if (right != null && right.getBlockState().getBlock() == Blocks.SCREEN_BLOCK.get()) {
                connectToSide(level, blockPos, state, blockEntity, right, ScreenBlock.RIGHT, right.getBlockState().getValue(ScreenBlock.FACING));
            }
            else {
                setState(state, level, state.setValue(ScreenBlock.RIGHT, false), blockPos);
            }

            if (!state.getValue(ScreenBlock.UP) && !state.getValue(ScreenBlock.DOWN) && !state.getValue(ScreenBlock.LEFT) && !state.getValue(ScreenBlock.RIGHT)) {
                // idk if something was meant to go here i'll remember later maybe?
            }

            if (refreshNumber >= refreshRate) {
                ServerMapRendering.updateBlockWithImage(blockEntity, 0, 0);
                refreshNumber = 0;
            }
        }
    }

    public void connectToSide(Level level, BlockPos blockPos, BlockState state, BlockEntity blockEntity, BlockEntity blockAdjacent, BooleanProperty side, Direction sideDirectional) {
        CableGenericEntity cableEntity = (CableGenericEntity) blockEntity;
        if (blockAdjacent == null) {
            setState(state, level, state.setValue(side, false), blockPos);
        }
        else if (blockAdjacent instanceof ScreenBlockEntity screenEntity) {
            cableEntity.mergeNetworks(screenEntity.energyNetwork);
            setState(state, level, state.setValue(side, true), blockPos);
        }
        else {
            LazyOptional<IEnergyStorage> potentialConsumer = blockAdjacent.getCapability(ForgeCapabilities.ENERGY, sideDirectional.getOpposite());
            potentialConsumer.ifPresent(consumer -> cableEntity.energyNetwork.getEnergyStorage().extractEnergy(consumer.receiveEnergy(cableEntity.energyNetwork.getEnergyStorage().extractEnergy(cableEntity.energyNetwork.getEnergyStorage().getThroughputOut(), true), false), false));
            if (!potentialConsumer.isPresent()) {
                setState(state, level, state.setValue(side, false), blockPos);
            }
            else {
                setState(state, level, state.setValue(side, false), blockPos);
            }
        }
    }
}
