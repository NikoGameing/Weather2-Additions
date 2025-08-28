package nikosmods.weather2additions.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import nikosmods.weather2additions.Weather2Additions;
import nikosmods.weather2additions.mapdata.ServerMapRendering;
import org.slf4j.Logger;

public class DebugCommands {

    static Logger logger = Weather2Additions.LOGGER;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("weather2_additions")
                .then(Commands.literal("write_map_to_file")
                        .executes(command -> {
                            ServerMapRendering.writeMapImage(command.getSource().getPlayer());
                            command.getSource().sendSuccess(() -> Component.literal("Image written!"), true);
                            return 1;
                        })));
    }
}