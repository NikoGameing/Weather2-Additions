package nikosmods.weather2additions.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.io.IOException;
import java.util.function.Supplier;

public interface Packet {
    void encode(FriendlyByteBuf byteBuffer) throws IOException;
    void handle(Supplier<NetworkEvent.Context> supplier);
    void work();
}
