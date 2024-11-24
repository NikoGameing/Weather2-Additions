package nikosmods.weather2additions.blocks.blockreg;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nikosmods.weather2additions.Weather2Additions;
import nikosmods.weather2additions.blocks.Cable;
import nikosmods.weather2additions.blocks.SmallBatteryBlock;

public class Blocks {
    private static final DeferredRegister<Block> blocks = DeferredRegister.create(ForgeRegistries.BLOCKS, Weather2Additions.MODID);
    public static final RegistryObject<Block> SMALL_BATTERY_BLOCK = blocks.register("small_battery_block", () -> new SmallBatteryBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.STONE)));
    public static final RegistryObject<Block> CABLE = blocks.register("cable", () -> new Cable(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.STONE)));
    public static void register(IEventBus modEventBus) {
        blocks.register(modEventBus);
    }
}

