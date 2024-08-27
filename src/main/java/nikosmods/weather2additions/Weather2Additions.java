package nikosmods.weather2additions;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nikosmods.weather2additions.blocks.blockentityreg.BlockEntityTypes;
import nikosmods.weather2additions.blocks.blockfunction.blockgui.MenuTypes;
import nikosmods.weather2additions.blocks.blockfunction.blockgui.SmallBatteryBlockScreen;
import nikosmods.weather2additions.blocks.blockreg.Blocks;
import nikosmods.weather2additions.keyreg.KeyRegistries;
import nikosmods.weather2additions.tabs.CreativeTabs;
import nikosmods.weather2additions.items.itemreg.Items;
import org.slf4j.Logger;
import nikosmods.weather2additions.network.Messages;

import java.util.concurrent.ThreadLocalRandom;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Weather2Additions.MODID)
public class Weather2Additions {
    private static final Integer randomChance = ThreadLocalRandom.current().nextInt(0, 500 + 1);
    // Define mod id in a common place for everything to reference
    public static final String MODID = "weather2_additions";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Creates a new Block with the id "weather2_additions:example_block", combining the namespace and path
    public Weather2Additions() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the Deferred Register to the mod event bus so items get registered
        BlockEntityTypes.register(modEventBus);
        Blocks.register(modEventBus);
        Items.register(modEventBus);
        CreativeTabs.register(modEventBus);
        MenuTypes.register(modEventBus);
        modEventBus.addListener(Messages::register);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);


        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        // ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // server starting stuff
        if (randomChance == 100) {
            LOGGER.info("""
                                                                                                                                   \s
                                                                      ░░    ░░                                                     \s
                                                      ░░░░░░░░░░░░  ░░  ░░░░  ░░░░░░    ░░░░░░░░░░  ░░                             \s
                                            ░░  ░░  ░░░░░░░░▒▒░░░░░░░░░░░░░░░░  ░░░░░░░░░░  ░░░░  ░░░░▒▒░░░░░░                     \s
                                        ░░▒▒░░  ░░░░░░░░  ░░░░░░░░▒▒▒▒▒▒▒▒▒▒░░░░▒▒  ░░░░░░░░░░░░  ░░  ░░░░▒▒  ░░░░                 \s
                                    ░░░░░░░░░░░░  ░░  ░░    ░░▒▒▒▒▒▒▒▒  ░░▒▒░░▒▒▒▒░░░░▒▒░░░░▒▒▒▒▒▒░░░░  ▒▒▒▒  ░░    ░░             \s
                                  ░░░░▒▒░░▒▒░░    ░░▒▒░░░░▒▒▒▒░░░░▒▒░░░░░░▒▒▒▒░░▒▒░░░░  ░░  ▒▒▒▒░░▒▒▒▒▒▒▒▒▒▒  ▒▒░░░░░░░░           \s
                              ░░░░▒▒░░░░▒▒  ░░░░  ░░▒▒▒▒░░░░░░░░░░░░░░  ░░░░░░    ░░░░░░░░    ░░▒▒▒▒▒▒▒▒▒▒▒▒▒▒░░░░▒▒▒▒▒▒░░         \s
                            ░░░░░░  ░░░░░░░░░░░░░░░░░░░░░░░░░░░░  ░░░░░░░░░░░░  ░░░░  ░░░░░░░░  ░░▒▒▒▒▒▒░░░░░░░░    ░░░░░░░░       \s
                          ░░░░  ░░░░░░░░░░░░░░░░░░  ░░░░░░░░░░░░░░░░░░░░░░░░░░░░  ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░  ▒▒░░       \s
                          ░░▒▒░░░░░░░░░░░░░░░░░░░░░░  ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░  ░░░░░░░░░░  ░░░░░░░░░░░░░░░░     \s
                        ░░░░░░░░  ░░░░  ░░░░  ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░  ░░░░  ░░░░░░░░░░░░░░░░░░░░  ░░░░░░░░  ░░░░░░░░░░     \s
                        ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░     \s
                        ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░     \s
                        ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░     \s
                          ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░    ░░░░░░░░  ░░░░     \s
                          ▒▒░░░░  ░░░░░░░░░░░░░░░░  ░░░░░░░░                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▒▒▒░░   \s
                          ░░▒▒▓▓▒▒░░░░░░░░░░░░░░                                ░░░░          ░░░░░░░░░░  ░░░░░░░░▒▒▒▒▓▓▒▒░░░░░░░░ \s
                          ░░▒▒▒▒▒▒░░▒▒▓▓▓▓░░░░░░░░░░                            ░░  ░░      ░░░░░░░░░░░░░░▒▒▓▓▒▒░░░░░░▒▒▒▒░░░░  ░░ \s
                            ░░░░░░░░░░░░░░▒▒▓▓░░░░▓▓▓▓▓▓▓▓▒▒▒▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒▒██▓▓▓▓▓▓▓▓▓▓▒▒▒▒▓▓▒▒▓▓▒▒░░░░░░░░░░░░░░░░       \s
                            ░░░░░░██░░░░░░░░▒▒▓▓▒▒░░  ░░▒▒▒▒▒▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒▒░░░░▓▓▒▒░░▒▒▒▒▒▒░░░░▒▒▓▓▒▒░░░░░░▒▒▒▒░░░░░░▒▒░░       \s
                          ░░  ░░░░████████▒▒▒▒▒▒▓▓▓▓▒▒░░░░░░▓▓▒▒░░▒▒▒▒▒▒▒▒▒▒░░░░▓▓▓▓▒▒░░░░░░▒▒▒▒▒▒░░▒▒░░░░▒▒▒▒▓▓▒▒░░░░░░░░░░░░     \s
                          ▒▒▒▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓████▓▓▓▓▓▓░░░░░░▒▒▒▒░░░░░░░░░░░░░░░░▒▒▒▒▒▒░░░░▒▒▒▒▒▒▓▓░░░░░░▒▒▓▓▓▓▓▓▒▒░░░░▓▓▓▓▓▓▒▒░░   \s
                        ░░▒▒▒▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒▒░░░░▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒░░░░░░░░░░▓▓▓▓▒▒▓▓▒▒▒▒░░░░░░
                        ▒▒▒▒▒▒▒▒▒▒▓▓▓▓▒▒▓▓▓▓▒▒▓▓▒▒▒▒▓▓▓▓▓▓▓▓██▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒░░░░░░░░          ▒▒▒▒▒▒▓▓▓▓▒▒░░░░░░   \s
                      ░░░░▒▒░░░░░░▒▒▓▓▓▓▒▒▒▒▓▓▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒▒                        ░░▓▓▒▒▒▒▒▒▓▓▓▓▒▒░░  ░░   \s
                        ░░░░    ░░░░░░░░▒▒▓▓██▓▓██▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒░░                  ▓▓▓▓▒▒▒▒▓▓▓▓▒▒░░    ░░   \s
                          ░░        ░░░░░░░░░░▒▒▒▒▒▒▓▓▓▓▓▓▒▒▒▒▓▓▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓░░          ▒▒▓▓▓▓▓▓▒▒▒▒░░░░           \s
                          ░░    ░░░░                ░░░░░░░░▒▒▒▒▒▒▒▒▓▓▓▓▒▒▒▒▒▒▓▓▒▒▓▓▒▒▒▒▒▒▓▓▓▓▓▓▓▓      ▒▒░░░░░░░░                 \s
                                          ░░                    ░░░░░░░░░░░░░░░░▒▒▓▓██▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░▒▒▒▒░░▒▒░░░░░░░░░░░░       \s
                              ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▒▒▒▒▒▒▒░░░░▒▒░░░░▒▒▒▒▒▒░░░░▒▒▒▒░░▒▒░░░░░░░░░░░░░░░░░░░░░░     \s
                              ░░▒▒░░░░░░░░░░  ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░  ░░░░░░  ░░░░░░  ░░░░  ░░░░░░░░░░░░░░░░░░       \s
                              ░░░░▒▒▒▒░░░░░░░░░░░░░░  ░░  ░░░░░░░░░░  ░░░░░░░░░░░░░░  ░░░░░░░░░░░░░░░░░░  ░░░░░░░░░░░░░░░░░░░░     \s
                              ░░░░░░░░▒▒░░░░░░░░░░░░░░░░  ░░░░░░░░░░░░  ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▒░░░░░░       \s
                                    ░░░░░░▒▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░  ░░  ░░░░░░░░░░░░░░░░               \s
                                              ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░                       \s
                                                              ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░                                       \s
                     """);
        }
        else {
            LOGGER.info("╬════════════════════════════════════╬\n" +
                    "║█░░░░░░░░░█░░███████░░░░░░░░░█░░░░░░║\n" +
                    "║█░░░░░░░░░█░█░░░░░░██░░░░░░░█░█░░░░░║\n" +
                    "║░█░░░░░░░█░░░░░░░██░░░░░░░░█░░░█░░░░║\n" +
                    "║░█░░░░░░░█░░░░░██░░░░░░░░░█░░░░░█░░░║\n" +
                    "║░░█░░█░░█░░░░░█░░░░░░░░░░█████████░░║\n" +
                    "║░░█░█░█░█░░░░█░░░░░░░░░░█░░░░░░░░░█░║\n" +
                    "║░░░█░░░█░░░░█████████░░█░░░░░░░░░░░█║\n" +
                    "╬════════════════════════════════════╬\n" +
                    "Weather2 Additions loaded successfully!\n");
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("Weather2 Additions loaded successfully!");
            event.enqueueWork(() -> MenuScreens.register(MenuTypes.BATTERY_BLOCK_MENU.get(), SmallBatteryBlockScreen::new));
        }

        @SubscribeEvent
        public static void onKeyEvent(RegisterKeyMappingsEvent RegKeyEvent) {
            KeyRegistries.register(RegKeyEvent);
        }
    }
}

