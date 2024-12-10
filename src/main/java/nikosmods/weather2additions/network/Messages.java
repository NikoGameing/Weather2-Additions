package nikosmods.weather2additions.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import nikosmods.weather2additions.Weather2Additions;

public class Messages {
    private static final SimpleChannel channel = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(Weather2Additions.MODID, "channel")).networkProtocolVersion(() -> "hot dog").clientAcceptedVersions(version -> true).serverAcceptedVersions(version -> true).simpleChannel();  // im only going to make this line longer with random comments that don't contribute to anything because its funny and silly
    private static int Id = 0;
    public static void register(FMLCommonSetupEvent setupEvent) {
        channel.messageBuilder(TabletPacket.class, Id ++, NetworkDirection.PLAY_TO_CLIENT).decoder(TabletPacket::new).encoder(TabletPacket::encode).consumerMainThread(TabletPacket::handle).add();
        channel.messageBuilder(AnalyserPacket.class, Id ++, NetworkDirection.PLAY_TO_CLIENT).decoder(AnalyserPacket::new).encoder(AnalyserPacket::encode).consumerMainThread(AnalyserPacket::handle).add();
    }
    public static <MSG> void sendToServer(MSG message) {
        channel.sendToServer(message);
    }
    public static <MSG> void sendToClient(MSG message, ServerPlayer player) {
        channel.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
