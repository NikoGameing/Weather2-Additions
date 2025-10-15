package nikosmods.weather2additions.mapdata;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockMapData {
    public BlockPos blockPos;
    public byte[] image;
    public int mapX;
    public int mapY;
    public int imageWidth;
    public int imageHeight;
    public int resolution;
    public int radius;
    public BlockEntity blockEntity;

    public BlockMapData(BlockPos blockPos, BlockEntity blockEntity) {
        this.blockPos = blockPos;
        this.blockEntity = blockEntity;
    }

    public void update(byte[] image, int mapX, int mapY, int imageWidth, int imageHeight, int resolution, int radius) {
        this.image = image;
        this.mapX = mapX;
        this.mapY = mapY;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.resolution = resolution;
        this.radius = radius;
    }
}
