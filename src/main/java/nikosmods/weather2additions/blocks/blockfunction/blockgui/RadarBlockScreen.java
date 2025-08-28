package nikosmods.weather2additions.blocks.blockfunction.blockgui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import nikosmods.weather2additions.mapdata.Maps;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Optional;

import static nikosmods.weather2additions.blocks.blockfunction.blockgui.MenuGenericUtil.renderEnergyHover;
import static nikosmods.weather2additions.blocks.blockfunction.blockgui.MenuGenericUtil.formatEnergy;


public class RadarBlockScreen extends AbstractContainerScreen<RadarBlockMenu> {

    private static final ResourceLocation texture = new ResourceLocation("weather2_additions", "textures/menu/radarmenu.png");
    private static final ResourceLocation inventoryTexture = new ResourceLocation("weather2_additions", "textures/menu/inventory.png");

    private static final int mapRadius = Maps.radarMapRadius;

    private boolean isPressed;

    ImageButton inventoryButton;
    ImageButton inventoryButtonDepressed;

    public RadarBlockScreen(RadarBlockMenu radarBlockMenu, Inventory inventory, Component component) {
        super(radarBlockMenu, inventory, component);
        imageWidth = 176;
        imageHeight = 172;
        inventoryLabelX = 10000;
        titleLabelX = 6;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float v, int i, int i1) {
        int maxEnergy = menu.getDataSlotMaximumEnergy();
        int currentEnergy = menu.getDataSlotCurrentEnergy();
        if (maxEnergy == 0) {
            maxEnergy = 404;
        }
        guiGraphics.blit(texture, leftPos, topPos, 0, 16, imageWidth, imageHeight);
        guiGraphics.blit(texture, leftPos + 7, topPos + 124 - 16, 0, 0, (currentEnergy * 160) / maxEnergy, 16);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderMap(guiGraphics.pose());
        if (inventoryButtonDepressed.visible) {
            guiGraphics.blit(inventoryTexture, leftPos - 8 + menu.inventoryOffsetX, topPos - 16 + menu.inventoryOffsetY, 0, 0, 176, 98);
        }
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int p_282681_, int p_283686_) {
        String throughput = formatEnergy(menu.getDataSlotThroughput(), false);
        int radius = menu.getDataSlotRadius();
        super.renderLabels(guiGraphics, p_282681_, p_283686_);
        guiGraphics.drawString(font, "Consumption: " + throughput + "/t", 26 + 2, 144 - 12, 0x00FF00);
        guiGraphics.drawString(font, "Radius: " + radius + " chunks", 7 + 2, 164 - 12, 0x00FF00);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int posX, int posY) {
        super.renderTooltip(guiGraphics, posX, posY);
        renderEnergyHover(guiGraphics, posX, posY, leftPos, topPos, menu.getDataSlotCurrentEnergy(), menu.getDataSlotMaximumEnergy(), 7, 168, 107, 124, font, Optional.of(menu.getDataSlotChangeEnergy()));
    }

    @Override
    protected void init() {
        super.init();
        inventoryButton = new ImageButton(6 + leftPos, 97 + topPos, 18, 8, 0, 189, texture, buttonPress -> menu.inventorySlots.forEach(slots -> {
            slots.setVisible(!slots.isActive());
            inventoryButton.visible = false;
            inventoryButtonDepressed.visible = true;
            isPressed = true;
        }));
        inventoryButtonDepressed = new ImageButton(6 + leftPos, 97 + topPos, 18, 8, 0, 197, texture, buttonPress -> menu.inventorySlots.forEach(slots -> {
            slots.setVisible(!slots.isActive());
            inventoryButton.visible = true;
            inventoryButtonDepressed.visible = false;
            isPressed = false;
        }));
        addRenderableWidget(inventoryButtonDepressed);
        addRenderableWidget(inventoryButton);
        inventoryButtonDepressed.visible = isPressed;
        inventoryButton.visible = !isPressed;
    }

    public void renderMap(PoseStack transform) {
        final int [] map = Maps.radarMap;
        if (map != null) {
            int xOffset = 88;
            int yOffset = 54;
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.enableDepthTest();
            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            Matrix4f matrix4f = transform.last().pose();
            int i = 0;
            for (int x = -mapRadius; x < mapRadius; x++) {
                for (int z = -mapRadius; z < mapRadius; z++) {
                    int colour = map[i++] | 0xFF000000;
                    int fx = x + leftPos + xOffset;
                    int fx1 = x + 1 + leftPos + xOffset;
                    int fy0 = -z + topPos + yOffset;
                    int fy1 = -z + 1 + topPos + yOffset;
                    bufferBuilder.vertex(matrix4f, fx, fy1, 0).color(colour).endVertex();
                    bufferBuilder.vertex(matrix4f, fx1, fy1, 0).color(colour).endVertex();
                    bufferBuilder.vertex(matrix4f, fx1, fy0, 0).color(colour).endVertex();
                    bufferBuilder.vertex(matrix4f, fx, fy0, 0).color(colour).endVertex();
                }
            }
            BufferUploader.drawWithShader(bufferBuilder.end());
        }
    }
}
