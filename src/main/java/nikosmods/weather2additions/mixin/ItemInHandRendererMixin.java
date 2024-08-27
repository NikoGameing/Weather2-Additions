package nikosmods.weather2additions.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import nikosmods.weather2additions.items.itemfunction.TabletMapRendering;
import nikosmods.weather2additions.items.itemreg.Items;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin (ItemInHandRenderer.class)
class ItemInHandRendererMixin {
    @Shadow @Final private ItemRenderer itemRenderer;

    @Inject (method = "renderArmWithItem", at = @At ("HEAD"), cancellable = true)
    @SuppressWarnings ("UnusedMethod")
    private void onRenderItem(
            AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack,
            float equippedProgress, PoseStack transform, MultiBufferSource buffer, int combinedLight, CallbackInfo ci
    ) throws IOException {
        if (stack.getItem() == Items.TABLET.get()) {
            ci.cancel();
            renderItemFirstPerson(transform, buffer, combinedLight, hand, pitch, 0f, swingProgress, stack);
        }
    }
    private void renderLight(PoseStack transform, ItemStack stack) {
        int r = 0;
        int g = 0;
        if (stack.getTag() != null && stack.getTag().getInt("CurrentEnergy") > 0) {
            g = 255;
        }
        else {
            r = 255;
        }
        transform.pushPose();
        transform.translate(1,-1,0.2f);
        transform.scale(0.075f,0.075f,1);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder.vertex(transform.last().pose(), 0,0,0).color(r,g,0,1).endVertex();
        bufferBuilder.vertex(transform.last().pose(), 1,0,0).color(r,g,0,1).endVertex();
        bufferBuilder.vertex(transform.last().pose(), 1,1,0).color(r,g,0,1).endVertex();
        bufferBuilder.vertex(transform.last().pose(), 0,1,0).color(r,g,0,1).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());
        transform.popPose();
    }


    private void renderItem(PoseStack transform, MultiBufferSource render, ItemStack stack, int combinedLight) throws IOException {
        transform.pushPose();
        transform.scale(0.7f, 0.7f, 0.7f);
        transform.translate(0, 0.2, 0);
        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, combinedLight, 0, transform, render, null, 69);
        transform.translate(0, -0.212, -0.0133);
        transform.scale(0.25f, 0.25f, 0.25f);
        renderLight(transform, stack);
        if (stack.getTag() != null && stack.getTag().getInt("CurrentEnergy") > 0) {
            TabletMapRendering.renderAll(transform, Minecraft.getInstance().player, stack);
        }
        transform.popPose();
    }

    public void renderItemFirstPerson(PoseStack transform, MultiBufferSource render, int lightTexture, InteractionHand hand, float pitch, float equipProgress, float swingProgress, ItemStack stack) throws IOException {
        Player player = Minecraft.getInstance().player;
        transform.pushPose();
        if (hand == InteractionHand.MAIN_HAND && player.getOffhandItem().isEmpty()) {
            renderItemFirstPersonCenter(transform, render, lightTexture, pitch, equipProgress, swingProgress, stack);
        } else {
            renderItemFirstPersonSide(
                    transform, render, lightTexture,
                    hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite(),
                    equipProgress, swingProgress, stack
            );
        }
        transform.popPose();
    }

    private void renderItemFirstPersonSide(PoseStack transform, MultiBufferSource render, int combinedLight, HumanoidArm side, float equipProgress, float swingProgress, ItemStack stack) throws IOException {
        var minecraft = Minecraft.getInstance();
        var offset = side == HumanoidArm.RIGHT ? 1f : -1f;
        transform.translate(offset * 0.125f, -0.125f, 0f);

        // If the player is not invisible then render a single arm
        if (!minecraft.player.isInvisible()) {
            transform.pushPose();
            transform.mulPose(Axis.ZP.rotationDegrees(offset * 10f));
            minecraft.getEntityRenderDispatcher().getItemInHandRenderer().renderPlayerArm(transform, render, combinedLight, equipProgress, swingProgress, side);
            transform.popPose();
        }

        // Setup the appropriate transformations. This is just copied from the
        // corresponding method in ItemRenderer.
        transform.pushPose();
        transform.translate(offset * 0.51f, -0.08f + equipProgress * -1.2f, -0.75f);
        var f1 = Mth.sqrt(swingProgress);
        var f2 = Mth.sin(f1 * (float) Math.PI);
        var f3 = -0.5f * f2;
        var f4 = 0.4f * Mth.sin(f1 * ((float) Math.PI * 2f));
        var f5 = -0.3f * Mth.sin(swingProgress * (float) Math.PI);
        transform.translate(offset * f3, f4 - 0.3f * f2, f5);
        transform.mulPose(Axis.XP.rotationDegrees(f2 * -45f));
        transform.mulPose(Axis.YP.rotationDegrees(offset * f2 * -30f));
        transform.scale(1.2F, 1.2F, 1.2F);
        renderItem(transform, render, stack, combinedLight);

        transform.popPose();
    }

    private void renderItemFirstPersonCenter(PoseStack transform, MultiBufferSource render, int combinedLight, float pitch, float equipProgress, float swingProgress, ItemStack stack) throws IOException {
        var minecraft = Minecraft.getInstance();
        var renderer = minecraft.getEntityRenderDispatcher().getItemInHandRenderer();

        // Setup the appropriate transformations. This is just copied from the
        // corresponding method in ItemRenderer.
        var swingRt = Mth.sqrt(swingProgress);
        var tX = -0.2f * Mth.sin(swingProgress * (float) Math.PI);
        var tZ = -0.4f * Mth.sin(swingRt * (float) Math.PI);
        transform.translate(0, -tX / 2, tZ);

        var pitchAngle = renderer.calculateMapTilt(pitch);
        transform.translate(0, 0.04F + equipProgress * -1.2f + pitchAngle * -0.5f, -0.72f);
        transform.mulPose(Axis.XP.rotationDegrees(pitchAngle * -85.0f));
        if (!minecraft.player.isInvisible()) {
            transform.pushPose();
            transform.mulPose(Axis.YP.rotationDegrees(90.0F));
            transform.translate(-0.1f,0.24f,0.22f);
            renderer.renderMapHand(transform, render, combinedLight, HumanoidArm.RIGHT);
            transform.translate(0f, 0, -0.44f);
            renderer.renderMapHand(transform, render, combinedLight, HumanoidArm.LEFT);
            transform.popPose();
        }

        var rX = Mth.sin(swingRt * (float) Math.PI);
        transform.mulPose(Axis.XP.rotationDegrees(rX * 20.0F));
        transform.scale(2.0F, 2.0F, 2.0F);

        renderItem(transform, render, stack, combinedLight);}}

/* I may be in hell */