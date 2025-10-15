package nikosmods.weather2additions.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import nikosmods.weather2additions.Weather2Additions;
import nikosmods.weather2additions.network.packets.energy.AnalyserPacket;
import nikosmods.weather2additions.network.packets.energy.EnergyPacket;
import nikosmods.weather2additions.network.packets.map.MapImagePacket;
import nikosmods.weather2additions.network.packets.map.MapPacket;
import nikosmods.weather2additions.network.packets.map.ScreenMapPacket;
import nikosmods.weather2additions.network.packets.map.TabletMapPacket;

public class Messages {
    private static final SimpleChannel channel = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(Weather2Additions.MODID, "channel")).networkProtocolVersion(() -> "hot dog").clientAcceptedVersions(version -> true).serverAcceptedVersions(version -> true).simpleChannel();  // im only going to make this line longer with random comments that don't contribute to anything because its funny and silly
    private static int Id = 0;
    public static void register(FMLCommonSetupEvent setupEvent) {
        // Server -> Player
        channel.messageBuilder(MapPacket.class, Id ++, NetworkDirection.PLAY_TO_CLIENT).decoder(MapPacket::new).encoder(MapPacket::encode).consumerMainThread(MapPacket::handle).add();
        channel.messageBuilder(AnalyserPacket.class, Id ++, NetworkDirection.PLAY_TO_CLIENT).decoder(AnalyserPacket::new).encoder(AnalyserPacket::encode).consumerMainThread(AnalyserPacket::handle).add();
        channel.messageBuilder(EnergyPacket.class, Id ++, NetworkDirection.PLAY_TO_CLIENT).decoder(EnergyPacket::new).encoder(EnergyPacket::encode).consumerMainThread(EnergyPacket::handle).add();
        channel.messageBuilder(MapImagePacket.class, Id ++, NetworkDirection.PLAY_TO_CLIENT).decoder(MapImagePacket::new).encoder(MapImagePacket::encode).consumerMainThread(MapImagePacket::handle).add();
        channel.messageBuilder(ScreenMapPacket.class, Id ++, NetworkDirection.PLAY_TO_CLIENT).decoder(ScreenMapPacket::new).encoder(ScreenMapPacket::encode).consumerMainThread(ScreenMapPacket::handle).add();
        channel.messageBuilder(TabletMapPacket.class, Id ++, NetworkDirection.PLAY_TO_CLIENT).decoder(TabletMapPacket::new).encoder(TabletMapPacket::encode).consumerMainThread(TabletMapPacket::handle).add();
        // Player -> Server
    }
    public static <MSG> void sendToServer(MSG message) {
        channel.sendToServer(message);
    }
    public static <MSG> void sendToClient(MSG message, ServerPlayer player) {
        channel.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
    public static <MSG> void sendToPlayersTrackingChunk(MSG message, LevelChunk chunk) {
        channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), message);
    }
}
