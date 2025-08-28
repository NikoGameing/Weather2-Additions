package nikosmods.weather2additions.items.itemreg;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nikosmods.weather2additions.Weather2Additions;
import nikosmods.weather2additions.blocks.blockreg.Blocks;
import nikosmods.weather2additions.items.SmallBattery;
import nikosmods.weather2additions.items.Tablet;
import nikosmods.weather2additions.items.itemfunction.NetworkAnalyser;

public class Items {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Weather2Additions.MODID);

    // Items that use energy
    public static final RegistryObject<Item> TABLET = ITEMS.register("tablet", () -> new Tablet(new Item.Properties().stacksTo(1), 25000, 7500, 0));
    public static final RegistryObject<Item> SMALL_BATTERY = ITEMS.register("small_battery", () -> new SmallBattery(new Item.Properties().stacksTo(1), 10000, 100, 100));
    // Functional blocks
    public static final RegistryObject<Item> SMALL_BATTERY_BLOCK = ITEMS.register("small_battery_block", () -> new BlockItem(Blocks.SMALL_BATTERY_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> CABLE_SMALL = ITEMS.register("cable_small", () -> new BlockItem(Blocks.CABLE_SMALL.get(), new Item.Properties()));
    public static final RegistryObject<Item> ANALYSER = ITEMS.register("network_analyser", () -> new NetworkAnalyser(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RADAR_BLOCK = ITEMS.register("radar_block", () -> new BlockItem(Blocks.RADAR_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> SCREEN_BLOCK = ITEMS.register("screen_block", () -> new BlockItem(Blocks.SCREEN_BLOCK.get(), new Item.Properties()));
    // Crafting ingredients
    public static final RegistryObject<Item> DETECTION_COMPONENTS = ITEMS.register("detection_components", () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
