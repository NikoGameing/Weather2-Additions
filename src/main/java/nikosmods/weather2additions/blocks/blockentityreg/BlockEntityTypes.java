package nikosmods.weather2additions.blocks.blockentityreg;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nikosmods.weather2additions.Weather2Additions;
import nikosmods.weather2additions.blocks.CableSmall;
import nikosmods.weather2additions.blocks.blockfunction.CableGenericEntity;
import nikosmods.weather2additions.blocks.blockfunction.CableSmallEntity;
import nikosmods.weather2additions.blocks.blockfunction.RadarBlockEntity;
import nikosmods.weather2additions.blocks.blockfunction.SmallBatteryBlockEntity;
import nikosmods.weather2additions.blocks.blockreg.Blocks;

public class BlockEntityTypes {

    private static final DeferredRegister<BlockEntityType<?>> blockEntityType = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Weather2Additions.MODID);
    public static final RegistryObject<BlockEntityType<SmallBatteryBlockEntity>> SMALL_BATTERY_BLOCK_ENTITY = blockEntityType.register("small_battery_block", () -> BlockEntityType.Builder.of(SmallBatteryBlockEntity::new, Blocks.SMALL_BATTERY_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<CableSmallEntity>> CABLE_BLOCK_ENTITY = blockEntityType.register("cable_small", () -> BlockEntityType.Builder.of(CableSmallEntity::new, Blocks.CABLE_SMALL.get()).build(null));
    public static final RegistryObject<BlockEntityType<RadarBlockEntity>> RADAR_BLOCK_ENTITY = blockEntityType.register("radar_block", () -> BlockEntityType.Builder.of(RadarBlockEntity::new, Blocks.RADAR_BLOCK.get()).build(null));
    public static void register(IEventBus IeventBus) {
        blockEntityType.register(IeventBus);
    }
}

