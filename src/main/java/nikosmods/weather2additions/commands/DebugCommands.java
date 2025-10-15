package nikosmods.weather2additions.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import nikosmods.weather2additions.Weather2Additions;
import nikosmods.weather2additions.mapdata.ServerMapRendering;
import org.slf4j.Logger;

public class DebugCommands {

    static Logger logger = Weather2Additions.LOGGER;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("weather2_additions")
                .then(Commands.literal("write_player_map_to_file")
                        .executes(command -> {
                            ServerMapRendering.writePlayerMapImage(command.getSource().getPlayer());
                            command.getSource().sendSuccess(() -> Component.literal("Image written!"), true);
                            return 1;
                        })));
        dispatcher.register(Commands.literal("weather2_additions")
                .then(Commands.literal("write_block_map_to_file")
                        .executes(command -> {
                            HitResult potentialBlockEntity = command.getSource().getPlayer().pick(5,0,false);
                            if (potentialBlockEntity.getType() == HitResult.Type.BLOCK) {
                                Vec3 location = potentialBlockEntity.getLocation();
                                ServerMapRendering.writeBlockMapImage(command.getSource().getLevel().getBlockEntity(new BlockPos((int) location.x, (int) location.y, (int) location.z)));
                                command.getSource().sendSuccess(() -> Component.literal("Image written!"), true);
                                return 1;
                            }
                            else {
                                command.getSource().sendFailure(Component.literal("Incorrect hit type; aim at a block to generate its image"));
                                return 0;
                            }
                        })));
    }
}