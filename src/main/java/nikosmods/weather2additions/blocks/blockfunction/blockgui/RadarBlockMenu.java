package nikosmods.weather2additions.blocks.blockfunction.blockgui;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import nikosmods.weather2additions.blocks.blockfunction.RadarBlockEntity;
import nikosmods.weather2additions.blocks.blockreg.Blocks;
import nikosmods.weather2additions.data.Maps;
import nikosmods.weather2additions.items.itemfunction.Column;
import nikosmods.weather2additions.network.Messages;
import nikosmods.weather2additions.network.MapPacket;
import nikosmods.weather2additions.Config;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RadarBlockMenu extends AbstractContainerMenu {

    public final int inventoryOffsetY = 32;
    public final int inventoryOffsetX = 35;
    public int mapResolution = Config.RESOLUTION.get();
    public int mapRadius = Maps.radarMapRadius;

    private final Map<Column, Integer> otherMap = Maps.otherMap;

    public final RadarBlockEntity radarBlock;

    public final List<HideableSlots> inventorySlots = new ArrayList<>();

    private final DataSlot dataSlotCurrentEnergy;
    private final DataSlot dataSlotMaxEnergy;
    private final DataSlot dataSlotChangeEnergy;
    private final DataSlot dataSlotRadius;
    private final DataSlot dataSlotThroughput;

    public RadarBlockMenu(int containerID, Inventory inventory, RadarBlockEntity radarBlock, boolean server) {
        super(MenuTypes.RADAR_BLOCK_MENU.get(), containerID);
        this.radarBlock = radarBlock;
        addSlot(new SlotItemHandler(radarBlock.getStackHandler(), 0, 7, 128));
        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);
        dataSlotCurrentEnergy = server?radarBlock.getCurrentStoredEnergyData():DataSlot.standalone();
        dataSlotMaxEnergy = server?radarBlock.getMaxEnergyStoredData():DataSlot.standalone();
        dataSlotChangeEnergy = server?radarBlock.getChangeEnergyData():DataSlot.standalone();
        dataSlotRadius = server?radarBlock.getRadiusData():DataSlot.standalone();
        dataSlotThroughput = server?radarBlock.getThroughputData():DataSlot.standalone();
        addDataSlot(dataSlotChangeEnergy);
        addDataSlot(dataSlotCurrentEnergy);
        addDataSlot(dataSlotRadius);
        addDataSlot(dataSlotThroughput);
        addDataSlot(dataSlotMaxEnergy);
    }

    public RadarBlockMenu(int containerID, Inventory inventory, FriendlyByteBuf byteBuffer) {
        this(containerID, inventory, (RadarBlockEntity) inventory.player.level().getBlockEntity(byteBuffer.readBlockPos()), false);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        mapResolution = Config.RESOLUTION.get();
        mapRadius = Maps.radarMapRadius;
        ServerPlayer serverPlayer = (ServerPlayer) player;
        int [] map = new int[98*98];
        int i = 0;
        for (int x = -mapRadius; x < mapRadius; x++) {
            for (int y = -mapRadius; y < mapRadius; y++) {
                int worldX = radarBlock.getBlockPos().getX() / mapResolution * mapResolution + x * mapResolution;
                int worldY = radarBlock.getBlockPos().getZ() / mapResolution * mapResolution + -y * mapResolution;
                Column column = new Column(worldX, worldY, serverPlayer.level());
                map[i++] = otherMap.getOrDefault(column, 0);
            }
        }
        MapPacket mapPacket = new MapPacket(map, mapResolution, 0, 0, 0, "radar");
        Messages.sendToClient(mapPacket, serverPlayer);
        return stillValid(ContainerLevelAccess.create(Objects.requireNonNull(radarBlock.getLevel()), radarBlock.getBlockPos()), player, Blocks.RADAR_BLOCK.get());
    }

    public int getDataSlotMaximumEnergy() {
        return dataSlotMaxEnergy.get();
    }

    public int getDataSlotCurrentEnergy() {
        return dataSlotCurrentEnergy.get();
    }

    public int getDataSlotChangeEnergy() {
        return dataSlotChangeEnergy.get();
    }

    public int getDataSlotRadius() {
        return dataSlotRadius.get();
    }

    public int getDataSlotThroughput() {
        return dataSlotThroughput.get();
    }

    private void addPlayerInventory(Inventory pPlayerInventory) {
        for (int i = 0; i < 3; i++) {
            for (int l = 0; l < 9; l++) {
                HideableSlots slot = new HideableSlots(pPlayerInventory, l + i * 9 + 9, inventoryOffsetX + l * 18, inventoryOffsetY + i * 18);
                addSlot(slot);
                inventorySlots.add(slot);
            }
        }
    }

    private void addPlayerHotbar(Inventory pPlayerInventory) {
        for (int i = 0; i < 9; i++) {
            HideableSlots slot = new HideableSlots(pPlayerInventory, i, inventoryOffsetX + i * 18, 58 + inventoryOffsetY);
            addSlot(slot);
            inventorySlots.add(slot);
        }
    }

    
}
