package nikosmods.weather2additions.blocks.blockfunction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import nikosmods.weather2additions.blocks.RadarBlock;
import nikosmods.weather2additions.blocks.blockentityreg.BlockEntityTypes;
import nikosmods.weather2additions.blocks.blockfunction.blockgui.RadarBlockMenu;
import nikosmods.weather2additions.mapdata.Maps;
import nikosmods.weather2additions.items.itemfunction.Column;
import nikosmods.weather2additions.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class RadarBlockEntity extends BlockEntity implements MenuProvider {

    public static Map<Column, Integer> otherMap = Maps.otherMap;
    static int serverMapLoadRadius = Config.RADAR_RADIUS.get();
    public static int serverResolution = Config.RESOLUTION.get();
    static int maxTimer = Config.RADAR_TIMER.get();
    private static int timer = maxTimer;

    private int lastEnergy;
    private int changeEnergy;
    private final int consumptionRate = 25;

    private double spin;
    private double lastSpin;
    private double spinFactor;

    private final ItemStackHandler stackHandler = new ItemStackHandler(1);

    private final BlockEnergyStorage blockEnergyStorage = new BlockEnergyStorage(this, 5000, 250, 0);

    public double getSpin() {
        return spin;
    }
    public double getLastSpin() {
        return lastSpin;
    }
    public double getSpinFactor() {
        return spinFactor;
    }

    public ItemStackHandler getStackHandler() {
        return stackHandler;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
        maxTimer = Config.RADAR_TIMER.get();
        RadarBlockEntity radarBlockEntity = (RadarBlockEntity) blockEntity;
        ItemStack battery = radarBlockEntity.stackHandler.getStackInSlot(0);
        battery.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> radarBlockEntity.blockEnergyStorage.receiveEnergy(energy.extractEnergy(radarBlockEntity.blockEnergyStorage.receiveEnergy(radarBlockEntity.blockEnergyStorage.getThroughputIn(), true), false), false));
        if (!level.isClientSide()) {
            if (radarBlockEntity.isPowered() && !state.getValue(RadarBlock.POWERED)) {
            level.setBlock(blockPos, state.setValue(RadarBlock.POWERED, true), 2);
        }
        else if (!radarBlockEntity.isPowered() && state.getValue(RadarBlock.POWERED)) {
            level.setBlock(blockPos, state.setValue(RadarBlock.POWERED, false), 2);
        }
            if (state.getValue(RadarBlock.POWERED)) {
                radarBlockEntity.blockEnergyStorage.consumeEnergy(radarBlockEntity.blockEnergyStorage.consumeEnergy(radarBlockEntity.consumptionRate, true), false);
                if (timer < 0) {
                    loadAroundBlock(blockPos, (ServerLevel) level);
                    timer = maxTimer;
                }
                else {
                    timer -= 1;
                }
            }
        }
        else {
            if (state.getValue(RadarBlock.POWERED)) {
                if (radarBlockEntity.spinFactor < 1) {
                    radarBlockEntity.spinFactor += 0.01;
                }
            } else if (!state.getValue(RadarBlock.POWERED)) {

                if (radarBlockEntity.spinFactor > 0) {
                    radarBlockEntity.spinFactor -= 0.01;
                }
            }
            radarBlockEntity.lastSpin = radarBlockEntity.spin;
            radarBlockEntity.spin = (radarBlockEntity.spin + (5 * (radarBlockEntity.spinFactor)));
            if (radarBlockEntity.spin > 360) {
                radarBlockEntity.lastSpin = radarBlockEntity.lastSpin - 360;
                radarBlockEntity.spin = radarBlockEntity.spin - 360;
            }
        }
        radarBlockEntity.changeEnergy = radarBlockEntity.blockEnergyStorage.getEnergyStored() - radarBlockEntity.lastEnergy;
        radarBlockEntity.lastEnergy = radarBlockEntity.blockEnergyStorage.getEnergyStored();
    }

    public static void loadAroundBlock(BlockPos blockPos, ServerLevel level) {
        if (level != null) {
            serverResolution = Config.RESOLUTION.get();
            serverMapLoadRadius = Config.RADAR_RADIUS.get();
            for (int x = -serverMapLoadRadius; x <= serverMapLoadRadius; x ++) {
                for (int z = -serverMapLoadRadius; z <= serverMapLoadRadius; z++) {
                    if (choose(x, z)) {
                        int worldX = blockPos.getX() / serverResolution * serverResolution + x * serverResolution;
                        int worldY = blockPos.getZ() / serverResolution * serverResolution + z * serverResolution;
                        Column column = new Column(worldX, worldY, level);
                        int colour = getColour(worldX, worldY, level);
                        if (colour != 0) {
                            otherMap.put(column, colour);
                        }
                    }
                }
            }
        }
    }

    public static int fixColour(int colour, float dim) {
        dim = Mth.clamp(dim, 0f, 1f);
        int r = (int) (((colour >> 16) & 0xFF) * dim);
        int g = (int) (((colour >> 8) & 0xFF) * dim);
        int b = (int) ((colour & 0xFF) * dim);
        return b << 16 | g << 8 | r;
    }

    public static int brightenColour(int colour, float brighten) {
        brighten = Mth.clamp(brighten, 0f, 1f);
        int b = ((colour >> 16) & 0xFF) | (int) (0xFF * brighten);
        int g = ((colour >> 8) & 0xFF) | (int) (0xFF * brighten);
        int r = (colour & 0xFF) | (int) (0xFF * brighten);
        return b << 16 | g << 8 | r;
    }

    public static int getColour(int x, int z, Level level) {
        int Colour = 0;
        for (int height = 320; height >= -64; height--) {
            BlockState blockState = level.getBlockState(new BlockPos(x, height, z));
            if (!blockState.isAir()) {
                Colour = blockState.getMapColor(level, new BlockPos(x, height, z)).calculateRGBColor(MapColor.Brightness.NORMAL);
                Colour = fixColour(Colour, ((float) (height + 56) / 220));
                if (Colour != 0) {
                    if (!blockState.canOcclude()) {
                        Colour = brightenColour(Colour, 0.15f);
                    }
                    break;
                }
            }
        }
        return Colour;
    }

    public static boolean choose(int x, int z) {
        // bigger = less likely (acts as a multiplier to pretend that the distance is longer)
        float loadChance = Config.RADAR_CHANCE.get().floatValue();
        double distance = (Math.abs(x) + Math.abs(z)) * loadChance;
        double random = Math.random();
        if (distance == 0) {
            return true;
        }
        else {
            return 1.0 / distance > random;
        }
    }

    public boolean isPowered() {
        return blockEnergyStorage.getEnergyStored() != 0;
    }

    public RadarBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityTypes.RADAR_BLOCK_ENTITY.get(), blockPos, blockState);
        if (blockState.getValue(RadarBlock.POWERED)) {
            spinFactor = 1;
        }
    }



    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("menu.weather2_additions.radarblockscreen");
    }



    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("Inventory", stackHandler.serializeNBT());
        tag.putInt("Energy", blockEnergyStorage.getEnergyStored());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        stackHandler.deserializeNBT(tag.getCompound("Inventory"));
        blockEnergyStorage.setEnergyStored(tag.getInt("Energy"));
        super.load(tag);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY && side == Direction.DOWN) {
            return LazyOptional.of(() -> blockEnergyStorage).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new RadarBlockMenu(i, inventory, this, true);
    }

    private final DataSlot currentStoredEnergyData = new DataSlot() {
        @Override
        public int get() {
            return blockEnergyStorage.getEnergyStored();
        }

        @Override
        public void set(int i) {

        }
    };

    private final DataSlot maxEnergyStoredData = new DataSlot() {
        @Override
        public int get() {
            return blockEnergyStorage.getMaxEnergyStored();
        }

        @Override
        public void set(int i) {

        }
    };

    private final DataSlot changeEnergyData = new DataSlot() {
        @Override
        public int get() {
            return changeEnergy;
        }

        @Override
        public void set(int i) {

        }
    };

    private final DataSlot radiusData = new DataSlot() {
        @Override
        public int get() {
            return Config.RADAR_RADIUS.get();
        }

        @Override
        public void set(int i) {

        }
    };

    private final DataSlot throughputData = new DataSlot() {
        @Override
        public int get() {
            return consumptionRate;
        }

        @Override
        public void set(int i) {

        }
    };

    public DataSlot getCurrentStoredEnergyData() {
        return currentStoredEnergyData;
    }
    public DataSlot getMaxEnergyStoredData() {
        return maxEnergyStoredData;
    }
    public DataSlot getChangeEnergyData() {
        return changeEnergyData;
    }
    public DataSlot getRadiusData() {
        return radiusData;
    }
    public DataSlot getThroughputData() {
        return throughputData;
    }
}
