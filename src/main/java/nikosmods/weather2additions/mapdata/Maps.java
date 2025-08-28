package nikosmods.weather2additions.mapdata;

import nikosmods.weather2additions.items.itemfunction.Column;

import java.util.HashMap;
import java.util.Map;

public class Maps {
    @Deprecated
    public static int [] tabletMap;

    public static int [] radarMap;
    public static byte[] tabletImage;
    public static byte[] screenImage;
    public static int mapResolution = 16;
    public static Map<Column, Integer> otherMap = new HashMap<>();
    public static int mapX;
    public static int mapY;
    public static int tabletImageWidth;
    public static int tabletImageHeight;
    public static int tabletMapRadius = 50;
    public static int screenImageWidth;
    public static int screenImageHeight;
    public static int screenImageRadius = 50;
    public static final int radarMapRadius = 49;

    public static void updateMap(byte[] image , int[] mapArray, int x, int z, int width, int height, int resolution, int radius, MapOwners owner) {
        switch (owner) {
            case RADAR -> {
                radarMap = mapArray;
                mapResolution = resolution;
            }
            case LEGACY -> {
                tabletMap = mapArray;
                mapX = x;
                mapY = z;
                mapResolution = resolution;
                tabletMapRadius = radius;
            }
            case TABLET -> {
                tabletImage = image;
                mapX = x;
                mapY = z;
                tabletImageWidth = width;
                tabletImageHeight = height;
                mapResolution = resolution;
                tabletMapRadius = radius;
            }
            case SCREEN -> {
                screenImage = image;
                mapX = x;
                mapY = z;
                screenImageWidth = width;
                screenImageHeight = height;
                mapResolution = resolution;
                screenImageRadius = radius;
            }
        }
    }
}
