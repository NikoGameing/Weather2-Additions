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
import nikosmods.weather2additions.mapdata.Maps;
import nikosmods.weather2additions.mapdata.ServerMapRendering;

public class ScreenBlockEntity extends CableGenericEntity {

    private static byte[] previousMap;
    private static byte[] validatedMap;
    private static int refreshNumber;

    public byte[] getMapImageByteArray() {
        return validatedMap;
    }

    public ScreenBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(BlockEntityTypes.SCREEN_BLOCK_ENTITY.get(), p_155229_, p_155230_, 5000, 5000, 5000);
    }

    @Override
    public void doTick(Level level, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
        int refreshRate = Config.SCREEN_REFRESH_RATE.get();
        byte[] map = Maps.screenImage;
        int width = Maps.screenImageWidth;
        int height = Maps.screenImageHeight;
        int mapResolution = Maps.mapResolution;
        int mapX = Maps.mapX;
        int mapY = Maps.mapY;
        int mapRadius = Maps.tabletMapRadius;
        refreshNumber += 1;
        if (level.isClientSide()) {
            if (refreshNumber >= refreshRate && map != null && map != previousMap && (width != 0 || height != 0 || map.length != 0)) {
                validatedMap = map;
                refreshNumber = 0;
            }
            float offsetX = (((float) mapX - (float) blockPos.getX() - 0.5f) / mapResolution / mapRadius);
            float offsetY = -(((float) mapY - (float) blockPos.getZ() - 0.5f) / mapResolution / mapRadius);
            previousMap = map;
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

            }

            if (refreshNumber >= refreshRate) {
                ServerMapRendering.updateBlockWithImage(blockEntity, 0 , 0);
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
