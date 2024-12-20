package nikosmods.weather2additions.blocks.blockfunction.blockrenderer;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nikosmods.weather2additions.Weather2Additions;
import nikosmods.weather2additions.blocks.blockentityreg.BlockEntityTypes;
import nikosmods.weather2additions.blocks.blockfunction.RadarBlockEntity;
import nikosmods.weather2additions.blocks.blockfunction.blockrenderer.models.*;



@Mod.EventBusSubscriber(modid = Weather2Additions.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RadarRenderer implements BlockEntityRenderer<RadarBlockEntity> {

    protected final RadarBlockTop dishModel;

    private static final ResourceLocation dishTexture = new ResourceLocation("weather2_additions", "textures/block/radar_block/top/finaldishtexture.png");

    public RadarRenderer(BlockEntityRendererProvider.Context context) {
        super();
        this.dishModel = new RadarBlockTop<>(Minecraft.getInstance().getEntityModels().bakeLayer(RadarBlockTop.LAYER_LOCATION));
    }

    @Override
    public void render(RadarBlockEntity radarBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        double spin = radarBlockEntity.getSpin();
        double futureSpin = radarBlockEntity.getLastSpin();

        poseStack.pushPose();
        poseStack.translate(0.5f, 1.5f, 0.5f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, (float) futureSpin, (float) spin)));
        VertexConsumer bufferBuilder = buffer.getBuffer(dishModel.renderType(dishTexture)); // .entityCutout() adds shadows to the model btw
        dishModel.renderToBuffer(poseStack, bufferBuilder, packedLight, packedOverlay, 1f, 1f, 1f, 1f);
        poseStack.popPose();

    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers renderersEvent) {
        renderersEvent.registerBlockEntityRenderer(BlockEntityTypes.RADAR_BLOCK_ENTITY.get(), RadarRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(RadarBlockTop.LAYER_LOCATION, RadarBlockTop::createBodyLayer);
    }

}
