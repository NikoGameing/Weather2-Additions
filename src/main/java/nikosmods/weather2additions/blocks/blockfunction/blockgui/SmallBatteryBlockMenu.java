package nikosmods.weather2additions.blocks.blockfunction.blockgui;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import nikosmods.weather2additions.blocks.blockfunction.SmallBatteryBlockEntity;
import nikosmods.weather2additions.blocks.blockreg.Blocks;
import nikosmods.weather2additions.network.packets.energy.EnergyPacket;
import nikosmods.weather2additions.network.Messages;

import java.util.Objects;


public class SmallBatteryBlockMenu extends AbstractContainerMenu {

    private final SmallBatteryBlockEntity smallBatteryBlock;
    private static int chargingThroughput;
    private static int dischargingThroughput;
    private static int chargingEnergy;
    private static int dischargingEnergy;
    private static int maxEnergy;
    private static int currentEnergy;
    private static int changeEnergy;

    public SmallBatteryBlockMenu(int containerID, Inventory inventory, SmallBatteryBlockEntity smallBatteryBlock, boolean server) {
        super(MenuTypes.BATTERY_BLOCK_MENU.get(), containerID);
        this.smallBatteryBlock = smallBatteryBlock;
        addPlayerHotbar(inventory);
        addPlayerInventory(inventory);
        addSlot(new SlotItemHandler(smallBatteryBlock.getStackHandler(), 0, 8,40));
        addSlot(new SlotItemHandler(smallBatteryBlock.getStackHandler(), 1, 152,60));
    }

    public int getDataSlotCharging() {
        return chargingThroughput;
    }
    public int getDataSlotDischarging() {
        return dischargingThroughput;
    }
    public int getDataSlotChargingEnergy() {
        return chargingEnergy;
    }
    public int getDataSlotDischargingEnergy() {
        return dischargingEnergy;
    }
    public int getDataSlotMaximumEnergy() {
        return maxEnergy;
    }
    public int getDataSlotCurrentEnergy() {
        return currentEnergy;
    }
    public int getDataSlotChangeEnergy() {
        return changeEnergy;
    }

    public static void setAll(int chargingThroughput, int chargingEnergy, int dischargingThroughput, int dischargingEnergy, int currentEnergy, int maxEnergy, int changeEnergy) {
        SmallBatteryBlockMenu.chargingThroughput = chargingThroughput;
        SmallBatteryBlockMenu.chargingEnergy = chargingEnergy;
        SmallBatteryBlockMenu.dischargingThroughput = dischargingThroughput;
        SmallBatteryBlockMenu.dischargingEnergy = dischargingEnergy;
        SmallBatteryBlockMenu.currentEnergy = currentEnergy;
        SmallBatteryBlockMenu.maxEnergy = maxEnergy;
        SmallBatteryBlockMenu.changeEnergy = changeEnergy;
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
        int chargingThroughput = smallBatteryBlock.getItemDifferenceCharging();
        int chargingEnergy = smallBatteryBlock.getItemChargingEnergy();
        int dischargingThroughput = smallBatteryBlock.getItemDifferenceDischarging();
        int dischargingEnergy = smallBatteryBlock.getItemDischargingEnergy();
        int currentEnergy = smallBatteryBlock.getCurrentEnergy();
        int maxEnergy = smallBatteryBlock.getMaxEnergy();
        int energyDifference = smallBatteryBlock.getEnergyDifference();
        Messages.sendToClient(new EnergyPacket(chargingThroughput, chargingEnergy, dischargingThroughput, dischargingEnergy, currentEnergy, maxEnergy, energyDifference), (ServerPlayer) player);
        return stillValid(ContainerLevelAccess.create(Objects.requireNonNull(smallBatteryBlock.getLevel()), smallBatteryBlock.getBlockPos()), player, Blocks.SMALL_BATTERY_BLOCK.get());
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

