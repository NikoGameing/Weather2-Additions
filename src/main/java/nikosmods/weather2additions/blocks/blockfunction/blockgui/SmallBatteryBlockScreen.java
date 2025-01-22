package nikosmods.weather2additions.blocks.blockfunction.blockgui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

import static nikosmods.weather2additions.blocks.blockfunction.blockgui.MenuGenericUtil.*;


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
        renderEnergyHover(guiGraphics, posX, posY, leftPos, topPos, menu.getDataSlotCurrentEnergy().get(), menu.getDataSlotMaximumEnergy().get(), 8, 168, 19, 36, font, Optional.of(menu.getDataSlotChangeEnergy().get()));
    }
}
