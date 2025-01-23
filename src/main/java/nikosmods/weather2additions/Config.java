// TODO: configs
package nikosmods.weather2additions;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Weather2Additions.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue RESOLUTION = BUILDER
            .comment("Resolution of mapped blocks (1 = 1 block, 16 = 1 chunk)")
            .defineInRange("mapResolution", 16, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue RADAR_TIMER = BUILDER
            .comment("How often the radar should query random chunks (1 = 1 tick, 20 = 1 second) (Small values will cause lag on non-pregenerated worlds)")
            .defineInRange("maxTimer",20, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue RADAR_CHANCE = BUILDER
            .comment("The odds of the radar mapping a chunk (1.0 / (Math.abs(x) + Math.abs(z)) * loadChance > Math.random; basically pseudo-increases distance) (Small values will cause lag  on non-pregenerated worlds)")
            .defineInRange("loadChance", 120, 1, Integer.MAX_VALUE);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
    }

}
