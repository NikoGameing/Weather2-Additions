package nikosmods.weather2additions.items.itemreg;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nikosmods.weather2additions.Weather2Additions;
import nikosmods.weather2additions.blocks.blockreg.Blocks;
import nikosmods.weather2additions.items.itemfunction.ItemTablet;

public class Items {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Weather2Additions.MODID);

    public static final RegistryObject<Item> TABLET = ITEMS.register("tablet", () -> new ItemTablet(new Item.Properties().stacksTo(1), 50000));
    public static final RegistryObject<Item> SMALL_BATTERY_BLOCK = ITEMS.register("small_battery_block", () -> new BlockItem(Blocks.SMALL_BATTERY_BLOCK.get(), new Item.Properties()));
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
