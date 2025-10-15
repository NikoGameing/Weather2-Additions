package nikosmods.weather2additions.blocks.blockfunction.blockrenderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nikosmods.weather2additions.Weather2Additions;
import nikosmods.weather2additions.blocks.ScreenBlock;
import nikosmods.weather2additions.blocks.blockentityreg.BlockEntityTypes;
import nikosmods.weather2additions.blocks.blockfunction.ScreenBlockEntity;
import nikosmods.weather2additions.mapdata.BlockMapData;
import nikosmods.weather2additions.mapdata.BlockMapDataList;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

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
    private static final Logger LOGGER = LogUtils.getLogger();

    public ScreenRenderer(BlockEntityRendererProvider.Context context) {
        super();
    }

    public static int genTextureID(int width, int height) {
        int textureID = TextureUtil.generateTextureId();
        TextureUtil.prepareImage(textureID, width, height);
        return textureID;
    }

    public static void renderMapImage(PoseStack transform, ScreenBlockEntity blockEntity) {
        BlockMapData mapData = BlockMapDataList.getData(blockEntity.getBlockPos());
        if (mapData == null)
            return;
        byte[] map = mapData.image;
        if (map != null && previousMap != map) {
            ByteArrayInputStream input = new ByteArrayInputStream(map);
            int width = mapData.imageWidth;
            int height = mapData.imageHeight;

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

            if (width == 0 || height == 0 || map.length == 0) {
                MemoryUtil.memFree(imageBuffer);
                LOGGER.warn("Skipping map rendering due to invalid map data");
            }

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

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, textureID);
        RenderSystem.enableDepthTest();
        // RenderSystem.setShaderTexture(0, new ResourceLocation("minecraft", "textures/block/dirt.png"));
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix4f = transform.last().pose();

        float fx0 = (1f);
        float fy0 = (1f );
        float fz0 = (0.98f);
        float fx1 = (0f );
        float fy1 = (0f );
        float fz1 = (0.98f);
        
        int uv00 = 0;
        int uv01 = 1;

        int uv10 = 0;
        int uv11 = 0;

        int uv20 = 1;
        int uv21 = 0;

        int uv30 = 1;
        int uv31 = 1;

        if (blockEntity.getBlockState().getValue(ScreenBlock.FACING) == Direction.EAST) {
            fx0 = (1-0.951f);
            fy0 = (1f );
            fz0 = (1f );
            fx1 = (1-0.951f);
            fy1 = (0f );
            fz1 = (0f );

            uv00 = 1;
            uv11 = 1;
            uv20 = 0;
            uv31 = 0;

        } else if (blockEntity.getBlockState().getValue(ScreenBlock.FACING) == Direction.SOUTH) {
            fx0 = (1f );
            fy0 = (0f );
            fz0 = (1-0.951f);
            fx1 = (0f );
            fy1 = (1f );
            fz1 = (1-0.951f);
        } else if (blockEntity.getBlockState().getValue(ScreenBlock.FACING) == Direction.WEST) {
            fx0 = (0.951f);
            fy0 = (1f );
            fz0 = (0f );
            fx1 = (0.951f);
            fy1 = (0f );
            fz1 = (1f );
            uv01 = 0;
            uv10 = 1;
            uv21 = 1;
            uv30 = 0;
        }

        bufferBuilder.vertex(matrix4f, fx0, fy0, fz0).uv(uv00, uv01).endVertex();
        bufferBuilder.vertex(matrix4f, fx1, fy0, fz1).uv(uv10, uv11).endVertex();
        bufferBuilder.vertex(matrix4f, fx1, fy1, fz1).uv(uv20, uv21).endVertex();
        bufferBuilder.vertex(matrix4f, fx0, fy1, fz0).uv(uv30, uv31).endVertex();

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
