package nikosmods.weather2additions.blocks.blockfunction.blockgui;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class HideableSlots extends Slot {

    private boolean visible;

    @Override
    public boolean isActive() {
        return visible;
    }

    public void setVisible(boolean b) {
        visible = b;
    }

    public HideableSlots(Container p_40223_, int p_40224_, int p_40225_, int p_40226_) {
        super(p_40223_, p_40224_, p_40225_, p_40226_);
    }
}
