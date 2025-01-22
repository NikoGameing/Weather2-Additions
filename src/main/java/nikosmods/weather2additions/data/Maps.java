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
    public static int mapDiameter;
    public static int tabletMapRadius = 50;
    public static final int radarMapRadius = 49;

    public static void updateMap(int[] mapArray, int x, int z, int resolution, String owner){
        switch (owner) {
            case "radar" -> {
                radarMap = mapArray;
            }
            case "tablet" -> {
                tabletMap = mapArray;
                mapDiameter = (int) Math.sqrt(tabletMap.length);
                mapX = x;
                mapY = z;
                mapResolution = resolution;
                tabletMapRadius = (mapDiameter / 2);
            }
        }
    }
}
