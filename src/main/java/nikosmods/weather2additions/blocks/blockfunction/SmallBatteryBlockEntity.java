package nikosmods.weather2additions.blocks.blockfunction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import nikosmods.weather2additions.Weather2Additions;
import nikosmods.weather2additions.blocks.blockentityreg.BlockEntityTypes;
import nikosmods.weather2additions.blocks.blockfunction.blockgui.SmallBatteryBlockMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;
import java.util.logging.Logger;

public class SmallBatteryBlockEntity extends BlockEntity implements MenuProvider {

    private int previousCharging = 0;
    private int differenceCharging = 0;
    private int previousDischarging = 0;
    private int differenceDischarging = 0;
    private int lastEnergy = 0;
    private int changeEnergy = 0;
    private boolean wasEmpty0 = true;
    private boolean wasEmpty1 = true;


    private final BlockEnergyStorage blockEnergyStorage = new BlockEnergyStorage(this, 50000, 50000, 250);
    private final ItemStackHandler stackHandler = new ItemStackHandler(2) {

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    };

    public static void tick(Level level, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
        if (blockEntity instanceof SmallBatteryBlockEntity batteryEntity && !level.isClientSide()) {
            ItemStack charging = batteryEntity.stackHandler.getStackInSlot(0);
            ItemStack discharging = batteryEntity.stackHandler.getStackInSlot(1);
            charging.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> batteryEntity.blockEnergyStorage.extractEnergy(energy.receiveEnergy(batteryEntity.blockEnergyStorage.extractEnergy(batteryEntity.blockEnergyStorage.getThroughputOut(), true), false), false));
            discharging.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> batteryEntity.blockEnergyStorage.receiveEnergy(energy.extractEnergy(batteryEntity.blockEnergyStorage.receiveEnergy(batteryEntity.blockEnergyStorage.getThroughputIn(), true), false), false));

            if (!charging.isEmpty() && !batteryEntity.wasEmpty0 && charging.getCapability(ForgeCapabilities.ENERGY).resolve().isPresent()) {
                batteryEntity.differenceCharging = charging.getCapability(ForgeCapabilities.ENERGY).resolve().orElseThrow().getEnergyStored() - batteryEntity.previousCharging;
                batteryEntity.previousCharging = charging.getCapability(ForgeCapabilities.ENERGY).resolve().orElseThrow().getEnergyStored();
            }
            else if (!charging.isEmpty() && batteryEntity.wasEmpty0 && charging.getCapability(ForgeCapabilities.ENERGY).resolve().isPresent()) {
                batteryEntity.previousCharging = charging.getCapability(ForgeCapabilities.ENERGY).resolve().orElseThrow().getEnergyStored();
                batteryEntity.differenceCharging = 0;
            }
            else if (charging.isEmpty()) {
                batteryEntity.differenceCharging = 0;
            }

            if (!discharging.isEmpty() && !batteryEntity.wasEmpty1 && discharging.getCapability(ForgeCapabilities.ENERGY).isPresent()) {
                batteryEntity.differenceDischarging = batteryEntity.previousDischarging - discharging.getCapability(ForgeCapabilities.ENERGY).resolve().orElseThrow().getEnergyStored();
                batteryEntity.previousDischarging = discharging.getCapability(ForgeCapabilities.ENERGY).resolve().orElseThrow().getEnergyStored();
            }
            else if (!discharging.isEmpty() && batteryEntity.wasEmpty1 && discharging.getCapability(ForgeCapabilities.ENERGY).isPresent()) {
                batteryEntity.differenceDischarging = 0;
                batteryEntity.previousDischarging = discharging.getCapability(ForgeCapabilities.ENERGY).resolve().orElseThrow().getEnergyStored();
            }
            else if (discharging.isEmpty()) {
                batteryEntity.differenceDischarging = 0;
            }

            batteryEntity.wasEmpty0 = charging.isEmpty();
            batteryEntity.wasEmpty1 = discharging.isEmpty();

            batteryEntity.changeEnergy = batteryEntity.blockEnergyStorage.getEnergyStored() - batteryEntity.lastEnergy;
            batteryEntity.lastEnergy = batteryEntity.blockEnergyStorage.getEnergyStored();
            BlockEntity blockEntityBelow = level.getBlockEntity(blockPos.below());
            if (blockEntityBelow != null) {
                blockEntityBelow.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> energy.receiveEnergy(batteryEntity.blockEnergyStorage.extractEnergy(energy.receiveEnergy(batteryEntity.blockEnergyStorage.getThroughputOut(), true), false), false));
            }
        }
    }

    public int getItemDifferenceCharging() {
        return differenceCharging;
    }

    public int getItemDifferenceDischarging() {
        return differenceDischarging;
    }

    public int getItemChargingEnergy() {
        if (stackHandler.getStackInSlot(0).getCapability(ForgeCapabilities.ENERGY).isPresent()) {
            return stackHandler.getStackInSlot(0).getCapability(ForgeCapabilities.ENERGY).resolve().orElseThrow().getEnergyStored();
        }
        return 0;
    }

    public int getItemDischargingEnergy() {
        if (stackHandler.getStackInSlot(1).getCapability(ForgeCapabilities.ENERGY).isPresent()) {
            return stackHandler.getStackInSlot(1).getCapability(ForgeCapabilities.ENERGY).resolve().orElseThrow().getEnergyStored();
        }
        return 0;
    }

    public int getCurrentEnergy() {
        return blockEnergyStorage.getEnergyStored();
    }

    public int getMaxEnergy() {
        return blockEnergyStorage.getMaxEnergyStored();
    }

    public int getEnergyDifference() {
        return changeEnergy;
    }

    public ItemStackHandler getStackHandler() {
        return stackHandler;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putInt("Energy", blockEnergyStorage.getEnergyStored());
        tag.put("Inventory", stackHandler.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        blockEnergyStorage.setEnergyStored(tag.getInt("Energy"));
        stackHandler.deserializeNBT(tag.getCompound("Inventory"));
        super.load(tag);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY && (side == Direction.UP)) {
            return LazyOptional.of(() -> blockEnergyStorage).cast();
        }
        else if (cap == ForgeCapabilities.ENERGY && (side == Direction.DOWN)) {
            return LazyOptional.of(DummyStorage::new).cast();
        }
        return super.getCapability(cap, side);
    }

    public SmallBatteryBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityTypes.SMALL_BATTERY_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("menu.weather2_additions.smallbatteryblock");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new SmallBatteryBlockMenu(i, inventory, this, true);
    }
}