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

    private int refreshNumber;
    public CollectiveScreen collectiveScreen;
    public float offsetX = 0;
    public float offsetY = 0;


    @Override
    public void onLoad() {
        assert level != null;
        if (level.isClientSide()) {
            BlockMapDataList.addBlock(getBlockPos());
        }
        if (!level.isClientSide()) {
            collectiveScreen = new CollectiveScreen(this);
            BlockEntity above = level.getBlockEntity(getBlockPos().above());
            BlockEntity below = level.getBlockEntity(getBlockPos().below());
            BlockEntity left = level.getBlockEntity(getBlockPos().relative(getBlockState().getValue(ScreenBlock.FACING).getCounterClockWise()));
            BlockEntity right = level.getBlockEntity(getBlockPos().relative(getBlockState().getValue(ScreenBlock.FACING).getClockWise()));
            connectToSide(level, getBlockPos(), getBlockState(), this, above, ScreenBlock.UP, Direction.UP);
            connectToSide(level, getBlockPos(), getBlockState(), this, below, ScreenBlock.DOWN, Direction.DOWN);
            if (left != null && left.getBlockState().getBlock() == Blocks.SCREEN_BLOCK.get()) {
                connectToSide(level, getBlockPos(), getBlockState(), this, left, ScreenBlock.LEFT, left.getBlockState().getValue(ScreenBlock.FACING));
            }
            else {
                setState(getBlockState(), level, getBlockState().setValue(ScreenBlock.LEFT, false), getBlockPos());
            }
            if (right != null && right.getBlockState().getBlock() == Blocks.SCREEN_BLOCK.get()) {
                connectToSide(level, getBlockPos(), getBlockState(), this, right, ScreenBlock.RIGHT, right.getBlockState().getValue(ScreenBlock.FACING));
            }
            else {
                setState(getBlockState(), level, getBlockState().setValue(ScreenBlock.RIGHT, false), getBlockPos());
            }
            if (getBlockState().getValue(ScreenBlock.LEFT) && left != null && ((ScreenBlockEntity) left).collectiveScreen != null) {
                ((ScreenBlockEntity) left).collectiveScreen.addScreenBlock(this);
                collectiveScreen = ((ScreenBlockEntity) left).collectiveScreen;
            }
            else if (getBlockState().getValue(ScreenBlock.RIGHT) && right != null && ((ScreenBlockEntity) right).collectiveScreen != null) {
                ((ScreenBlockEntity) right).collectiveScreen.addScreenBlock(this);
                collectiveScreen = ((ScreenBlockEntity) right).collectiveScreen;
            }
            else if (getBlockState().getValue(ScreenBlock.UP) && above != null && ((ScreenBlockEntity) above).collectiveScreen != null) {
                ((ScreenBlockEntity) above).collectiveScreen.addScreenBlock(this);
                collectiveScreen = ((ScreenBlockEntity) above).collectiveScreen;
            }
            else if (getBlockState().getValue(ScreenBlock.DOWN) && below != null && ((ScreenBlockEntity) below).collectiveScreen != null) {
                ((ScreenBlockEntity) below).collectiveScreen.addScreenBlock(this);
                collectiveScreen = ((ScreenBlockEntity) below).collectiveScreen;
            }
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

            if (refreshNumber >= refreshRate) {
                ServerMapRendering.updateBlockWithImage(blockEntity, offsetX, offsetY);
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
