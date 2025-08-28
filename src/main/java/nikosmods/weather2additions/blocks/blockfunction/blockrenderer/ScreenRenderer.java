package nikosmods.weather2additions.blocks.blockfunction.blockrenderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nikosmods.weather2additions.Config;
import nikosmods.weather2additions.Weather2Additions;
import nikosmods.weather2additions.blocks.blockentityreg.BlockEntityTypes;
import nikosmods.weather2additions.blocks.blockfunction.ScreenBlockEntity;
import nikosmods.weather2additions.mapdata.Maps;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;


@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(modid = Weather2Additions.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ScreenRenderer implements BlockEntityRenderer<ScreenBlockEntity> {
    private static int textureID;
    private static byte[] previousMap;

    public ScreenRenderer(BlockEntityRendererProvider.Context context) {
        super();
    }

    public static int genTextureID(int width, int height) {
        int textureID = TextureUtil.generateTextureId();
        TextureUtil.prepareImage(textureID, width, height);
        return textureID;
    }

    public static void renderMapImage(PoseStack transform, ScreenBlockEntity blockEntity) {
        int mapRadius = Config.TABLET_RADIUS.get();
        int mapResolution = Config.RESOLUTION.get();
        byte[] map = Maps.screenImage;
        if (map != null && previousMap != map) {
            ByteArrayInputStream input = new ByteArrayInputStream(map);
            int width = Maps.screenImageWidth;
            int height = Maps.screenImageHeight;
            if (textureID == 0) {
                textureID = genTextureID(width, height);
            }
            int i = 0;
            byte[] RGBInfo = new byte[height * width * 4];
            try {
                BufferedImage mapImage = ImageIO.read(input);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int color = mapImage.getRGB(x, y);
                        RGBInfo[i++] = (byte) (color >> 16);
                        RGBInfo[i++] = (byte) (color >> 8);
                        RGBInfo[i++] = (byte) (color);
                        RGBInfo[i++] = (byte) (color >> 24);
                    }
                }
            } catch (Exception ignored) {
            }
            ByteBuffer imageBuffer = MemoryUtil.memAlloc(RGBInfo.length);
            imageBuffer.put(RGBInfo);

            RenderSystem.bindTexture(textureID);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            if (width <= 0 || height <= 0 || imageBuffer.capacity() == 0) {
                Weather2Additions.LOGGER.warn(ScreenRenderer.class.getName() + ": " + "Skipping rendering due to invalid map data");
                MemoryUtil.memFree(imageBuffer);
                return;
            }

            GlStateManager._texImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageBuffer.flip().asIntBuffer());
            MemoryUtil.memFree(imageBuffer);
        }
        int mapX = Maps.mapX;
        int mapY = Maps.mapY;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, textureID);
        RenderSystem.enableDepthTest();
        // RenderSystem.setShaderTexture(0, new ResourceLocation("minecraft", "textures/block/dirt.png"));
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix4f = transform.last().pose();

        float offsetX = ((float) mapX - (float) blockEntity.getBlockPos().getX()) / mapResolution / mapRadius;
        float offsetY = -((float) mapY - (float) blockEntity.getBlockPos().getZ()) / mapResolution / mapRadius;

        float fx0 = (1f + offsetX);
        float fy0 = (1f + offsetY);
        float fx1 = (0f + offsetX);
        float fy1 = (0f + offsetY);

        bufferBuilder.vertex(matrix4f, fx0, fy0, 0.951f).uv(0, 1).endVertex();
        bufferBuilder.vertex(matrix4f, fx1, fy0, 0.951f).uv(0, 0).endVertex();
        bufferBuilder.vertex(matrix4f, fx1, fy1, 0.951f).uv(1, 0).endVertex();
        bufferBuilder.vertex(matrix4f, fx0, fy1, 0.951f).uv(1, 1).endVertex();

        BufferUploader.drawWithShader(bufferBuilder.end());
        previousMap = map;
    }


    @Override
    public void render(ScreenBlockEntity screenBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        renderMapImage(poseStack, screenBlockEntity);
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers renderersEvent) {
        renderersEvent.registerBlockEntityRenderer(BlockEntityTypes.SCREEN_BLOCK_ENTITY.get(), ScreenRenderer::new);
    }
}
