package nikosmods.weather2additions.blocks.blockfunction;

import java.util.ArrayList;

// todo: this

public class CollectiveScreen {

    private final ArrayList<ScreenBlockEntity> screenBlocks = new ArrayList<>();
    private ScreenBlockEntity bottomLeftScreen;
    private ScreenBlockEntity topLeftScreen;
    private ScreenBlockEntity topRightScreen;
    private ScreenBlockEntity bottomRightScreen;

    public CollectiveScreen(ScreenBlockEntity sourceScreenBlock) {
        screenBlocks.add(sourceScreenBlock);
    }

    public ArrayList<ScreenBlockEntity> getScreenBlocks() {
        return screenBlocks;
    }

    public void addScreenBlock(ScreenBlockEntity screenBlockEntity) {
        if (!screenBlocks.contains(screenBlockEntity)) {
            screenBlocks.add(screenBlockEntity);
        }
    }

    public void removeScreenBlock(ScreenBlockEntity screenBlockEntity) {
        screenBlocks.remove(screenBlockEntity);
    }

    private void update() {
        int bottomLeft;
        int bottomRight;
        int topLeft;
        int topRight;
        screenBlocks.forEach(screenBlockEntity -> {
        });
    }
}
