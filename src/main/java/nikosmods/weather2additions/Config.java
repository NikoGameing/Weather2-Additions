package nikosmods.weather2additions;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Weather2Additions.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder()
            .comment("These configs will persist across saves on Singleplayer.\n");


    public static final ForgeConfigSpec.IntValue RESOLUTION = BUILDER
            .comment("Resolution of mapped blocks (1 = 1 block, 16 = 1 chunk, will reduce radius)")
            .defineInRange("mapResolution", 16, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue RADAR_TIMER = BUILDER
            .comment("How often the radar should query random chunks (1 = 1 tick, 20 = 1 second) (Small values will cause lag!)")
            .defineInRange("radarMaxTimer",20, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue RADAR_CHANCE = BUILDER
            .comment("The odds of the radar mapping a chunk (1.0 / (Math.abs(x) + Math.abs(z)) * loadChance > Math.random; basically pseudo-increases distance) (Small values will cause lag on non-pregenerated worlds)")
            .defineInRange("radarLoadChance", 120, Float.MIN_VALUE, Float.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue RADAR_RADIUS = BUILDER
            .comment("The radius of the blocks that will be loaded around the radar in blocks (Not limited by server render distance!)")
            .defineInRange("loadRadius", 36, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue TABLET_TIMER = BUILDER
            .comment("How often the tablet should query random chunks (1 = 1 tick, 20 = 1 second) (Small values will cause lag or become buggy!)")
            .defineInRange("tabletLoadChance", 20, 5, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue TABLET_RADIUS = BUILDER
            .comment("The radius of the tablet map (low values tend to be buggy, large values tend to be laggy)")
            .defineInRange("tabletRadius", 50, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue PLAYER_LOAD_TIMER = BUILDER
            .comment("Rate at which pixels will be plotted around the player")
            .defineInRange("loadRate", 13, 5, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue PLAYER_LOAD_RADIUS = BUILDER
            .comment("The radius of the blocks that will be loaded around the player passively in blocks (Limited by server render distance!)")
            .defineInRange("loadRadius", 6, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue SCREEN_REFRESH_RATE = BUILDER
            .comment("Rate at which the Screen Block will update (in ticks)")
            .defineInRange("refreshRate", 20, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue OP_LEVEL = BUILDER
            .comment("What Operator level is required to run config commands")
            .defineInRange("opLevel", 0, 0, 4);

    static final ForgeConfigSpec SPEC = BUILDER.build();

}
