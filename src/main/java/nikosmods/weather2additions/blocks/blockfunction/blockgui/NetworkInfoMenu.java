package nikosmods.weather2additions.blocks.blockfunction.blockgui;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import nikosmods.weather2additions.blocks.blockfunction.CableGenericEntity;
import nikosmods.weather2additions.blocks.blockreg.Blocks;

public class NetworkInfoMenu extends AbstractContainerMenu {
    int capacity;
    int maxCapacity;
    int throughput;
    int cableNumber;
    CableGenericEntity cableGeneric;

    public NetworkInfoMenu(int containerID, Inventory inventory, CableGenericEntity cableGeneric, boolean server) {
        super(MenuTypes.NETWORK_INFO_MENU.get(), containerID);
        this.cableGeneric = cableGeneric;
        capacity = cableGeneric.getEnergyNetwork().energyStorage.getEnergyStored();
        maxCapacity = cableGeneric.getEnergyNetwork().energyStorage.getMaxEnergyStored();
        throughput = cableGeneric.getEnergyNetwork().getEnergyStorage().getThroughputIn();
        cableNumber = cableGeneric.getEnergyNetwork().getCableEntities().size();
    }

    public NetworkInfoMenu(int containerID, Inventory inventory, FriendlyByteBuf byteBuffer) {
        this(containerID, inventory, (CableGenericEntity) inventory.player.level().getBlockEntity(byteBuffer.readBlockPos()), false);
    }

    public int getCapacity() {
        return capacity;
    }
    public int getMaxCapacity() {
        return maxCapacity;
    }
    public int getThroughput() {
        return throughput;
    }
    public int getCableNumber() {
        return cableNumber;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(cableGeneric.getLevel(), cableGeneric.getBlockPos()), player, Blocks.SMALL_BATTERY_BLOCK.get());
    }
}
