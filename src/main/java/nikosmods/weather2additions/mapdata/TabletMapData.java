package nikosmods.weather2additions.mapdata;

import nikosmods.weather2additions.network.Messages;

// Accessed universally by all Tablet items, but each player will see something different because it's only sent to them, nobody else
public class TabletMapData {
    public static byte[] image;
    public static int resolution;
    public static int radius;
    public static int width;
    public static int height;
    public static int mapX;
    public static int mapY;

    public static void update(byte[] newImage, int newWidth, int newHeight, int newResolution, int newRadius, int newMapX, int newMapY) {
        image = newImage;
        width = newWidth;
        height = newHeight;
        radius = newRadius;
        mapX = newMapX;
        mapY = newMapY;
        resolution = newResolution;
    }
}
