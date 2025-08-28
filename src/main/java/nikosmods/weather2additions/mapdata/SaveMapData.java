package nikosmods.weather2additions.mapdata;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;
import nikosmods.weather2additions.items.itemfunction.Column;
import org.jetbrains.annotations.NotNull;


public class SaveMapData extends SavedData {
    public SaveMapData() {}

    public SaveMapData(CompoundTag compoundTag) {
        for (String key:compoundTag.getAllKeys()) {
            ServerMapRendering.otherMap.put(Column.uncereal(key),compoundTag.getInt(key));
        }
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag) {
        for (Column column : ServerMapRendering.otherMap.keySet()) {
            compoundTag.putInt(column.cereal(), ServerMapRendering.otherMap.get(column));
        }
        return compoundTag;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    public static void get() {
        ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage().computeIfAbsent(SaveMapData::new, SaveMapData::new, "w2a_mapdata");
    }
}
