package nikosmods.weather2additions.data;

import nikosmods.weather2additions.items.itemfunction.Column;

import java.util.HashMap;
import java.util.Map;

public class Maps {
    public static int [] tabletMap;
    public static int [] radarMap;
    public static int mapResolution = 16;
    public static Map<Column, Integer> otherMap = new HashMap<>();
    public static int mapX;
    public static int mapY;
    public static int tabletMapRadius = 50;
    public static final int radarMapRadius = 49;

    public static void updateMap(int[] mapArray, int x, int z, int resolution, int radius, String owner){
        switch (owner) {
            case "radar" -> {
                radarMap = mapArray;
                mapResolution = resolution;
            }
            case "tablet" -> {
                tabletMap = mapArray;
                mapX = x;
                mapY = z;
                mapResolution = resolution;
                tabletMapRadius = radius;
            }
        }
    }
}
