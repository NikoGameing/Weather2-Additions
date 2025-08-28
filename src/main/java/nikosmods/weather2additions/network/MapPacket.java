package nikosmods.weather2additions.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import nikosmods.weather2additions.mapdata.MapOwners;
import nikosmods.weather2additions.mapdata.Maps;

import java.util.function.Supplier;

public class MapPacket implements Packet {
    private final int [] map;
    private final int resolution;
    private final int radius;
    private final int x;
    private final int z;
    private final MapOwners ownership;


    public MapPacket(int [] map, int resolution, int radius, int x, int z, MapOwners ownership) {
        this.resolution = resolution;
        this.radius = radius;
        this.x = x;
        this.z = z;
        this.map = map;
        this.ownership = ownership;
    }


    public MapPacket(FriendlyByteBuf byteBuffer) {
        map = byteBuffer.readVarIntArray();
        x = byteBuffer.readInt();
        z = byteBuffer.readInt();
        resolution = byteBuffer.readInt();
        radius = byteBuffer.readInt();
        ownership = byteBuffer.readEnum(MapOwners.class);
    }

    public void encode(FriendlyByteBuf byteBuffer) {
        byteBuffer.writeVarIntArray(map);
        byteBuffer.writeInt(x);
        byteBuffer.writeInt(z);
        byteBuffer.writeInt(resolution);
        byteBuffer.writeInt(radius);
        byteBuffer.writeEnum(ownership);
    }

    public void handle(Supplier <NetworkEvent.Context> supplier){
        supplier.get().enqueueWork(this::work);
    }
    public void work() {
        Maps.updateMap( null, map, x, z,  0, 0,resolution, radius, ownership);
    }
}
