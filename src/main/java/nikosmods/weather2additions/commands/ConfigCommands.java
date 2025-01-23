package nikosmods.weather2additions.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import nikosmods.weather2additions.Config;

public class ConfigCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("set_resolution")
                .then(Commands.argument("resolution", IntegerArgumentType.integer())
                        .executes(command -> {
                            Config.RESOLUTION.set(IntegerArgumentType.getInteger(command, "amount"));
                            return 1;
                        })));
    }
}
