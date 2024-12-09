package nikosmods.weather2additions.blocks.blockfunction.blockgui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class NetworkInfoScreen extends AbstractContainerScreen<NetworkInfoMenu> {

    private static final ResourceLocation texture = new ResourceLocation("weather2_additions", "textures/menu/networkinfoscreen.png");

    public NetworkInfoScreen(NetworkInfoMenu containerMenu, Inventory inventory, Component component) {
        super(containerMenu, inventory, component);
        imageWidth = 176;
        imageHeight = 100;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        int maxEnergy = menu.getMaxCapacity();
        if (maxEnergy == 0) {
            maxEnergy = 404;
        }
        guiGraphics.blit(texture, leftPos, topPos, 0, 16, imageWidth, imageHeight);
        guiGraphics.blit(texture, leftPos + 8, topPos + 20, 0, 0, (menu.getCapacity() * 160) / maxEnergy, 16);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int posX, int posY) {
        super.renderTooltip(guiGraphics, posX, posY);
        int r, g;
        int b = 0;
        float energyDecimal = (float) menu.getCapacity() / menu.getMaxCapacity();
        if (energyDecimal < 0.5) {
            r = 255;
            g = (int) (255 * energyDecimal * 2);
        }
        else {
            r = (int) (255 * (1 - energyDecimal) * 2);
            g = 255;
        }

        if (leftPos + 8 < posX && posX < leftPos + 168 && topPos + 20 < posY && posY < topPos + 36) {
            guiGraphics.renderComponentTooltip(font,
                    List.of(
                            Component.translatable(formatEnergy(menu.getCapacity(), false)).withStyle(Style.EMPTY.withColor(rgbToHex(r,g,b))).append(
                                    Component.translatable("/").withStyle(Style.EMPTY.withColor(0xFFFFFF))).append(
                                    Component.translatable(formatEnergy(menu.getMaxCapacity(), false)).withStyle(Style.EMPTY.withColor(0x00FF00)))
                            ),
                    posX,
                    posY);
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
