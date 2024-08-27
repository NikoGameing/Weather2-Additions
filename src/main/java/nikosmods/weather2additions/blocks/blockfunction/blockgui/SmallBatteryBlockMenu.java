package nikosmods.weather2additions.blocks.blockfunction.blockgui;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import nikosmods.weather2additions.blocks.blockfunction.SmallBatteryBlockEntity;
import nikosmods.weather2additions.blocks.blockreg.Blocks;


public class SmallBatteryBlockMenu extends AbstractContainerMenu {

    private final SmallBatteryBlockEntity smallBatteryBlock;
    private final DataSlot dataSlotCharging;
    private final DataSlot dataSlotDischarging;
    private final DataSlot dataSlotChargingEnergy;
    private final DataSlot dataSlotDischargingEnergy;
    private final DataSlot dataSlotMaximumEnergy;
    private final DataSlot dataSlotCurrentEnergy;
    private final DataSlot dataSlotChangeEnergy;

    public SmallBatteryBlockMenu(int containerID, Inventory inventory, SmallBatteryBlockEntity smallBatteryBlock, boolean server) {
        super(MenuTypes.BATTERY_BLOCK_MENU.get(), containerID);
        this.smallBatteryBlock = smallBatteryBlock;
        addPlayerHotbar(inventory);
        addPlayerInventory(inventory);
        addSlot(new SlotItemHandler(smallBatteryBlock.getStackHandler(), 0, 8,40));
        addSlot(new SlotItemHandler(smallBatteryBlock.getStackHandler(), 1, 152,60));
        dataSlotCharging = server?smallBatteryBlock.getChargingDataSlot():DataSlot.standalone();
        dataSlotDischarging = server?smallBatteryBlock.getDischargingDataSlot():DataSlot.standalone();
        dataSlotChargingEnergy = server?smallBatteryBlock.getChargingEnergy():DataSlot.standalone();
        dataSlotDischargingEnergy = server?smallBatteryBlock.getDischargingEnergy():DataSlot.standalone();
        dataSlotMaximumEnergy = server?smallBatteryBlock.getMaximumEnergy():DataSlot.standalone();
        dataSlotCurrentEnergy = server?smallBatteryBlock.getCurrentStoredEnergyData():DataSlot.standalone();
        dataSlotChangeEnergy = server?smallBatteryBlock.getChangeInEnergy():DataSlot.standalone();
        addDataSlot(dataSlotCharging);
        addDataSlot(dataSlotDischarging);
        addDataSlot(dataSlotChargingEnergy);
        addDataSlot(dataSlotDischargingEnergy);
        addDataSlot(dataSlotMaximumEnergy);
        addDataSlot(dataSlotCurrentEnergy);
        addDataSlot(dataSlotChangeEnergy);
    }

    public DataSlot getDataSlotCharging() {
        return dataSlotCharging;
    }

    public DataSlot getDataSlotDischarging() {
        return dataSlotDischarging;
    }

    public DataSlot getDataSlotChargingEnergy() {
        return dataSlotChargingEnergy;
    }

    public DataSlot getDataSlotDischargingEnergy() {
        return dataSlotDischargingEnergy;
    }

    public DataSlot getDataSlotMaximumEnergy() {
        return dataSlotMaximumEnergy;
    }

    public DataSlot getDataSlotCurrentEnergy() {
        return dataSlotCurrentEnergy;
    }

    public DataSlot getDataSlotChangeEnergy() {
        return dataSlotChangeEnergy;
    }

    public SmallBatteryBlockMenu(int containerID, Inventory inventory, FriendlyByteBuf byteBuffer) {
        this(containerID, inventory, (SmallBatteryBlockEntity) inventory.player.level().getBlockEntity(byteBuffer.readBlockPos()), false);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(smallBatteryBlock.getLevel(), smallBatteryBlock.getBlockPos()), player, Blocks.SMALL_BATTERY_BLOCK.get());
    }

    private void addPlayerInventory(Inventory pPlayerInventory) {
        for (int i = 0; i < 3; i++) {
            for (int l = 0; l < 9; l++) {
                addSlot(new Slot(pPlayerInventory, l + i * 9 + 9, 8 + l * 18, 91 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory pPlayerInventory) {
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(pPlayerInventory, i, 8 + i * 18, 149));
        }
    }
}

