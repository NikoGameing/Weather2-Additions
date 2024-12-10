package nikosmods.weather2additions.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import nikosmods.weather2additions.blocks.blockfunction.blockgui.NetworkInfoMenu;

import java.util.function.Supplier;

public class AnalyserPacket {
    private final int capacity;
    private final int throughput;
    private final int maxCapacity;
    private final int cableNumber;


    public AnalyserPacket(int capacity, int throughput, int maxCapacity, int cableNumber) {
        this.throughput = throughput;
        this.maxCapacity = maxCapacity;
        this.cableNumber = cableNumber;
        this.capacity = capacity;
    }


    public AnalyserPacket(FriendlyByteBuf byteBuffer) {
        capacity = byteBuffer.readInt();
        maxCapacity = byteBuffer.readInt();
        cableNumber = byteBuffer.readInt();
        throughput = byteBuffer.readInt();
    }
    public void encode(FriendlyByteBuf byteBuffer) {
        byteBuffer.writeInt(capacity);
        byteBuffer.writeInt(maxCapacity);
        byteBuffer.writeInt(cableNumber);
        byteBuffer.writeInt(throughput);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier){
        supplier.get().enqueueWork(this::work);
    }

    public void work() {
        NetworkInfoMenu.setAll(capacity, throughput, maxCapacity, cableNumber);
    }
}
