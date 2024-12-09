package nikosmods.weather2additions.blocks.blockfunction.blockgui;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nikosmods.weather2additions.Weather2Additions;

public class MenuTypes {
    private static final DeferredRegister<MenuType<?>> menuTypes = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Weather2Additions.MODID);
    public static final RegistryObject<MenuType<SmallBatteryBlockMenu>> BATTERY_BLOCK_MENU = menuTypes.register("small_battery_block_menu", () -> IForgeMenuType.create(SmallBatteryBlockMenu::new));
    public static final RegistryObject<MenuType<NetworkInfoMenu>> NETWORK_INFO_MENU = menuTypes.register("network_info_menu", () -> IForgeMenuType.create(NetworkInfoMenu::new));
    public static void register(IEventBus eventBus) {
     menuTypes.register(eventBus);
    }
}
