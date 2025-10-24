package nikosmods.weather2additions.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import nikosmods.weather2additions.Config;
import nikosmods.weather2additions.Weather2Additions;
import nikosmods.weather2additions.mapdata.Maps;
import nikosmods.weather2additions.mapdata.ServerMapRendering;

public class ConfigCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(Weather2Additions.MODID).

                then(Commands.literal("config").

                then(Commands.literal("tablet").

                then(Commands.literal("set_tablet_resolution").then(Commands.argument("resolution", IntegerArgumentType.integer(1)).executes(
                        command -> {
                            if (command.getSource().hasPermission(Config.OP_LEVEL.get())) {
                                Config.TABLET_RESOLUTION.set(IntegerArgumentType.getInteger(command, "resolution"));
                                command.getSource().sendSuccess(() -> Component.literal("Updated map resolution to " + IntegerArgumentType.getInteger(command, "resolution") + " blocks per pixel"), true);
                                return 1;
                            }
                            command.getSource().sendFailure(Component.literal("You do not have the required permissions to execute this command."));
                            return 0;
                        }
                )))

                .then(Commands.literal("set_tablet_radius").then(Commands.argument("radius", IntegerArgumentType.integer(1)).executes(
                        command -> {
                            if (command.getSource().hasPermission(Config.OP_LEVEL.get())) {
                                Config.TABLET_RADIUS.set(IntegerArgumentType.getInteger(command, "radius"));
                                command.getSource().getServer().getPlayerList().getPlayers().forEach(ServerMapRendering::updatePlayerWithImage); // replaced updatePlayer with updatePlayerWIthImage here
                                command.getSource().sendSuccess(() -> Component.literal("Updated tablet mapping radius to " + IntegerArgumentType.getInteger(command, "radius") + " blocks"), true);
                                return 1;
                            }
                            command.getSource().sendFailure(Component.literal("You do not have the required permissions to execute this command."));
                            return 0;
                        }
                )))

                .then(Commands.literal("set_tablet_timer").then(Commands.argument("ticks", IntegerArgumentType.integer(5)).executes(
                        command -> {
                            if (command.getSource().hasPermission(Config.OP_LEVEL.get())) {
                                Config.TABLET_TIMER.set(IntegerArgumentType.getInteger(command, "ticks"));
                                command.getSource().sendSuccess(() -> Component.literal("Updated tablet update rate to " + IntegerArgumentType.getInteger(command, "ticks") + " ticks per update"), true);
                                return 1;
                            }
                            command.getSource().sendFailure(Component.literal("You do not have the required permissions to execute this command."));
                            return 0;
                        }
                ))))));

        dispatcher.register(Commands.literal(Weather2Additions.MODID)

                .then(Commands.literal("config")

                .then(Commands.literal("radar")

                .then(Commands.literal("set_radar_load_radius").then(Commands.argument("blocks", IntegerArgumentType.integer(1)).executes(
                        command -> {
                            if (command.getSource().hasPermission(Config.OP_LEVEL.get())) {
                                Config.RADAR_RADIUS.set(IntegerArgumentType.getInteger(command, "blocks"));
                                command.getSource().sendSuccess(() -> Component.literal("Updated radar loading radius to " + IntegerArgumentType.getInteger(command, "blocks") + " blocks"), true);
                                return 1;
                            }
                            command.getSource().sendFailure(Component.literal("You do not have the required permissions to execute this command."));
                            return 0;
                        }
                )))

                .then(Commands.literal("set_radar_load_chance").then(Commands.argument("number", FloatArgumentType.floatArg()).executes(
                        command -> {
                            if (command.getSource().hasPermission(Config.OP_LEVEL.get())) {
                                Config.RADAR_CHANCE.set((double) FloatArgumentType.getFloat(command, "number"));
                                command.getSource().sendSuccess(() -> Component.literal("Updated load chance factor to " + FloatArgumentType.getFloat(command, "number")), true);
                                return 1;
                            }
                            command.getSource().sendFailure(Component.literal("You do not have the required permissions to execute this command."));
                            return 0;
                        }
                ))))));

        dispatcher.register(Commands.literal(Weather2Additions.MODID)

                        .then(Commands.literal("config")

                        .then(Commands.literal("screen")

                        .then(Commands.literal("set_screen_resolution").then(Commands.argument("resolution", IntegerArgumentType.integer(1)).executes(
                            command -> {
                                if (command.getSource().hasPermission(Config.OP_LEVEL.get())) {
                                    Config.SCREEN_RESOLUTION.set(IntegerArgumentType.getInteger(command, "resolution"));
                                    command.getSource().sendSuccess(() -> Component.literal("Updated map resolution to " + IntegerArgumentType.getInteger(command, "resolution") + " blocks per pixel"), true);
                                    return 1;
                                }
                                command.getSource().sendFailure(Component.literal("You do not have the required permissions to execute this command."));
                                return 0;
                            }
                    )))

                        .then(Commands.literal("set_screen_radius").then(Commands.argument("radius", IntegerArgumentType.integer(1)).executes(
                                command -> {
                                    if (command.getSource().hasPermission(Config.OP_LEVEL.get())) {
                                        Config.SCREEN_RADIUS.set(IntegerArgumentType.getInteger(command, "radius"));
                                        command.getSource().getServer().getPlayerList().getPlayers().forEach(ServerMapRendering::updatePlayerWithImage); // replaced updatePlayer with updatePlayerWIthImage here
                                        command.getSource().sendSuccess(() -> Component.literal("Updated screen mapping radius to " + IntegerArgumentType.getInteger(command, "radius") + " blocks"), true);
                                        return 1;
                                    }
                                    command.getSource().sendFailure(Component.literal("You do not have the required permissions to execute this command."));
                                    return 0;
                                }

                        ))))));





                dispatcher.register(Commands.literal(Weather2Additions.MODID)

                        .then(Commands.literal("config")

                        .then(Commands.literal("server")

                .then(Commands.literal("set_player_load_timer").then(Commands.argument("ticks", IntegerArgumentType.integer(5)).executes(
                        command -> {
                            if (command.getSource().hasPermission(Config.OP_LEVEL.get())) {
                                Config.PLAYER_LOAD_TIMER.set(IntegerArgumentType.getInteger(command, "ticks"));
                                command.getSource().sendSuccess(() -> Component.literal("Updated player loading rate to " + IntegerArgumentType.getInteger(command, "ticks") + " ticks per update"), true);
                                return 1;
                            }
                            command.getSource().sendFailure(Component.literal("You do not have the required permissions to execute this command."));
                            return 0;
                        }
                )))

                .then(Commands.literal("set_player_load_radius").then(Commands.argument("blocks", IntegerArgumentType.integer(1)).executes(
                        command -> {
                            if (command.getSource().hasPermission(Config.OP_LEVEL.get())) {
                                Config.PLAYER_LOAD_RADIUS.set(IntegerArgumentType.getInteger(command, "blocks"));
                                command.getSource().sendSuccess(() -> Component.literal("Updated player loading radius to " + IntegerArgumentType.getInteger(command, "blocks") + " blocks"), true);
                                return 1;
                            }
                            command.getSource().sendFailure(Component.literal("You do not have the required permissions to execute this command."));
                            return 0;
                        }
                )))

                .then(Commands.literal("commands_operator_level").requires(player -> player.hasPermission(3)).then(Commands.argument("Operator permission level", IntegerArgumentType.integer(0, 4))
                        .executes(
                        command -> {
                            Config.OP_LEVEL.set(IntegerArgumentType.getInteger(command, "Operator permission level"));
                            command.getSource().sendSuccess(() -> Component.literal("Required permission level set to level " + Config.OP_LEVEL.get()), true);
                            return 1;
                        }
                )))

                .then(Commands.literal("clear_map").executes(
                        command -> {
                            if (command.getSource().hasPermission(3)) {
                                Maps.otherMap.clear();
                                command.getSource().sendSuccess(() -> Component.literal("Map cleared!"), true);
                                return 1;
                            }
                            command.getSource().sendFailure(Component.literal("You do not have the required permissions to execute this command."));
                            return 0;
                        })))));
    }
}
