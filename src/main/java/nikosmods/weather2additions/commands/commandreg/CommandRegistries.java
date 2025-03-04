package nikosmods.weather2additions.commands.commandreg;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nikosmods.weather2additions.Weather2Additions;
import nikosmods.weather2additions.commands.ConfigCommands;
import nikosmods.weather2additions.commands.DebugCommands;

@Mod.EventBusSubscriber(modid = Weather2Additions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandRegistries {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        ConfigCommands.register(event.getDispatcher());
        DebugCommands.register(event.getDispatcher());
    }
}
