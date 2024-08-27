package nikosmods.weather2additions.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import nikosmods.weather2additions.items.itemfunction.TabletMapRendering;

import java.util.function.Supplier;

public class TabletPacket {
    private int [] map;
    private int resolution;
    private int x;
    private int z;


    public TabletPacket(int [] map, int resolution, int x, int z) {
        this.resolution = resolution;
        this.x = x;
        this.z = z;
        this.map = map;
    }


    public TabletPacket(FriendlyByteBuf byteBuffer) {
        map = byteBuffer.readVarIntArray();
        x = byteBuffer.readInt();
        z = byteBuffer.readInt();
        resolution = byteBuffer.readInt();
    }
    public void encode(FriendlyByteBuf byteBuffer) {
        byteBuffer.writeVarIntArray(map);
        byteBuffer.writeInt(x);
        byteBuffer.writeInt(z);
        byteBuffer.writeInt(resolution);
    }
    public void handle(Supplier <NetworkEvent.Context> supplier){
        supplier.get().enqueueWork(this::work);
    }
    public void work() {
        TabletMapRendering.updateMap(map, x, z, resolution);
    }
}
