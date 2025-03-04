package nikosmods.weather2additions.data;

import net.minecraft.server.level.ServerPlayer;
import nikosmods.weather2additions.Config;
import nikosmods.weather2additions.Weather2Additions;
import nikosmods.weather2additions.items.itemfunction.Column;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

public class MapImageGenerator {

    static Logger logger = Weather2Additions.LOGGER;

    public static void writeMapImage(ServerPlayer player) {

        Map<Column, Integer> map = Maps.otherMap;

        try {
            logger.info("Starting image writing");
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

                    logger.debug("\n"+
                            "X | " + x + "\n" +
                            "Y | " + y + "\n" +
                                    "Colour | " + colour
                    );
                    mapImage.setRGB(x, y, colour);
                }
            }

            logger.info("Writing to file");
            File output = new File("map.png");
            ImageIO.write(mapImage, "png", output);
            logger.info("Saved to " + output);
        }
        catch (Exception exception) {
            Weather2Additions.LOGGER.error("Error in saving file:\n" + exception);
        }
    }

}
