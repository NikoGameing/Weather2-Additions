package nikosmods.weather2additions.items.itemfunction;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import nikosmods.weather2additions.items.itemproperties.ItemEnergy;
import org.jetbrains.annotations.Nullable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class ItemTablet extends ItemEnergy {
    public static final int maxTimer = 20;
    public static final int mapRadius = 50;
    public static final int resolution = 16;

    @Override
    public void inventoryTick(ItemStack p_41404_, Level p_41405_, Entity p_41406_, int p_41407_, boolean p_41408_) {
        super.inventoryTick(p_41404_, p_41405_, p_41406_, p_41407_, p_41408_);
        if (p_41406_ instanceof ServerPlayer player) {
            if (!p_41405_.isClientSide && p_41408_) {
                int timer = getTag(p_41404_).getInt("Timer");
                if (timer > 0) {
                    timer -= 1;
                    getTag(p_41404_).putInt("Timer", timer);
                }
                else {
                    getTag(p_41404_).putInt("Timer", maxTimer);
                    updateMap(p_41404_, player);
                }
            }
        }
    }

    @Override
    public @Nullable CompoundTag getShareTag(ItemStack stack) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("CurrentEnergy", getTag(stack).getInt("CurrentEnergy"));
        return tag;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        int r, g;
        int b = 0;
        int maxEnergy = getMaxEnergy();
        int currentEnergy = getTag(itemStack).getInt("CurrentEnergy");
        float energyDecimal = (float) currentEnergy / maxEnergy;
        if (energyDecimal < 0.5) {
            r = 255;
            g = (int) (255 * energyDecimal * 2);
        }
        else {
            r = (int) (255 * (1 - energyDecimal) * 2);
            g = 255;
        }
        List<Component> components = List.of(
                Component.translatable(formatEnergy(currentEnergy, false)).withStyle(Style.EMPTY.withColor(rgbToHex(r,g,b))).append(
                Component.translatable("/").withStyle(Style.EMPTY.withColor(0xFFFFFF))).append(
                Component.translatable(formatEnergy(maxEnergy, false)).withStyle(Style.EMPTY.withColor(0x00FF00)))
        );

        componentList.addAll(components);

        super.appendHoverText(itemStack, level, componentList, tooltipFlag);

    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged||oldStack.getItem()!= newStack.getItem();
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem()!=newStack.getItem();
    }

    public ItemTablet(Properties properties, int paramMaxEnergy) {
        super(properties, paramMaxEnergy);
    }

    private void updateMap(ItemStack p_41404_, ServerPlayer player) {
        int energy = getTag(p_41404_).getInt("CurrentEnergy");
        if (energy > 0) {
            energy -= 10;
            if (energy < 0) {
                energy = 0;
            }
            getTag(p_41404_).putInt("CurrentEnergy", energy);
            ServerTabletMapRendering.updatePlayer(player);
        }
    }

    private int rgbToHex(int r, int g, int b) {
        return r << 16 | g << 8 | b;
    }

    private static String formatEnergy(int energy, Boolean addSymbol) {
        String formatted = energy + "RF";
        int energyNormal = energy;
        if (energy < 0) {
            energyNormal = -energy;
        }
        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        decimalFormat.setRoundingMode(RoundingMode.HALF_EVEN);
        if (energyNormal >= 1000 && energyNormal < 100000) {
            formatted = decimalFormat.format((float) energy / 1000) + "KRF";
        }
        else if (energyNormal >= 100000 && energyNormal < 1000000000) {
            formatted = decimalFormat.format((float) energy / 1000000) + "MRF";
        }
        else if (energyNormal > 1000000000) {
            formatted = decimalFormat.format((float) energy / 1000000000) + "GRF";
        }

        if (addSymbol && energy != 0) {
            if (energy > 0) {
                formatted = "+" + formatted;
            }
        }
        return formatted;
    }
}
