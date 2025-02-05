package nikosmods.weather2additions.items.itemfunction;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.logging.LogUtils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import nikosmods.weather2additions.data.Maps;
import nikosmods.weather2additions.items.Tablet;
import nikosmods.weather2additions.keyreg.KeyRegistries;
import nikosmods.weather2additions.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import weather2.ClientTickHandler;
import weather2.weathersystem.WeatherManagerClient;
import weather2.weathersystem.storm.StormObject;
import weather2.weathersystem.storm.WeatherObject;
import weather2.weathersystem.storm.WeatherObjectParticleStorm;
import weather2.weathersystem.wind.WindManager;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class TabletMapRendering {
    private static int mapResolution = Config.RESOLUTION.get();
    private static int mapRadius = Config.TABLET_RADIUS.get();
    private static int selection = 0;
    private static WeatherObject selected;
    public static int tick = 0;
    // big resourcelocation burger

    private static final ResourceLocation cyclone = new ResourceLocation("weather2", "textures/radar/radar_icon_cyclone.png");
    private static final ResourceLocation hail = new ResourceLocation("weather2", "textures/radar/radar_icon_hail.png");
    private static final ResourceLocation lightning = new ResourceLocation("weather2", "textures/radar/radar_icon_lightning.png");
    private static final ResourceLocation rain = new ResourceLocation("weather2", "textures/radar/radar_icon_rain.png");
    private static final ResourceLocation rain2 = new ResourceLocation("weather2", "textures/radar/radar_icon_rain2.png"); // why's there a second one?? the rain2 icon mason, what does it mean!?!
    private static final ResourceLocation sandstorm = new ResourceLocation("weather2", "textures/radar/radar_icon_sandstorm.png");
    private static final ResourceLocation tornado = new ResourceLocation("weather2", "textures/radar/radar_icon_tornado.png");
    private static final ResourceLocation wind = new ResourceLocation("weather2", "textures/radar/radar_icon_wind.png");
    private static final ResourceLocation snowstorm = new ResourceLocation("weather2_additions", "textures/weathericon/radar_icon_snowstorm.png");
    private static final ResourceLocation text = new ResourceLocation("weather2_additions", "textures/weathericon/words/textmap.png");
    private static final ResourceLocation background = new ResourceLocation("weather2_additions", "textures/weathericon/background.png");
    private static final ResourceLocation compass = new ResourceLocation("weather2_additions", "textures/weathericon/compass.png");
    private static final ResourceLocation tornadoText = new ResourceLocation("weather2_additions", "textures/weathericon/words/tornadotext.png");
    private static final ResourceLocation hurricaneText = new ResourceLocation("weather2_additions", "textures/weathericon/words/hurricanetext.png");
    private static final ResourceLocation hailText = new ResourceLocation("weather2_additions", "textures/weathericon/words/hailstormtext.png");
    private static final ResourceLocation windText = new ResourceLocation("weather2_additions", "textures/weathericon/words/highwindtext.png");
    private static final ResourceLocation lightningText = new ResourceLocation("weather2_additions", "textures/weathericon/words/lightningtext.png");
    private static final ResourceLocation rainText = new ResourceLocation("weather2_additions", "textures/weathericon/words/raintext.png");
    private static final ResourceLocation snowText = new ResourceLocation("weather2_additions", "textures/weathericon/words/snowtext.png");
    private static final ResourceLocation sandText = new ResourceLocation("weather2_additions", "textures/weathericon/words/sandtext.png");
    private static final ResourceLocation clearText = new ResourceLocation("weather2_additions", "textures/weathericon/words/cleartext.png");
    private static final ResourceLocation unselectedBorder = new ResourceLocation("weather2_additions", "textures/weathericon/unselected.png");
    private static final ResourceLocation selectedBorder = new ResourceLocation("weather2_additions", "textures/weathericon/selected.png");
    private static final ResourceLocation battery5 = new ResourceLocation("weather2_additions", "textures/weathericon/battery/5.png");
    private static final ResourceLocation battery4 = new ResourceLocation("weather2_additions", "textures/weathericon/battery/4.png");
    private static final ResourceLocation battery3 = new ResourceLocation("weather2_additions", "textures/weathericon/battery/3.png");
    private static final ResourceLocation battery2 = new ResourceLocation("weather2_additions", "textures/weathericon/battery/2.png");
    private static final ResourceLocation battery1 = new ResourceLocation("weather2_additions", "textures/weathericon/battery/1.png");
    private static final ResourceLocation battery0 = new ResourceLocation("weather2_additions", "textures/weathericon/battery/0.png");

    public static @Nullable Dimension getImageDimension(InputStream inputStream) throws IOException {
        Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix("png");
        while(iter.hasNext()) {
            ImageReader reader = iter.next();
            try {
                assert inputStream != null;
                ImageInputStream stream = new MemoryCacheImageInputStream(inputStream);
                reader.setInput(stream);
                int width = reader.getWidth(reader.getMinIndex());
                int height = reader.getHeight(reader.getMinIndex());
                return new Dimension(width, height);
            } catch (Exception e) {
                LogUtils.getLogger().warn("Error reading: {}", e);
            } finally {
                reader.dispose();
            }
            return null;
        }
        throw new IOException("Not a PNG file");
    }


    public static void renderSpecificSize(@NotNull PoseStack transform, float xPos, float yPos, int uvX1, int uvY1, int uvX2, int uvY2, int uvX3, int uvY3, int uvX4, int uvY4, float zPos, float width, float height, int resX, int resY, ResourceLocation texture) { // basically renderTexture but more intricate
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(transform.last().pose(), xPos-width,yPos-height,zPos).uv((float) uvX1 / resX,(float) uvY1 / resY).endVertex();
        bufferBuilder.vertex(transform.last().pose(), xPos+width,yPos-height,zPos).uv((float) uvX2 / resX,(float) uvY2 / resY).endVertex();
        bufferBuilder.vertex(transform.last().pose(), xPos+width,yPos+height,zPos).uv((float) uvX3 / resX,(float) uvY3 / resY).endVertex();
        bufferBuilder.vertex(transform.last().pose(), xPos-width,yPos+height,zPos).uv((float) uvX4 / resX,(float) uvY4 / resY).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    public static void renderTexture(PoseStack transform, ResourceLocation texture, float x, float y, float z, float width, float height, boolean enableTransparency) { // basically renderSpecificSize but less intricate
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableDepthTest();
        if (enableTransparency) {
            RenderSystem.enableBlend();
        }
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(transform.last().pose(), x-width,y-height,z).uv(0,1).endVertex();
        bufferBuilder.vertex(transform.last().pose(), x+width,y-height,z).uv(1,1).endVertex();
        bufferBuilder.vertex(transform.last().pose(), x+width,y+height,z).uv(1,0).endVertex();
        bufferBuilder.vertex(transform.last().pose(), x-width,y+height,z).uv(0,0).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    public static void renderHead(@NotNull PoseStack transform, @NotNull LocalPlayer player) {
        RenderSystem.setShaderTexture(0, player.getSkinTextureLocation());
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableDepthTest();
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(transform.last().pose(), -0.02f,-0.02f,0.00011f).uv(8f/64f,16f/64f).endVertex(); // (8f/64f,16f/64f)
        bufferBuilder.vertex(transform.last().pose(), 0.02f,-0.02f,0.00011f).uv(16f/64f,16f/64f).endVertex(); // (16f/64f,16f/64f)
        bufferBuilder.vertex(transform.last().pose(), 0.02f,0.02f,0.00011f).uv(16f/64f,8f/64f).endVertex(); // (16f/64f,8f/64f)
        bufferBuilder.vertex(transform.last().pose(), -0.02f,0.02f,0.00011f).uv(8f/64f,8f/64f).endVertex(); // (8f/64f,8f/64f)
        BufferUploader.drawWithShader(bufferBuilder.end());
    }

    public static void renderAll(PoseStack transform, LocalPlayer player, ItemStack stack) throws IOException {
        // renderRectangle(transform,0,0,0.2f,0.1f,1,0,1,1f); // (redundant, only used for testing)

        renderWeather(transform, player);
        renderHead(transform, player);
        renderMap(transform, player);
        renderWeatherInfo(transform, player);
        renderCompass(transform, player);
        renderStormInfo(transform, player);
        renderBattery(transform, stack);
    }


    public static void renderWeather(PoseStack transform, LocalPlayer player) {
        WeatherManagerClient weather = ClientTickHandler.weatherManager;
        mapRadius = Maps.tabletMapRadius;
        mapResolution = Maps.mapResolution;
        for (WeatherObject weatherObject:weather.getStormObjects()) {
            float playerX = (float) player.getX();
            float playerZ = (float) player.getZ();
            float relativeX = (float) weatherObject.pos.x - playerX;
            float relativeZ = (float) weatherObject.pos.z - playerZ;
            float screenX = relativeX / mapRadius / mapResolution;
            float screenY = -(relativeZ / mapRadius / mapResolution);
            float iconMultiplier = 1;
            ResourceLocation icon = null;
            if (weatherObject instanceof StormObject stormObject) {
                if (stormObject.levelCurIntensityStage >= StormObject.STATE_FORMING) {
                    if (stormObject.stormType == StormObject.TYPE_LAND) {
                        icon = tornado;
                    }
                    else if (stormObject.stormType == StormObject.TYPE_WATER) {
                        icon = cyclone;
                    }
                }
                else if (stormObject.levelCurIntensityStage == StormObject.STATE_HAIL) {
                    icon = hail;
                }
                else if (stormObject.levelCurIntensityStage == StormObject.STATE_HIGHWIND) {
                    icon = wind;
                }
                else if (stormObject.levelCurIntensityStage == StormObject.STATE_THUNDER) {
                    icon = lightning;
                }
                 else if (stormObject.isPrecipitating()) {
                    icon = rain;
                }
                 if (stormObject.levelCurIntensityStage > 3) {
                     iconMultiplier = (float) Math.sqrt(stormObject.levelCurStagesIntensity + (stormObject.levelCurIntensityStage - (float) 4)) / 2;
                     if (iconMultiplier < 0.2f) {
                         iconMultiplier = 0.2f;
                     }
                     renderNumber(transform, text, "F".concat(Integer.toString(stormObject.levelCurIntensityStage - 4)), screenX + (0.005f * iconMultiplier), screenY - (0.04f * iconMultiplier),0.00012f,(0.011f * 1.5f) * iconMultiplier,(0.015f * 1.5f) * iconMultiplier, false);
                 }
            }
            else if (weatherObject instanceof WeatherObjectParticleStorm particleStorm) {
                if (particleStorm.type == WeatherObjectParticleStorm.StormType.SANDSTORM) {
                    icon = sandstorm;
                }
                else if (particleStorm.type == WeatherObjectParticleStorm.StormType.SNOWSTORM) {
                    icon = snowstorm;
                }
            }
            if (icon != null && screenX > -1 && screenX < 1 && screenY > -1 && screenY < 1) {
                renderTexture(transform, icon, screenX, screenY, 0.0001f,0.05f * iconMultiplier, 0.05f * iconMultiplier, false);
            }
        }
    }


    public static void renderWeatherInfo(PoseStack transform, @NotNull Player player) {
        WindManager windManager = ClientTickHandler.weatherManager.getWindManager();
        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        float textScaleFactor = 1.5f;
        float directionScaleFactor = 0.75f;
        float compassScaleFactor = 0.62f;
        float numberScaleFactor = 2.1f;
        float windSpeed = windManager.getWindSpeed(player.blockPosition());
        float windDirection = (float) Math.toRadians(windManager.getWindAngle(player.position()));
        String windDirectionString = getString(windDirection);
        renderTexture(transform, compass, 0.73f,-0.65f,0.00011f,0.27f * compassScaleFactor,0.31f * compassScaleFactor, true);
        renderTexture(transform, background, -0.62f,0.76f, 0.00011f, 0.65f,0.13f, true);
        renderSpecificSize(transform, -0.73f,0.8f,0,50, 332,50, 332,0,  0,0, 0.00015f, 0.166f * textScaleFactor, 0.025f * textScaleFactor, 1052, 1020, text);
        renderNumber(transform, text, (decimalFormat.format(windSpeed * (windSpeed * 28)).concat("[")), -0.47f, 0.8f, 0.00014f, 0.011f * numberScaleFactor,0.015f * numberScaleFactor, false); // approximated to resemble real life wind, made to be exponential because tornado wind is just "2" lol
        renderSpecificSize(transform, -0.665f,0.7f,0,105, 416,105, 416,61,  0,61, 0.00015f, 0.416f * directionScaleFactor, (0.105f-0.061f) * directionScaleFactor, 1052, 1020, text);
        renderNumber(transform, text, windDirectionString, -0.35f, 0.695f, 0.00014f, 0.011f * numberScaleFactor,0.015f * numberScaleFactor, false);
    }

    public static void renderStormInfo(PoseStack transform, Player player) throws IOException {
        WeatherManagerClient weather = ClientTickHandler.weatherManager;
        mapRadius = Maps.tabletMapRadius;
        mapResolution = Maps.mapResolution;
        ArrayList<WeatherObject> stormObjects = new ArrayList<>();
        for (WeatherObject weatherObject : weather.getStormObjects()) {
            float playerX = (float) player.getX();
            float playerZ = (float) player.getZ();
            float relativeX = (float) weatherObject.pos.x-playerX;
            float relativeZ = -((float) weatherObject.pos.z-playerZ);
            float screenX = relativeX / mapRadius / mapResolution;
            float screenY = -(relativeZ / mapRadius / mapResolution);
            if (screenX > -1 && screenX < 1 && screenY > -1 && screenY < 1) {
                if (weatherObject instanceof StormObject stormObject) {
                    if (stormObject.levelCurIntensityStage != StormObject.STATE_NORMAL) {
                        stormObjects.add(weatherObject);
                    }
                }
                else if (weatherObject instanceof WeatherObjectParticleStorm) {
                    stormObjects.add(weatherObject);
                }
            }
        }
        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        ResourceLocation name = rainText;
        int stormSize = 0;
        float stormTime = 0;
        String stormProgress = "0";
        Dimension imageDimensions;
        String stormStrength = "0";
        float textScaleFactor = 0.75f;
        float sizeMultiplier = 2.2f;
        if (stormObjects.isEmpty()) {
            name = clearText;
        }

        if (KeyRegistries.cycleMapForward.get().consumeClick()) {
            if (selection < stormObjects.size()) {
                selection++;
            }
        } else if (KeyRegistries.cycleMapBackward.get().consumeClick()) {
            if (selection > -1) {
                selection--;
            }
        }
        if (selection >= stormObjects.size()) {
            selection = 0;
        } else if (selection <= -1) {
            selection = stormObjects.size()-1;
        }

        if (!stormObjects.isEmpty()) {
            if (stormObjects.get(selection) instanceof StormObject stormObject) {
                String strengthDecimal = decimalFormat.format(stormObject.spinSpeed * 5600)+"[";
                if (stormObject.levelCurIntensityStage >= StormObject.STATE_FORMING) {
                    if (stormObject.stormType == StormObject.TYPE_LAND) {
                        name = tornadoText;
                        stormStrength = "F"+(stormObject.levelCurIntensityStage-4);
                        stormSize = stormObject.size;
                        stormProgress = decimalFormat.format(stormObject.levelCurStagesIntensity * 100) + "%";
                        selected = stormObject;
                    } else if (stormObject.stormType == StormObject.TYPE_WATER) {
                        name = hurricaneText;
                        stormStrength = "F"+(stormObject.levelCurIntensityStage-4);
                        stormSize = stormObject.size;
                        stormProgress = decimalFormat.format(stormObject.levelCurStagesIntensity * 100) + "%";
                        selected = stormObject;
                    }
                } else if (stormObject.levelCurIntensityStage == StormObject.STATE_HAIL) {
                    name = hailText;
                    stormStrength = strengthDecimal;
                    stormSize = stormObject.size;
                    stormProgress = decimalFormat.format(stormObject.levelCurStagesIntensity * 100) + "%";
                    selected = stormObject;
                } else if (stormObject.levelCurIntensityStage == StormObject.STATE_HIGHWIND) {
                    name = windText;
                    stormStrength = strengthDecimal;
                    stormSize = stormObject.size;
                    stormProgress = decimalFormat.format(stormObject.levelCurStagesIntensity * 100) + "%";
                    selected = stormObject;
                } else if (stormObject.levelCurIntensityStage == StormObject.STATE_THUNDER) {
                    name = lightningText;
                    stormStrength = strengthDecimal;
                    stormSize = stormObject.size;
                    stormProgress = decimalFormat.format(stormObject.levelCurStagesIntensity * 100) + "%";
                    selected = stormObject;
                } else if (stormObject.isPrecipitating()) {
                    stormStrength = strengthDecimal;
                    stormSize = stormObject.size;
                    stormProgress = decimalFormat.format(stormObject.levelCurStagesIntensity * 100) + "%";
                    selected = stormObject;
                }
            } else if (stormObjects.get(selection) instanceof WeatherObjectParticleStorm particleStorm) {
                if (particleStorm.type == WeatherObjectParticleStorm.StormType.SANDSTORM) {
                    name = sandText;
                    stormStrength = decimalFormat.format(particleStorm.getIntensity() * (particleStorm.getIntensity() * 28)) + "[";
                    stormTime = ((float) particleStorm.age / 20) / 60;
                    stormProgress = decimalFormat.format(stormTime)+"M";
                    selected = particleStorm;
                } else if (particleStorm.type == WeatherObjectParticleStorm.StormType.SNOWSTORM) {
                    name = snowText;
                    stormStrength = decimalFormat.format(particleStorm.getIntensity() * (particleStorm.getIntensity() * 28)) + "[";
                    stormSize = particleStorm.size;
                    stormTime = ((float) particleStorm.age / 20) / 60;
                    stormProgress = decimalFormat.format(stormTime)+"M";
                    selected = particleStorm;
                }
            }
            selectionIcon(transform, player, stormObjects);
            imageDimensions = getImageDimension(
                    TabletMapRendering.class.getClassLoader().getResourceAsStream(
                            "/assets/weather2_additions/"+name.getPath()
                    )
            );
            assert imageDimensions != null;
            if (name != clearText) {
                renderSpecificSize(transform, -0.61f, -0.71f, 0, 478, 488, 478, 488, 233, 0, 233, 0.00015f, 0.488f * textScaleFactor, (0.478f-0.233f) * textScaleFactor, 1052, 1020, text);
                renderTexture(transform, background, -0.53f, -0.71f, 0.00014f, 0.65f * textScaleFactor+0.15f, (0.478f-0.244f) * textScaleFactor+0.02f, true);
                renderSpecificSize(transform,
                        -0.355f + (float) imageDimensions.width / 1350 / 2,
                        -0.578f,
                        0, imageDimensions.height,
                        imageDimensions.width, imageDimensions.height,
                        imageDimensions.width, 0,
                        0, 0,
                        0.00015f,
                        (float) imageDimensions.width / 1350,
                        (float) imageDimensions.height / 1350,
                        imageDimensions.width,
                        imageDimensions.height,
                        name);
                renderNumber(transform, text, stormStrength, -0.285f, -0.669f, 0.00015f, (0.011f) * sizeMultiplier, (0.015f) * sizeMultiplier, false);
                renderNumber(transform, text, stormSize+"M", -0.5f, -0.762f, 0.00015f, (0.011f) * sizeMultiplier, (0.015f) * sizeMultiplier, false);
                renderNumber(transform, text, stormProgress, -0.252f, -0.852f, 0.00015f, (0.011f) * sizeMultiplier, (0.015f) * sizeMultiplier, false);
            }
        }
    }

    public static void selectionIcon(PoseStack transform, Player player, @NotNull ArrayList<WeatherObject> stormObjects) {
        mapRadius = Maps.tabletMapRadius;
        mapResolution = Maps.mapResolution;
        for (WeatherObject stormObject:stormObjects) {
            float playerX = (float) player.getX();
            float playerZ = (float) player.getZ();
            float relativeX = (float) stormObject.pos.x - playerX;
            float relativeZ = (float) stormObject.pos.z - playerZ;
            float screenX = relativeX / mapRadius / mapResolution;
            float screenY = -(relativeZ / mapRadius / mapResolution);
            if (stormObject == selected) {
                renderTexture(transform, selectedBorder, screenX, screenY, 0.00011f, 0.07f, 0.07f, true);
            }
            else {
                renderTexture(transform, unselectedBorder, screenX, screenY, 0.00011f, 0.07f, 0.07f, true);
            }
        }
    }

    public static void renderCompass(PoseStack transform, @NotNull Player player) {
        float playerFacing = (float) Math.toRadians(player.getYHeadRot()) + 180;
        int playerFacingInt = (int) (0.5f + 8f * (playerFacing / (2f * Math.PI))) % 8;
        float xPos;
        String facingDirection = "";
        switch (playerFacingInt) {
            case 1 -> facingDirection = "N";
            case 2 -> facingDirection = "NE";
            case 3 -> facingDirection = "E";
            case 4 -> facingDirection = "SE";
            case 5 -> facingDirection = "S";
            case 6 -> facingDirection = "SW";
            case 7 -> facingDirection = "W";
            case 0 -> facingDirection = "NW";
        }
        renderTexture(transform, background, 0.9f,0.76f, 0.00011f, 0.13f,0.13f, true);
        if (facingDirection.length() == 2) {
            xPos = 0.76f;
        }
        else {
            xPos = 0.80f;
        }
        renderNumber(transform, text, facingDirection, xPos, 0.75f, 0.00014f, 0.011f * 4f, 0.015f * 4f, false);
    }

    @NotNull
    private static String getString(float windDirection) {
        String windDirectionString = "N";
        int compassInt = (int) (0.5f + 8f * (windDirection / (2f * Math.PI))) % 8;
        switch (compassInt) {
            case 4 -> windDirectionString = "N";
            case 5 -> windDirectionString = "NE";
            case 6 -> windDirectionString = "E";
            case 7 -> windDirectionString = "SE";
            case 0 -> windDirectionString = "S";
            case 1 -> windDirectionString = "SW";
            case 2 -> windDirectionString = "W";
            case 3 -> windDirectionString = "NW";
        }
        return windDirectionString;
    }
    public static void renderNumber(PoseStack transform, ResourceLocation numberTexture, @NotNull String number, float xPos, float yPos, float zPos, float width, float height, boolean centerByLength) {
        char[] numArray = number.toCharArray();
        float offset = 0;
        float offsetHeight = 0;
        boolean unWiden = false;
        boolean unWidenMph = false;
        int resolutionX = 1052;
        int resolutionY = 1020;
        int intMapPosX1 = 0;
        int intMapPosY1 = 0;
        int intMapPosX2 = 0;
        int intMapPosY2 = 0;
        int intMapPosX3 = 0;
        int intMapPosY3 = 0;
        int intMapPosX4 = 0;
        int intMapPosY4 = 0;
        if (centerByLength) {
            xPos = xPos - (float) (numArray.length * 36 - 72 + 12) / 1000;
        }
        for (char numString:numArray) {
            switch (numString) {
                case '1' -> {
                    offset = offset+2*width;
                    intMapPosX1 = 0;
                    intMapPosY1 = 532;
                    intMapPosX2 = 32;
                    intMapPosY2 = 532;
                    intMapPosX3 = 32;
                    intMapPosY3 = 488;
                    intMapPosX4 = 0;
                    intMapPosY4 = 488;
                }
                case '2' -> {
                    offset = offset+2*width;
                    intMapPosX1 = 36;
                    intMapPosY1 = 532;
                    intMapPosX2 = 68;
                    intMapPosY2 = 532;
                    intMapPosX3 = 68;
                    intMapPosY3 = 488;
                    intMapPosX4 = 36;
                    intMapPosY4 = 488;
                }
                case '3' -> {
                    offset = offset+2*width;
                    intMapPosX1 = 36+36;
                    intMapPosY1 = 532;
                    intMapPosX2 = 68+36;
                    intMapPosY2 = 532;
                    intMapPosX3 = 68+36;
                    intMapPosY3 = 488;
                    intMapPosX4 = 36+36;
                    intMapPosY4 = 488;
                }
                case '4' -> {
                    offset = offset+2*width;
                    intMapPosX1 = 36+36+36;
                    intMapPosY1 = 532;
                    intMapPosX2 = 68+36+36;
                    intMapPosY2 = 532;
                    intMapPosX3 = 68+36+36;
                    intMapPosY3 = 488;
                    intMapPosX4 = 36+36+36;
                    intMapPosY4 = 488;
                }
                case '5' -> {
                    offset = offset+2*width;
                    intMapPosX1 = 36+36+36+36;
                    intMapPosY1 = 532;
                    intMapPosX2 = 68+36+36+36;
                    intMapPosY2 = 532;
                    intMapPosX3 = 68+36+36+36;
                    intMapPosY3 = 488;
                    intMapPosX4 = 36+36+36+36;
                    intMapPosY4 = 488;
                }
                case '6' -> {
                    offset = offset+2*width;
                    intMapPosX1 = 36+36+36+36+36;
                    intMapPosY1 = 532;
                    intMapPosX2 = 68+36+36+36+36;
                    intMapPosY2 = 532;
                    intMapPosX3 = 68+36+36+36+36;
                    intMapPosY3 = 488;
                    intMapPosX4 = 36+36+36+36+36;
                    intMapPosY4 = 488;
                }
                case '7' -> {
                    offset = offset+2*width;
                    intMapPosX1 = 36+36+36+36+36+36;
                    intMapPosY1 = 532;
                    intMapPosX2 = 68+36+36+36+36+36;
                    intMapPosY2 = 532;
                    intMapPosX3 = 68+36+36+36+36+36;
                    intMapPosY3 = 488;
                    intMapPosX4 = 36+36+36+36+36+36;
                    intMapPosY4 = 488;
                }
                case '8' -> {
                    offset = offset+2*width;
                    intMapPosX1 = 36+36+36+36+36+36+36;
                    intMapPosY1 = 532;
                    intMapPosX2 = 68+36+36+36+36+36+36;
                    intMapPosY2 = 532;
                    intMapPosX3 = 68+36+36+36+36+36+36;
                    intMapPosY3 = 488;
                    intMapPosX4 = 36+36+36+36+36+36+36;
                    intMapPosY4 = 488;
                }
                case '9' -> {
                    offset = offset+ 2*width;
                    intMapPosX1 = 36+36+36+36+36+36+36+36;
                    intMapPosY1 = 532;
                    intMapPosX2 = 68+36+36+36+36+36+36+36;
                    intMapPosY2 = 532;
                    intMapPosX3 = 68+36+36+36+36+36+36+36;
                    intMapPosY3 = 488;
                    intMapPosX4 = 36+36+36+36+36+36+36+36;
                    intMapPosY4 = 488;
                }
                case '0' -> {
                    offset = offset+2*width;
                    intMapPosX1 = 36+36+36+36+36+36+36+36+36;
                    intMapPosY1 = 532;
                    intMapPosX2 = 68+36+36+36+36+36+36+36+36;
                    intMapPosY2 = 532;
                    intMapPosX3 = 68+36+36+36+36+36+36+36+36;
                    intMapPosY3 = 488;
                    intMapPosX4 = 36+36+36+36+36+36+36+36+36;
                    intMapPosY4 = 488;
                }
                case '%' -> {
                    offset = offset+2.1f*width;
                    intMapPosX1 = 36+36+36+36+36+36+36+36+36+36;
                    intMapPosY1 = 532;
                    intMapPosX2 = 68+36+36+36+36+36+36+36+36+36;
                    intMapPosY2 = 532;
                    intMapPosX3 = 68+36+36+36+36+36+36+36+36+36;
                    intMapPosY3 = 488;
                    intMapPosX4 = 36+36+36+36+36+36+36+36+36+36;
                    intMapPosY4 = 488;
                    unWiden = true;
                }
                case '.' -> {
                    offset = offset+2*width*0.8f;
                    width = width * 0.3f;
                    intMapPosX1 = 36+36+36+36+36+36+36+36+36+36+36;
                    intMapPosY1 = 532;
                    intMapPosX2 = 68+36+36+36+36+36+36+36+36+36+12;
                    intMapPosY2 = 532;
                    intMapPosX3 = 68+36+36+36+36+36+36+36+36+36+12;
                    intMapPosY3 = 488;
                    intMapPosX4 = 36+36+36+36+36+36+36+36+36+36+36;
                    intMapPosY4 = 488;
                    unWiden = true;
                }
                case '[' -> {
                    offset = offset+4.7f*width;
                    offsetHeight = -0.01f;
                    width = width * 3;
                    intMapPosX1 = 0;
                    intMapPosY1 = 660;
                    intMapPosX2 = 104;
                    intMapPosY2 = 660;
                    intMapPosX3 = 104;
                    intMapPosY3 = 610;
                    intMapPosX4 = 0;
                    intMapPosY4 = 610;
                    unWidenMph = true;
                }
                case 'N' -> {
                    offset = offset+2*width;
                    intMapPosX1 = 0;
                    intMapPosY1 = 898;
                    intMapPosX2 = 35;
                    intMapPosY2 = 898;
                    intMapPosX3 = 35;
                    intMapPosY3 = 854;
                    intMapPosX4 = 0;
                    intMapPosY4 = 854;
                }
                case 'E' -> {
                    offset = offset+2*width;
                    intMapPosX1 = 36;
                    intMapPosY1 = 898;
                    intMapPosX2 = 35+36;
                    intMapPosY2 = 898;
                    intMapPosX3 = 35+36;
                    intMapPosY3 = 854;
                    intMapPosX4 = 35;
                    intMapPosY4 = 854;
                }
                case 'S' -> {
                    offset = offset+2*width;
                    intMapPosX1 = 35+36;
                    intMapPosY1 = 898;
                    intMapPosX2 = 35+36+36;
                    intMapPosY2 = 898;
                    intMapPosX3 = 35+36+36;
                    intMapPosY3 = 854;
                    intMapPosX4 = 35+36;
                    intMapPosY4 = 854;
                }
                case 'W' -> {
                    offset = offset+2*width;
                    intMapPosX1 = 35+36+36;
                    intMapPosY1 = 898;
                    intMapPosX2 = 35+36+36+36;
                    intMapPosY2 = 898;
                    intMapPosX3 = 35+36+36+36;
                    intMapPosY3 = 854;
                    intMapPosX4 = 35+36+36;
                    intMapPosY4 = 854;
                }
                case 'F' -> {
                    offset = offset+2*width;
                    intMapPosX1 = 0;
                    intMapPosY1 = 593;
                    intMapPosX2 = 32;
                    intMapPosY2 = 593;
                    intMapPosX3 = 32;
                    intMapPosY3 = 549;
                    intMapPosX4 = 0;
                    intMapPosY4 = 549;
                }
                case 'M' -> {
                    offset = offset+2.35f*width;
                    offsetHeight = -0.01f;
                    intMapPosX1 = 0;
                    intMapPosY1 = 660;
                    intMapPosX2 = 32;
                    intMapPosY2 = 660;
                    intMapPosX3 = 32;
                    intMapPosY3 = 610;
                    intMapPosX4 = 0;
                    intMapPosY4 = 610;
                    unWidenMph = true;
                }
            }
            renderSpecificSize(transform, xPos + offset,yPos + offsetHeight,intMapPosX1, intMapPosY1, intMapPosX2, intMapPosY2, intMapPosX3, intMapPosY3, intMapPosX4, intMapPosY4, zPos, width, height, resolutionX, resolutionY, numberTexture);
            offsetHeight = 0;
            if (unWiden) {
                width = width / 0.3f;
                unWiden = false;
            }
            if (unWidenMph) {
                width = width / 3;
                unWidenMph = false;
            }
        }
    }

    public static void renderBattery(PoseStack transform, @NotNull ItemStack stack) {
        float energyDecimalPercent = 1;
        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        float size = 0.6f;
        ResourceLocation batteryIcon = battery0;
        if (stack.getTag() != null) {
            energyDecimalPercent = (float) stack.getTag().getInt("CurrentEnergy") / ((Tablet) stack.getItem()).getMaxEnergy();
            if (energyDecimalPercent >= 0.8) {
                batteryIcon = battery5;
            } else if (energyDecimalPercent < 0.8 && energyDecimalPercent >= 0.6) {
                batteryIcon = battery4;
            } else if (energyDecimalPercent < 0.6 && energyDecimalPercent >= 0.4) {
                batteryIcon = battery3;
            } else if (energyDecimalPercent < 0.4 && energyDecimalPercent >= 0.2) {
                batteryIcon = battery2;
            } else if (energyDecimalPercent < 0.2 && energyDecimalPercent >= 0.1) {
                batteryIcon = battery1;
            } else if (energyDecimalPercent < 0.1 && energyDecimalPercent >= 0 && tick >= 40) {
                batteryIcon = battery1;
            }
            if (tick >= 80) {
                tick = 0;
            }
            tick++;
        }
        renderTexture(transform, batteryIcon, 0.6f, 0.75f, 0.00014f, (float) 20 / 100 * size, (float) 10 / 100 * size, true);
        renderNumber(transform, text, decimalFormat.format(energyDecimalPercent * 100)+"%", 0.62f, 0.65f, 0.00014f, 0.011f * 2f, 0.015f * 2f, true);
    }

    public static void renderRectangle(@NotNull PoseStack transform, float x, float y, float z, float width, float height, float r, float g, float b, float a) {
        transform.pushPose();
        transform.translate(x,y,0);
        transform.scale(width,height,1);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_LIGHTMAP);
        bufferBuilder.vertex(transform.last().pose(), 0,0,z).color(r,g,b,a).endVertex();
        bufferBuilder.vertex(transform.last().pose(), 1,0,z).color(r,g,b,a).endVertex();
        bufferBuilder.vertex(transform.last().pose(), 1,1,z).color(r,g,b,a).endVertex();
        bufferBuilder.vertex(transform.last().pose(), 0,1,z).color(r,g,b,a).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());
        transform.popPose();
    }

    public static void renderMap(PoseStack transform, Player player) {
        int[] map = Maps.tabletMap;
        mapRadius = Maps.tabletMapRadius;
        mapResolution = Maps.mapResolution;
        int mapX = Maps.mapX;
        int mapY = Maps.mapY;
        if (map != null) {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.enableDepthTest();
            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            Matrix4f matrix4f = transform.last().pose();
            int i = 0;
            float offsetX = ((float) mapX - (float) player.getX() - 0.5f) / mapResolution;
            float offsetY = -((float) mapY - (float) player.getZ() - 0.5f) / mapResolution;
            for (int x = -mapRadius; x <= mapRadius; x++) {
                for (int z = -mapRadius; z <= mapRadius; z++) {
                    int colour = map[i++] | 0xFF000000;
                    float fx0 = (x - 0.5f + offsetX) / mapRadius;
                    float fy0 = (-z - 0.5f + offsetY) / mapRadius;
                    float fx1 = (x + 0.5f + offsetX) / mapRadius;
                    float fy1 = (-z + 0.5f + offsetY) / mapRadius;
                    bufferBuilder.vertex(matrix4f, fx0, fy0, 0).color(colour).endVertex();
                    bufferBuilder.vertex(matrix4f, fx1, fy0, 0).color(colour).endVertex();
                    bufferBuilder.vertex(matrix4f, fx1, fy1, 0).color(colour).endVertex();
                    bufferBuilder.vertex(matrix4f, fx0, fy1, 0).color(colour).endVertex();
                }
            }
            BufferUploader.drawWithShader(bufferBuilder.end());
        }
    }
}

// im going absolutely mental, positively bonkers even