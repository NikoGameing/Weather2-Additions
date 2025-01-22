package nikosmods.weather2additions.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import nikosmods.weather2additions.data.Maps;

import java.util.function.Supplier;

public class MapPacket {
    private final int [] map;
    private final int resolution;
    private final int x;
    private final int z;
    private final String ownership;


    public MapPacket(int [] map, int resolution, int x, int z, String ownership) {
        this.resolution = resolution;
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
        ownership = byteBuffer.readUtf();
    }
    public void encode(FriendlyByteBuf byteBuffer) {
        byteBuffer.writeVarIntArray(map);
        byteBuffer.writeInt(x);
        byteBuffer.writeInt(z);
        byteBuffer.writeInt(resolution);
        byteBuffer.writeUtf(ownership);
    }
    public void handle(Supplier <NetworkEvent.Context> supplier){
        supplier.get().enqueueWork(this::work);
    }
    public void work() {
        Maps.updateMap(map, x, z, resolution, ownership);
    }
}
