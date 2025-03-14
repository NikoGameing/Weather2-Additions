package nikosmods.weather2additions.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import nikosmods.weather2additions.data.Maps;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.function.Supplier;

public class MapImagePacket implements Packet {
    private final byte[] map;
    private final int resolution;
    private final int radius;
    private final int x;
    private final int z;
    private final int width;
    private final int height;
    private final String ownership;


    public MapImagePacket(byte[] map, int resolution, int radius, int x, int z, int width, int height, String ownership) {
        this.resolution = resolution;
        this.radius = radius;
        this.x = x;
        this.z = z;
        this.width = width;
        this.height = height;
        this.map = map;
        this.ownership = ownership;
    }


    public MapImagePacket(FriendlyByteBuf byteBuffer) throws IOException {
        map = byteBuffer.readByteArray();
        x = byteBuffer.readInt();
        z = byteBuffer.readInt();
        width = byteBuffer.readInt();
        height = byteBuffer.readInt();
        resolution = byteBuffer.readInt();
        radius = byteBuffer.readInt();
        ownership = byteBuffer.readUtf();
    }

    public void encode(FriendlyByteBuf byteBuffer) throws IOException {
        byteBuffer.writeByteArray(map);
        byteBuffer.writeInt(x);
        byteBuffer.writeInt(z);
        byteBuffer.writeInt(width);
        byteBuffer.writeInt(height);
        byteBuffer.writeInt(resolution);
        byteBuffer.writeInt(radius);
        byteBuffer.writeUtf(ownership);
    }

    public void handle(Supplier <NetworkEvent.Context> supplier){
        supplier.get().enqueueWork(this::work);
    }
    public void work() {
        Maps.updateMap(map, null, x, z, width, height, resolution, radius, ownership);
    }
}
