package nikosmods.weather2additions.blocks.blockfunction;

import net.minecraft.world.phys.Vec3;
import nikosmods.weather2additions.blocks.ScreenBlock;

import java.util.ArrayList;

public class CollectiveScreen {

    public final ArrayList<ScreenBlockEntity> screenBlocks = new ArrayList<>();
    public ScreenBlockEntity bottomLeftScreen;
    public ScreenBlockEntity topLeftScreen;
    public ScreenBlockEntity topRightScreen;
    public ScreenBlockEntity bottomRightScreen;

    public CollectiveScreen(ScreenBlockEntity sourceScreenBlock) {
        addScreenBlock(sourceScreenBlock);
    }

    public void addScreenBlock(ScreenBlockEntity screenBlockEntity) {
        if (!screenBlocks.contains(screenBlockEntity)) {
            screenBlocks.add(screenBlockEntity);
            update();
        }
    }

    public void removeScreenBlock(ScreenBlockEntity screenBlockEntity) {
        screenBlocks.remove(screenBlockEntity);
        update();
    }

    private void update() {
        int tBlockPosX = 0;
        int tBlockPosY = 0;
        int tBlockPosZ = 0;
        for (ScreenBlockEntity blockEntity : screenBlocks) {
            tBlockPosX += blockEntity.getBlockPos().getX();
            tBlockPosY += blockEntity.getBlockPos().getY();
            tBlockPosZ += blockEntity.getBlockPos().getZ();
        }
        Vec3 centre = new Vec3((double) tBlockPosX / screenBlocks.size(), (double) tBlockPosY / screenBlocks.size(), (double) tBlockPosZ / screenBlocks.size());
        screenBlocks.forEach(screenBlockEntity -> {
            switch (screenBlockEntity.getBlockState().getValue(ScreenBlock.FACING)) {
                case NORTH -> {
                    screenBlockEntity.offsetX = screenBlockEntity.getBlockPos().getX() - (float) centre.x;
                    screenBlockEntity.offsetY = -(screenBlockEntity.getBlockPos().getY() - (float) centre.y);
                }
                case EAST -> {
                    screenBlockEntity.offsetY = screenBlockEntity.getBlockPos().getZ() - (float) centre.z;
                    screenBlockEntity.offsetX = (screenBlockEntity.getBlockPos().getY() - (float) centre.y);
                }
                case SOUTH -> {
                    screenBlockEntity.offsetX = screenBlockEntity.getBlockPos().getX() - (float) centre.x;
                    screenBlockEntity.offsetY = (screenBlockEntity.getBlockPos().getY() - (float) centre.y);
                }
                case WEST -> {
                    screenBlockEntity.offsetY = (screenBlockEntity.getBlockPos().getZ() - (float) centre.z);
                    screenBlockEntity.offsetX = -(screenBlockEntity.getBlockPos().getY() - (float) centre.y);
                }
            }
        });
    }
}
