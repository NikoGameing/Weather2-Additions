package nikosmods.weather2additions.network.packets.map;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import nikosmods.weather2additions.mapdata.BlockMapDataList;
import nikosmods.weather2additions.network.Packet;

import java.util.function.Supplier;

public class ScreenMapPacket implements Packet {
    private final byte[] image;
    private final int resolution;
    private final int radius;
    private final int x;
    private final int z;
    private final int width;
    private final int height;
    private final int blockPosX;
    private final int blockPosY;
    private final int blockPosZ;

    public ScreenMapPacket(byte[] image, int width, int height, int resolution, int radius, int mapX, int mapY, BlockPos blockPos) {
        this.resolution = resolution;
        this.radius = radius;
        this.x = mapX;
        this.z = mapY;
        this.width = width;
        this.height = height;
        this.image = image;
        this.blockPosX = blockPos.getX();
        this.blockPosY = blockPos.getY();
        this.blockPosZ = blockPos.getZ();
    }

    public ScreenMapPacket(FriendlyByteBuf byteBuffer) {
        resolution = byteBuffer.readInt();
        radius = byteBuffer.readInt();
        x = byteBuffer.readInt();
        z = byteBuffer.readInt();
        width = byteBuffer.readInt();
        height = byteBuffer.readInt();
        image = byteBuffer.readByteArray();
        blockPosX = byteBuffer.readInt();
        blockPosY = byteBuffer.readInt();
        blockPosZ = byteBuffer.readInt();
    }

    @Override
    public void encode(FriendlyByteBuf byteBuffer) {
        byteBuffer.writeInt(resolution);
        byteBuffer.writeInt(radius);
        byteBuffer.writeInt(x);
        byteBuffer.writeInt(z);
        byteBuffer.writeInt(width);
        byteBuffer.writeInt(height);
        byteBuffer.writeByteArray(image);
        byteBuffer.writeInt(blockPosX);
        byteBuffer.writeInt(blockPosY);
        byteBuffer.writeInt(blockPosZ);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(this::work);
    }

    @Override
    public void work() {
        BlockMapDataList.update(image, x, z, width, height, resolution, radius, new BlockPos(blockPosX, blockPosY, blockPosZ));
    }
}
