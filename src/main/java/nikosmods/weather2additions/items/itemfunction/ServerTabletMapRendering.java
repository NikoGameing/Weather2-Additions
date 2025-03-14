package nikosmods.weather2additions.items.itemfunction;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import nikosmods.weather2additions.Weather2Additions;
import nikosmods.weather2additions.data.Maps;
import nikosmods.weather2additions.network.MapImagePacket;
import nikosmods.weather2additions.network.Messages;
import nikosmods.weather2additions.network.MapPacket;
import nikosmods.weather2additions.Config;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;
import java.util.Objects;

public class ServerTabletMapRendering {
    public static Map<Column, Integer> otherMap = Maps.otherMap;
    public static int serverMapLoadRadius = Config.PLAYER_LOAD_RADIUS.get();
    public static int serverResolution = Config.RESOLUTION.get();
    public static int serverMapRadius = Config.TABLET_RADIUS.get();
    private static final Logger logger = Weather2Additions.LOGGER;
    private static BufferedImage lastImage;

    public static int fixColour(int colour, float dim) {
        dim = Mth.clamp(dim, 0f, 1f);
        int r = (int) (((colour >> 16) & 0xFF) * dim);
        int g = (int) (((colour >> 8) & 0xFF) * dim);
        int b = (int) ((colour & 0xFF) * dim);
        return b << 16 | g << 8 | r;
    }

    public static int brightenColour(int colour, float brighten) {
        brighten = Mth.clamp(brighten, 0f, 1f);
        int b = ((colour >> 16) & 0xFF) | (int) (0xFF * brighten);
        int g = ((colour >> 8) & 0xFF) | (int) (0xFF * brighten);
        int r = (colour & 0xFF) | (int) (0xFF * brighten);
        return b << 16 | g << 8 | r;
    }

    public static void loadAroundPlayer(Player player, ServerLevel level) {
        if (level != null) {
            serverMapRadius = Config.PLAYER_LOAD_RADIUS.get();
            for (int x = -serverMapLoadRadius; x <= serverMapLoadRadius; x ++) {
                for (int z = -serverMapLoadRadius; z <= serverMapLoadRadius; z++) {
                    if (choose(x, z)) {
                        int worldX = player.getBlockX() / serverResolution * serverResolution + x * serverResolution;
                        int worldY = player.getBlockZ() / serverResolution * serverResolution + z * serverResolution;
                        Column column = new Column(worldX, worldY, level);
                        int colour = getColour(worldX, worldY, level);
                        if (colour != 0) {
                            otherMap.put(column, colour);
                        }
                    }
                }
            }
        }
    }

    public static void updatePlayerWithImage(ServerPlayer player) {
        serverMapRadius = Config.TABLET_RADIUS.get();
        serverResolution = Config.RESOLUTION.get();
        int centerX = player.getBlockX() / serverResolution * serverResolution;
        int centerZ = player.getBlockZ() / serverResolution * serverResolution;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        BufferedImage map = generateMapBufferedImage(player);
        assert map != null;
        if (lastImage == null || map.getData() != lastImage.getData()) {
            try {
                ImageIO.write(map, "png", output);
                Messages.sendToClient(new MapImagePacket(output.toByteArray(), serverResolution, serverMapRadius, centerX, centerZ, map.getWidth(), map.getHeight(), "tablet"), player);
            } catch (Exception exception) {
                logger.error("This error should never have happened. Something is extremely wrong. Out of memory???");
            }
        }
        lastImage = map;
    }

    @Deprecated
    public static void updatePlayer(ServerPlayer player) {
        serverMapRadius = Config.TABLET_RADIUS.get();
        serverResolution = Config.RESOLUTION.get();
        int i = 0;
        int centerX = player.getBlockX() / serverResolution * serverResolution;
        int centerZ = player.getBlockZ() / serverResolution * serverResolution;
        int [] map = new int[4 * serverMapRadius * serverMapRadius + (serverMapRadius * 4) + 1];
        for (int x = -serverMapRadius; x <= serverMapRadius; x ++) {
            for (int z = -serverMapRadius; z <= serverMapRadius; z ++) {
                int worldX = centerX + x * serverResolution;
                int worldY = centerZ + z * serverResolution;
                Column column = new Column(worldX, worldY, player.level());
                map[i++] = otherMap.getOrDefault(column, 0);
            }
        }
        Messages.sendToClient(new MapPacket(map, serverResolution, serverMapRadius, centerX, centerZ, "tabletLegacy"), player);
    }

    public static boolean choose(int x, int z) {
        double distance = (Math.abs(x) + Math.abs(z)) * 0.5;
        double random = Math.random();
        if (distance == 0) {
            return true;
        }
        else {
            return 1.0 / distance > random;
        }
    }

    public static int getColour(int x, int z, Level level) {
        int Colour = 0;
        for (int height = 320; height >= -64; height --) {
            BlockPos blockPos = new BlockPos(x, height, z);
            if (level.isLoaded(blockPos)) {
                BlockState blockState = level.getBlockState(blockPos);
                if (!blockState.isAir()) {
                    Colour = blockState.getMapColor(level, blockPos).calculateRGBColor(MapColor.Brightness.NORMAL);
                    Colour = fixColour(Colour, ((float) (height + 56) / 220));
                    if (Colour != 0) {
                        if (!blockState.canOcclude()) {
                            Colour = brightenColour(Colour, 0.15f);
                        }
                        break;
                    }
                }
            }
        }
        return Colour;
    }

    public static void writeMapImage(ServerPlayer player) {
        logger.info("Writing to file");
        File output = new File("map.png");
        try {
            ImageIO.write(Objects.requireNonNull(generateMapBufferedImage(player)), "png", output);
        }
        catch(Exception exception) {
            logger.error("Error in saving file:" + exception);
        }
        logger.info("Saved to " + output);
    }

    public static BufferedImage generateMapBufferedImage(ServerPlayer player) {

        Map<Column, Integer> map = otherMap;

        try {
            BufferedImage mapImage = new BufferedImage(Config.TABLET_RADIUS.get() * 2 + 1, Config.TABLET_RADIUS.get() * 2 + 1, BufferedImage.TYPE_INT_RGB);
            int resolution = Config.RESOLUTION.get();

            for (int x = 0; x < Config.TABLET_RADIUS.get() * 2 + 1; x++) {
                for (int y = 0; y < Config.TABLET_RADIUS.get() * 2 + 1; y++) {

                    int colour;

                    if (map.get(new Column((player.getBlockX() / resolution + x - Config.TABLET_RADIUS.get()) * resolution, (player.getBlockZ() / resolution + y - Config.TABLET_RADIUS.get()) * resolution, player.level())) != null) {
                        colour = map.get(new Column((player.getBlockX() / resolution + x - Config.TABLET_RADIUS.get())* resolution, (player.getBlockZ() / resolution + y - Config.TABLET_RADIUS.get()) * resolution, player.level()));
                    }
                    else {
                        colour = 0;
                    }

                    mapImage.setRGB(x, y, colour);
                }
            }
            return mapImage;
        }
        catch (Exception exception) {
            Weather2Additions.LOGGER.error("Error in generation of BufferedImage:\n" + exception);
            return null;
        }
    }
}
