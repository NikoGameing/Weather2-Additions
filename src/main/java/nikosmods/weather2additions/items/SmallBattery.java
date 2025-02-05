package nikosmods.weather2additions.items;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import nikosmods.weather2additions.items.itemproperties.ItemEnergy;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static nikosmods.weather2additions.blocks.blockfunction.blockgui.MenuGenericUtil.formatEnergy;
import static nikosmods.weather2additions.blocks.blockfunction.blockgui.MenuGenericUtil.rgbToHex;

public class SmallBattery extends ItemEnergy {
    public SmallBattery(Properties properties, int paramMaxEnergy, int throughputIn, int throughputOut) {
        super(properties, paramMaxEnergy, throughputIn, throughputOut);
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

}
