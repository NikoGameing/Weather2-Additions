package nikosmods.weather2additions.data;

import com.mojang.blaze3d.platform.NativeImage;
import nikosmods.weather2additions.items.itemfunction.Column;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Maps {
    @Deprecated
    public static int [] tabletMap;

    public static int [] radarMap;
    public static byte[] tabletImage;
    public static int mapResolution = 16;
    public static Map<Column, Integer> otherMap = new HashMap<>();
    public static int mapX;
    public static int mapY;
    public static int tabletImageWidth;
    public static int tabletImageHeight;
    public static int tabletMapRadius = 50;
    public static final int radarMapRadius = 49;

    public static void updateMap(byte[] image , int[] mapArray, int x, int z, int width, int height, int resolution, int radius, String owner){
        switch (owner) {
            case "radar" -> {
                radarMap = mapArray;
                mapResolution = resolution;
            }
            case "tabletLegacy" -> {
                tabletMap = mapArray;
                mapX = x;
                mapY = z;
                mapResolution = resolution;
                tabletMapRadius = radius;
            }
            case "tablet" -> {
                tabletImage = image;
                mapX = x;
                mapY = z;
                tabletImageWidth = width;
                tabletImageHeight = height;
                mapResolution = resolution;
                tabletMapRadius = radius;
            }
        }
    }
}
