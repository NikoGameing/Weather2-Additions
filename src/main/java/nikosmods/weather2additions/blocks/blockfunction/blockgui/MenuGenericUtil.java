package nikosmods.weather2additions.blocks.blockfunction.blockgui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.checkerframework.checker.optional.qual.OptionalBottom;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

public class MenuGenericUtil {

    public static int rgbToHex(int r, int g, int b) {
        return r << 16 | g << 8 | b;
    }

    public static String formatEnergy(int energy, Boolean addSymbol) {
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

    public static void renderEnergyHover(GuiGraphics guiGraphics, int posX, int posY, int leftPos, int topPos, int capacity, int maxCapacity, int x1, int x2, int y1, int y2, Font font, Optional<Integer> energyChange) { // x1-y2 create a bounding box around the energy bar, x1 < x2 and y1 < y2
        int r, g;
        int b = 0;
        int changeColor;
        int energyChangeInt;
        float energyDecimal = (float) capacity / maxCapacity;
        if (energyDecimal < 0.5) {
            r = 255;
            g = (int) (255 * energyDecimal * 2);
        }
        else {
            r = (int) (255 * (1 - energyDecimal) * 2);
            g = 255;
        }

        if (leftPos + x1 < posX && posX < leftPos + x2 && topPos + y1 < posY && posY < topPos + y2) {
            if (energyChange.isEmpty()) {
                guiGraphics.renderComponentTooltip(font,
                        List.of(
                                Component.translatable(formatEnergy(capacity, false)).withStyle(Style.EMPTY.withColor(rgbToHex(r, g, b))).append(
                                        Component.translatable("/").withStyle(Style.EMPTY.withColor(0xFFFFFF))).append(
                                        Component.translatable(formatEnergy(maxCapacity, false)).withStyle(Style.EMPTY.withColor(0x00FF00)))
                        ),
                        posX,
                        posY);
            }
            else {
                energyChangeInt = energyChange.get();
                if (energyChangeInt > 0) {
                    changeColor = 0x00FF00;
                }
                else if (energyChangeInt == 0) {
                    changeColor = 0xFFFFFF;
                }
                else {
                    changeColor = 0xFF0000;
                }
                guiGraphics.renderComponentTooltip(font,
                        List.of(
                                Component.translatable(formatEnergy(capacity, false)).withStyle(Style.EMPTY.withColor(rgbToHex(r,g,b))).append(
                                        Component.translatable("/").withStyle(Style.EMPTY.withColor(0xFFFFFF))).append(
                                        Component.translatable(formatEnergy(maxCapacity, false)).withStyle(Style.EMPTY.withColor(0x00FF00))),
                                Component.translatable(formatEnergy(energyChangeInt, true)+"/t").withStyle(Style.EMPTY.withColor(changeColor))
                        ),
                        posX,
                        posY);
            }
        }
    }
}
