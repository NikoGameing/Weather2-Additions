package nikosmods.weather2additions.mapdata;

// Accessed by all blocks that require a map, but all the data is serialised by block position

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nikosmods.weather2additions.Weather2Additions;
import org.slf4j.Logger;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = Weather2Additions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class BlockMapDataList {
    private static Level level;
    private static final Logger LOGGER = LogUtils.getLogger();
    public static ArrayList<BlockMapData> blockList = new ArrayList<>();

    public static BlockMapData getData(BlockPos blockPos) {
        for (BlockMapData mapData : blockList) {
            if (mapData.blockPos.equals(blockPos)) {
                return mapData;
            }
        }
        return null;
    }

    public static void addBlock(BlockPos blockPos) {
        blockList.add(new BlockMapData(blockPos, level.getBlockEntity(blockPos)));
    }

    public static void removeBlock(BlockPos blockPos) {
        blockList.removeIf(mapData -> mapData.blockPos.equals(blockPos));
    }

    public static void checkList() {
        int size = blockList.size();
        blockList.removeIf(mapData -> mapData.blockEntity != level.getBlockEntity(mapData.blockPos));
        if (blockList.size() < size) {
            LOGGER.warn(size - blockList.size() + " blocks were removed from the Block Map Data List that no longer exist");
        }
    }

    public static void update(byte[] image, int mapX, int mapY, int imageWidth, int imageHeight, int resolution, int radius, BlockPos blockPos) {
        blockList.forEach(mapData -> {
            if (mapData.blockPos.equals(blockPos)) {
                mapData.update(image, mapX, mapY, imageWidth, imageHeight, resolution, radius);
            }
        }
        );
    }

    @SubscribeEvent
    public static void unload(LevelEvent.Unload unload) {
        blockList.clear();
    }

    @SubscribeEvent
    public static void load(LevelEvent.Load load) {
        level = (Level) load.getLevel();
    }

}
