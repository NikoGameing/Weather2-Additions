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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import nikosmods.weather2additions.blocks.RadarBlock;
import nikosmods.weather2additions.blocks.blockentityreg.BlockEntityTypes;
import nikosmods.weather2additions.items.itemfunction.Column;
import nikosmods.weather2additions.items.itemfunction.ItemTablet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class RadarBlockEntity extends BlockEntity implements MenuProvider {

    public static Map<Column, Integer> otherMap = new HashMap<>();
    static int serverMapLoadRadius = 64;
    public static int serverResolution = ItemTablet.resolution;
    static int maxTimer = 20;
    private static int timer = maxTimer;

    private double spin;
    private double lastSpin;
    private double spinFactor;

    private final BlockEnergyStorage blockEnergyStorage = new BlockEnergyStorage(this, 5000, 250, 0);

    public double getSpin() {
        return spin;
    }
    public double getLastSpin() {
        return lastSpin;
    }

    public static void loadAroundBlock(BlockPos blockPos, ServerLevel level) {
        if (level != null) {
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
        for (int height = 320; height >= -64; height --) {
            BlockPos blockPos = new BlockPos(x, height, z);
            if (level.isLoaded(blockPos)) {
                BlockState blockState = level.getBlockState(blockPos);
                if (!blockState.isAir()) {
                    Colour = blockState.getMapColor(level, blockPos).calculateRGBColor(MapColor.Brightness.NORMAL);
                    Colour = fixColour(Colour, ((float) (height + 56) / 220));
                    if (Colour != 0) {
                        if (!blockState.canOcclude()) {
                            Colour = brightenColour(Colour, 0.15f);
                        }
                        break;
                    }
                }
            }
        }
        return Colour;
    }

    public static boolean choose(int x, int z) {
        double distance = (Math.abs(x) + Math.abs(z)) * 0.5;
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

    public static void tick(Level level, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
        RadarBlockEntity radarBlockEntity = (RadarBlockEntity) blockEntity;
        if (radarBlockEntity.isPowered() && !state.getValue(RadarBlock.POWERED)) {
            level.setBlock(blockPos, state.setValue(RadarBlock.POWERED, true), 2);
        }
        else if (state.getValue(RadarBlock.POWERED)) {
            level.setBlock(blockPos, state.setValue(RadarBlock.POWERED, false), 2);
        }
        if (!level.isClientSide()) {
            if (timer < 0 || state.getValue(RadarBlock.POWERED)) {
                loadAroundBlock(blockPos, (ServerLevel) level);
                timer = maxTimer;
            } else if (state.getValue(RadarBlock.POWERED)) {
                timer -= 1;
            }
        }
            else {
            if (state.getValue(RadarBlock.POWERED)) {

                if (radarBlockEntity.spinFactor < 1) {
                    radarBlockEntity.spinFactor += 0.005;
                }
            } else if (!state.getValue(RadarBlock.POWERED)) {

                if (radarBlockEntity.spinFactor > 0) {
                    radarBlockEntity.spinFactor -= 0.005;
                }
            }
            radarBlockEntity.lastSpin = radarBlockEntity.spin;
            radarBlockEntity.spin = (radarBlockEntity.spin + (1000.0F / (radarBlockEntity.spinFactor + 200.0F)));
            if (radarBlockEntity.spin > 360) {
                radarBlockEntity.lastSpin = radarBlockEntity.lastSpin - 360;
                radarBlockEntity.spin = radarBlockEntity.spin - 360;
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("menu.weather2_additions.radarblockscreen");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putInt("Energy", blockEnergyStorage.getEnergyStored());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
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
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return null;
    }
}
