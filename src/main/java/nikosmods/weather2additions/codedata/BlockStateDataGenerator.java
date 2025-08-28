package nikosmods.weather2additions.codedata;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import nikosmods.weather2additions.Weather2Additions;
import nikosmods.weather2additions.blocks.ScreenBlock;
import nikosmods.weather2additions.blocks.blockreg.Blocks;

public class BlockStateDataGenerator extends BlockStateProvider {
    public BlockStateDataGenerator(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Weather2Additions.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        screenBlock(Blocks.SCREEN_BLOCK.get(), new ResourceLocation(Weather2Additions.MODID, "block/blockstate/screen_block"));
    }

    private void screenBlock(Block block, ResourceLocation resourceLocation) {
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block)
                .part()
                .modelFile(models().getExistingFile(resourceLocation.withSuffix("/screenedge")))
                .addModel()
                .end();
        Direction.Plane.HORIZONTAL.forEach(facing -> {
            screenBlockBack(builder, resourceLocation, facing);
            screenBlockOuterEdges(builder, resourceLocation, facing);
            screenBlockOuterCorners(builder, resourceLocation, facing);
            screenBlockInnerEdges(builder, resourceLocation, facing);
            screenBlockInnerCorners(builder, resourceLocation, facing);
        });
    }

    private void screenBlockBack(MultiPartBlockStateBuilder builder, ResourceLocation resourceLocation, Direction facing) {
        builder
                .part()
                .modelFile(models().getExistingFile(resourceLocation.withSuffix("/screenouterback")))
                .rotationY((int) facing.toYRot())
                .addModel()
                .condition(ScreenBlock.FACING, facing)
                .end();
    }

    private void screenBlockOuterEdges(MultiPartBlockStateBuilder builder, ResourceLocation resourceLocation, Direction facing) {
        builder
                .part()
                .modelFile(models().getExistingFile(resourceLocation.withSuffix("/screenouter")))
                .rotationY((int) facing.toYRot())
                .addModel()
                .condition(ScreenBlock.FACING, facing)
                .condition(ScreenBlock.DOWN, false)
                .end()
                .part()
                .modelFile(models().getExistingFile(resourceLocation.withSuffix("/screenouterleft")))
                .rotationY((int) facing.toYRot())
                .addModel()
                .condition(ScreenBlock.FACING, facing)
                .condition(ScreenBlock.LEFT, false)
                .end()
                .part()
                .modelFile(models().getExistingFile(resourceLocation.withSuffix("/screenoutertop")))
                .rotationY((int) facing.toYRot())
                .addModel()
                .condition(ScreenBlock.FACING, facing)
                .condition(ScreenBlock.UP, false)
                .end()
                .part()
                .modelFile(models().getExistingFile(resourceLocation.withSuffix("/screenouterright")))
                .rotationY((int) facing.toYRot())
                .addModel()
                .condition(ScreenBlock.FACING, facing)
                .condition(ScreenBlock.RIGHT, false)
                .end();
    }

    private void screenBlockOuterCorners(MultiPartBlockStateBuilder builder, ResourceLocation resourceLocation, Direction facing) {
        builder
                .part()
                .modelFile(models().getExistingFile(resourceLocation.withSuffix("/screenoutercorner")))
                .rotationY((int) facing.toYRot())
                .addModel()
                .nestedGroup()
                .condition(ScreenBlock.FACING, facing)
                .end()
                .nestedGroup()
                .useOr()
                .condition(ScreenBlock.DOWN, false)
                .condition(ScreenBlock.LEFT, false)
                .end()
                .end()
                .part()
                .modelFile(models().getExistingFile(resourceLocation.withSuffix("/screenoutercornerul")))
                .rotationY((int) facing.toYRot())
                .addModel()
                .nestedGroup()
                .condition(ScreenBlock.FACING, facing)
                .end()
                .nestedGroup()
                .useOr()
                .condition(ScreenBlock.LEFT, false)
                .condition(ScreenBlock.UP, false)
                .end()
                .end()
                .part()
                .modelFile(models().getExistingFile(resourceLocation.withSuffix("/screenoutercornerur")))
                .rotationY((int) facing.toYRot())
                .addModel()
                .nestedGroup()
                .condition(ScreenBlock.FACING, facing)
                .end()
                .nestedGroup()
                .useOr()
                .condition(ScreenBlock.UP, false)
                .condition(ScreenBlock.RIGHT, false)
                .end()
                .end()
                .part()
                .modelFile(models().getExistingFile(resourceLocation.withSuffix("/screenoutercornerbr")))
                .rotationY((int) facing.toYRot())
                .addModel()
                .nestedGroup()
                .condition(ScreenBlock.FACING, facing)
                .end()
                .nestedGroup()
                .useOr()
                .condition(ScreenBlock.RIGHT, false)
                .condition(ScreenBlock.DOWN, false)
                .end()
                .end();
    }

    private void screenBlockInnerEdges(MultiPartBlockStateBuilder builder, ResourceLocation resourceLocation, Direction facing) {
        builder
                .part()
                .modelFile(models().getExistingFile(resourceLocation.withSuffix("/screenjoin")))
                .rotationY((int) facing.toYRot())
                .addModel()
                .condition(ScreenBlock.FACING, facing)
                .condition(ScreenBlock.DOWN, true)
                .end()
                .part()
                .modelFile(models().getExistingFile(resourceLocation.withSuffix("/screenjoinleft")))
                .rotationY((int) facing.toYRot())
                .addModel()
                .condition(ScreenBlock.FACING, facing)
                .condition(ScreenBlock.LEFT, true)
                .end()
                .part()
                .modelFile(models().getExistingFile(resourceLocation.withSuffix("/screenjointop")))
                .rotationY((int) facing.toYRot())
                .addModel()
                .condition(ScreenBlock.FACING, facing)
                .condition(ScreenBlock.UP, true)
                .end()
                .part()
                .modelFile(models().getExistingFile(resourceLocation.withSuffix("/screenjoinright")))
                .rotationY((int) facing.toYRot())
                .addModel()
                .condition(ScreenBlock.FACING, facing)
                .condition(ScreenBlock.RIGHT, true)
                .end();
    }

    private void screenBlockInnerCorners(MultiPartBlockStateBuilder builder, ResourceLocation resourceLocation, Direction facing) {
        builder
                .part()
                .modelFile(models().getExistingFile(resourceLocation.withSuffix("/screencornerjoin")))
                .rotationY((int) facing.toYRot())
                .addModel()
                .condition(ScreenBlock.FACING, facing)
                .condition(ScreenBlock.DOWN, true)
                .condition(ScreenBlock.LEFT, true)
                .end()
                .part()
                .modelFile(models().getExistingFile(resourceLocation.withSuffix("/screencornerjoinul")))
                .rotationY((int) facing.toYRot())
                .addModel()
                .condition(ScreenBlock.FACING, facing)
                .condition(ScreenBlock.LEFT, true)
                .condition(ScreenBlock.UP, true)
                .end()
                .part()
                .modelFile(models().getExistingFile(resourceLocation.withSuffix("/screencornerjoinur")))
                .rotationY((int) facing.toYRot())
                .addModel()
                .condition(ScreenBlock.FACING, facing)
                .condition(ScreenBlock.UP, true)
                .condition(ScreenBlock.RIGHT, true)
                .end()
                .part()
                .modelFile(models().getExistingFile(resourceLocation.withSuffix("/screencornerjoinbr")))
                .rotationY((int) facing.toYRot())
                .addModel()
                .condition(ScreenBlock.FACING, facing)
                .condition(ScreenBlock.RIGHT, true)
                .condition(ScreenBlock.DOWN, true)
                .end();
    }
}