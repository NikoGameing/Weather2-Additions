package nikosmods.weather2additions.network.packets.map;

import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import nikosmods.weather2additions.mapdata.TabletMapData;
import nikosmods.weather2additions.network.Packet;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class TabletMapPacket implements Packet {
    private final byte[] image;
    private final int resolution;
    private final int radius;
    private final int x;
    private final int z;
    private final int width;
    private final int height;

    public TabletMapPacket(byte[] image, int width, int height, int resolution, int radius, int mapX, int mapY) {
        this.resolution = resolution;
        this.radius = radius;
        this.x = mapX;
        this.z = mapY;
        this.width = width;
        this.height = height;
        this.image = image;
    }

    public TabletMapPacket(FriendlyByteBuf byteBuffer) {
        resolution = byteBuffer.readInt();
        radius = byteBuffer.readInt();
        x = byteBuffer.readInt();
        z = byteBuffer.readInt();
        width = byteBuffer.readInt();
        height = byteBuffer.readInt();
        image = byteBuffer.readByteArray();

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
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(this::work);
    }

    @Override
    public void work() {
        TabletMapData.update(image, width, height, resolution, radius, x, z);
    }
}
