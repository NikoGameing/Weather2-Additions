package nikosmods.weather2additions.blocks;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum Connections implements StringRepresentable {
    NONE("none"),
    SIMILAR("similar"),
    CONNECTED("connected");

    private final String serializedName;

    Connections(String serializedName) {
        this.serializedName = serializedName;
    }

    public @NotNull String getSerializedName() {
        return serializedName;
    }
}
