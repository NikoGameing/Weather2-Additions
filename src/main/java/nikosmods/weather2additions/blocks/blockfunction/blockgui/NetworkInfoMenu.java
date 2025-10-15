package nikosmods.weather2additions.blocks.blockfunction.blockgui;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import nikosmods.weather2additions.blocks.blockfunction.CableGenericEntity;
import nikosmods.weather2additions.blocks.blockreg.Blocks;
import nikosmods.weather2additions.network.packets.energy.AnalyserPacket;
import nikosmods.weather2additions.network.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NetworkInfoMenu extends AbstractContainerMenu {
    static int capacity;
    static int maxCapacity;
    static int throughput;
    static int cableNumber;
    CableGenericEntity cableGeneric;

    public NetworkInfoMenu(int containerID, Inventory inventory, CableGenericEntity cableGeneric, boolean server) {
        super(MenuTypes.NETWORK_INFO_MENU.get(), containerID);
        this.cableGeneric = cableGeneric;
    }

    public NetworkInfoMenu(int containerID, Inventory inventory, FriendlyByteBuf byteBuffer) {
        this(containerID, inventory, (CableGenericEntity) inventory.player.level().getBlockEntity(byteBuffer.readBlockPos()), false);
    }

    public static void setAll(int capacity, int maxCapacity, int throughput, int cableNumber) {
        NetworkInfoMenu.capacity = capacity;
        NetworkInfoMenu.maxCapacity = maxCapacity;
        NetworkInfoMenu.throughput = throughput;
        NetworkInfoMenu.cableNumber = cableNumber;
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
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        capacity = cableGeneric.getEnergyNetwork().getEnergyStorage().getEnergyStored();
        maxCapacity = cableGeneric.getEnergyNetwork().getEnergyStorage().getMaxEnergyStored();
        throughput = cableGeneric.getEnergyNetwork().getEnergyStorage().getThroughputIn();
        cableNumber = cableGeneric.getEnergyNetwork().getCableEntities().size();
        Messages.sendToClient(new AnalyserPacket(capacity, maxCapacity, throughput, cableNumber), (ServerPlayer) player);
        return stillValid(ContainerLevelAccess.create(Objects.requireNonNull(cableGeneric.getLevel()), cableGeneric.getBlockPos()), player, Blocks.CABLE_SMALL.get());
    }
}
