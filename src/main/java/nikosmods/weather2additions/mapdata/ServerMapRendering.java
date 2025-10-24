package nikosmods.weather2additions.mapdata;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import nikosmods.weather2additions.Weather2Additions;
import nikosmods.weather2additions.blocks.blockfunction.ScreenBlockEntity;
import nikosmods.weather2additions.items.itemfunction.Column;
import nikosmods.weather2additions.network.packets.map.MapImagePacket;
import nikosmods.weather2additions.network.Messages;
import nikosmods.weather2additions.network.packets.map.MapPacket;
import nikosmods.weather2additions.Config;
import nikosmods.weather2additions.network.packets.map.ScreenMapPacket;
import nikosmods.weather2additions.network.packets.map.TabletMapPacket;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class ServerMapRendering {
    public static Map<Column, Integer> otherMap = Maps.otherMap;
    public static int serverMapLoadRadius = Config.PLAYER_LOAD_RADIUS.get();
    public static int tabletResolution = Config.TABLET_RESOLUTION.get();
    public static int tabletRadius = Config.TABLET_RADIUS.get();
    public static int screenResolution = Config.SCREEN_RESOLUTION.get();
    public static int screenRadius = Config.SCREEN_RADIUS.get();
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
            tabletRadius = Config.PLAYER_LOAD_RADIUS.get();
            for (int x = -serverMapLoadRadius; x <= serverMapLoadRadius; x ++) {
                for (int z = -serverMapLoadRadius; z <= serverMapLoadRadius; z++) {
                    if (choose(x, z)) {
                        int worldX = player.getBlockX() / tabletResolution * tabletResolution + x * tabletResolution;
                        int worldY = player.getBlockZ() / tabletResolution * tabletResolution + z * tabletResolution;
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
        tabletRadius = Config.TABLET_RADIUS.get();
        tabletResolution = Config.TABLET_RESOLUTION.get();
        int centerX = player.getBlockX() / tabletResolution * tabletResolution;
        int centerZ = player.getBlockZ() / tabletResolution * tabletResolution;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        BufferedImage map = generateMapBufferedImage(player);
        assert map != null;
        if (lastImage == null || map.getData() != lastImage.getData()) {
            try {
                ImageIO.write(map, "png", output);
                Messages.sendToClient(new MapImagePacket(output.toByteArray(), tabletResolution, tabletRadius, centerX, centerZ, map.getWidth(), map.getHeight(), MapOwners.TABLET), player);
            } catch (Exception exception) {
                logger.error("Error in sending map to Player " + player + " with exception " + exception + ";\n" + Arrays.toString(exception.getStackTrace()));
            }
        }
        lastImage = map;
    }

    public static void updateTablet(ServerPlayer player) {
        tabletRadius = Config.TABLET_RADIUS.get();
        tabletResolution = Config.TABLET_RESOLUTION.get();
        int centerX = player.getBlockX() / tabletResolution * tabletResolution;
        int centerZ = player.getBlockZ() / tabletResolution * tabletResolution;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        BufferedImage map = generateMapBufferedImage(player);
        assert map != null;
        if (lastImage == null || map.getData() != lastImage.getData()) {
            try {
                ImageIO.write(map, "png", output);
                Messages.sendToClient(new TabletMapPacket(output.toByteArray(), map.getWidth(), map.getHeight(), tabletResolution, tabletRadius, centerX, centerZ), player);
            } catch (Exception exception) {
                logger.error("Error in sending map to Player " + player + " with exception " + exception + ";\n" + Arrays.toString(exception.getStackTrace()));
            }
        }
        lastImage = map;
    }

    public static void updateBlockWithImage(BlockEntity block, float offsetX, float offsetY) {
        screenRadius = Config.SCREEN_RADIUS.get();
        screenResolution = Config.SCREEN_RESOLUTION.get();
        int centerX = block.getBlockPos().getX() / screenResolution * screenResolution;
        int centerZ = block.getBlockPos().getZ() / screenResolution * screenResolution;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        BufferedImage map = generateMapBufferedImage(block, offsetX, offsetY);
        assert map != null;
        if (lastImage == null || map.getData() != lastImage.getData()) {
            try {
                ImageIO.write(map, "png", output);
                assert block.getLevel() != null;
                Messages.sendToPlayersTrackingChunk(new ScreenMapPacket(output.toByteArray(), map.getWidth(), map.getHeight(), screenResolution, screenRadius, centerX, centerZ, block.getBlockPos()), block.getLevel().getChunkAt(block.getBlockPos()));
            } catch (Exception exception) {
                logger.error("Error in sending map to Block " + block + " with exception " + exception + ";\n" + Arrays.toString(exception.getStackTrace()));
            }
        }
        lastImage = map;
    }

    @Deprecated
    public static void updatePlayer(ServerPlayer player) {
        tabletRadius = Config.TABLET_RADIUS.get();
        tabletResolution = Config.TABLET_RESOLUTION.get();
        int i = 0;
        int centerX = player.getBlockX() / tabletResolution * tabletResolution;
        int centerZ = player.getBlockZ() / tabletResolution * tabletResolution;
        int [] map = new int[4 * tabletRadius * tabletRadius + (tabletRadius * 4) + 1];
        for (int x = -tabletRadius; x <= tabletRadius; x ++) {
            for (int z = -tabletRadius; z <= tabletRadius; z ++) {
                int worldX = centerX + x * tabletResolution;
                int worldY = centerZ + z * tabletResolution;
                Column column = new Column(worldX, worldY, player.level());
                map[i++] = otherMap.getOrDefault(column, 0);
            }
        }
        Messages.sendToClient(new MapPacket(map, tabletResolution, tabletRadius, centerX, centerZ, MapOwners.LEGACY), player);
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

    public static void writePlayerMapImage(ServerPlayer player) {
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

    public static void writeBlockMapImage(BlockEntity blockEntity) {
        logger.info("Writing to file");
        File output = new File("map.png");
        float offsetX = 0;
        float offsetY = 0;
        if (blockEntity instanceof ScreenBlockEntity screenBlock) {
            offsetX = screenBlock.offsetX;
            offsetY = screenBlock.offsetY;
        }
        try {
            ImageIO.write(Objects.requireNonNull(generateMapBufferedImage(blockEntity, offsetX, offsetY)), "png", output);
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
            int resolution = Config.TABLET_RESOLUTION.get();

            for (int x = 0; x < Config.TABLET_RADIUS.get() * 2 + 1; x++) {
                for (int y = 0; y < Config.TABLET_RADIUS.get() * 2 + 1; y++) {
                    int colour = map.getOrDefault(new Column((player.getBlockX() / resolution + x - Config.TABLET_RADIUS.get()) * resolution, (player.getBlockZ() / resolution + y - Config.TABLET_RADIUS.get()) * resolution, player.level()), 0);
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

    public static BufferedImage generateMapBufferedImage(BlockEntity block, float offsetX, float offsetY) {
        Map<Column, Integer> map = otherMap;
        try {
            BufferedImage mapImage = new BufferedImage(Config.SCREEN_RADIUS.get() * 2 + 1, Config.SCREEN_RADIUS.get() * 2 + 1, BufferedImage.TYPE_INT_RGB);
            int resolution = Config.SCREEN_RESOLUTION.get();

            for (int x = 0; x < Config.SCREEN_RADIUS.get() * 2 + 1; x++) {
                for (int y = 0; y < Config.SCREEN_RADIUS.get() * 2 + 1; y++) {
                    Column queriedColumn = new Column(
                            ((block.getBlockPos().getX() + (int) (Math.ceil(offsetX * ((double) (Config.SCREEN_RADIUS.get() * (resolution * 4)) / 2)))) / resolution + x - Config.SCREEN_RADIUS.get()) * resolution,
                            ((block.getBlockPos().getZ() + (int) (Math.ceil(offsetY * ((double) (Config.SCREEN_RADIUS.get() * (resolution * 4)) / 2)))) / resolution + y - Config.SCREEN_RADIUS.get()) * resolution,
                            block.getLevel()
                    );
                    int colour;
                    colour = map.getOrDefault(queriedColumn, 0);
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
