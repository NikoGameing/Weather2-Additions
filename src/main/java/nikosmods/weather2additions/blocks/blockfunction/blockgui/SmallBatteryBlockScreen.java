package nikosmods.weather2additions.blocks.blockfunction.blockgui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;


public class SmallBatteryBlockScreen extends AbstractContainerScreen<SmallBatteryBlockMenu>{

    private static final ResourceLocation texture = new ResourceLocation("weather2_additions", "textures/menu/smallbatterymenu.png");

    public SmallBatteryBlockScreen(SmallBatteryBlockMenu containerMenu, Inventory inventory, Component component) {
        super(containerMenu, inventory, component);
        imageWidth = 176;
        imageHeight = 173;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        int maxEnergy = menu.getDataSlotMaximumEnergy().get();
        if (maxEnergy == 0) {
            maxEnergy = 404;
        }
        guiGraphics.blit(texture, leftPos, topPos, 0, 16, imageWidth, imageHeight);
        guiGraphics.blit(texture, leftPos + 8, topPos + 20, 0, 0, (menu.getDataSlotCurrentEnergy().get() * 160) / maxEnergy, 16);
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

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int p_282681_, int p_283686_) {
        String output = formatEnergy(menu.getDataSlotCharging().get(), true);
        String outputEnergy = formatEnergy(menu.getDataSlotChargingEnergy().get(), false);
        String input = formatEnergy(-menu.getDataSlotDischarging().get(), true);
        String inputEnergy = formatEnergy(menu.getDataSlotDischargingEnergy().get(), false);

        super.renderLabels(guiGraphics, p_282681_, p_283686_);
        guiGraphics.drawString(font, "Output: " + output + "/t, " + outputEnergy, 30, 44, 0x00FF00);
        guiGraphics.drawString(font, "Input: " + input + "/t, " + inputEnergy, 145 - font.width("Input: " + input + "/t, " + inputEnergy), 64, 0x00FF00);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int posX, int posY) {
        super.renderTooltip(guiGraphics, posX, posY);
        int r, g;
        int b = 0;
        int changeColor;
        int energyChange;
        float energyDecimal = (float) menu.getDataSlotCurrentEnergy().get() / menu.getDataSlotMaximumEnergy().get();
        if (energyDecimal < 0.5) {
            r = 255;
            g = (int) (255 * energyDecimal * 2);
        }
        else {
            r = (int) (255 * (1 - energyDecimal) * 2);
            g = 255;
        }

        energyChange = menu.getDataSlotChangeEnergy().get();

        if (energyChange > 0) {
            changeColor = 0x00FF00;
        }
        else if (energyChange == 0) {
            changeColor = 0xFFFFFF;
        }
        else {
            changeColor = 0xFF0000;
        }

        if (leftPos + 8 < posX && posX < leftPos + 168 && topPos + 20 < posY && posY < topPos + 36) {
            guiGraphics.renderComponentTooltip(font,
                    List.of(
                            Component.translatable(formatEnergy(menu.getDataSlotCurrentEnergy().get(), false)).withStyle(Style.EMPTY.withColor(rgbToHex(r,g,b))).append(
                            Component.translatable("/").withStyle(Style.EMPTY.withColor(0xFFFFFF))).append(
                            Component.translatable(formatEnergy(menu.getDataSlotMaximumEnergy().get(), false)).withStyle(Style.EMPTY.withColor(0x00FF00))),
                            Component.translatable(formatEnergy(menu.getDataSlotChangeEnergy().get(), true)+"/t").withStyle(Style.EMPTY.withColor(changeColor))
                            ),
                    posX,
                    posY);
        }
    }
    private int rgbToHex(int r, int g, int b) {
        return r << 16 | g << 8 | b;
    }
}
