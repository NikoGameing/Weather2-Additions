package nikosmods.weather2additions.network.packets.energy;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import nikosmods.weather2additions.blocks.blockfunction.blockgui.SmallBatteryBlockMenu;
import nikosmods.weather2additions.network.Packet;

import java.util.function.Supplier;

public class EnergyPacket implements Packet {

    private final int itemChargingThroughput;
    private final int itemChargingEnergy;
    private final int itemDischargingThroughput;
    private final int itemDischargingEnergy;
    private final int currentEnergy;
    private final int maxEnergy;
    private final int energyDifference;

    public EnergyPacket(int itemChargingThroughput, int itemChargingEnergy, int itemDischargingThroughput, int itemDischargingEnergy, int currentEnergy, int maxEnergy, int energyDifference) {
        this.itemChargingThroughput = itemChargingThroughput;
        this.itemChargingEnergy = itemChargingEnergy;
        this.itemDischargingThroughput = itemDischargingThroughput;
        this.itemDischargingEnergy = itemDischargingEnergy;
        this.currentEnergy = currentEnergy;
        this.maxEnergy = maxEnergy;
        this.energyDifference = energyDifference;
    }

    public EnergyPacket(FriendlyByteBuf byteBuf) {
        itemChargingThroughput = byteBuf.readInt();
        itemChargingEnergy = byteBuf.readInt();
        itemDischargingThroughput = byteBuf.readInt();
        itemDischargingEnergy = byteBuf.readInt();
        currentEnergy = byteBuf.readInt();
        maxEnergy = byteBuf.readInt();
        energyDifference = byteBuf.readInt();
    }

    public void encode(FriendlyByteBuf byteBuffer) {
        byteBuffer.writeInt(itemChargingThroughput);
        byteBuffer.writeInt(itemChargingEnergy);
        byteBuffer.writeInt(itemDischargingThroughput);
        byteBuffer.writeInt(itemDischargingEnergy);
        byteBuffer.writeInt(currentEnergy);
        byteBuffer.writeInt(maxEnergy);
        byteBuffer.writeInt(energyDifference);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(this::work);
    }

    public void work() {
        SmallBatteryBlockMenu.setAll(itemChargingThroughput, itemChargingEnergy, itemDischargingThroughput, itemDischargingEnergy, currentEnergy, maxEnergy, energyDifference);
    }
}
