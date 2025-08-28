package nikosmods.weather2additions.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import nikosmods.weather2additions.blocks.blockfunction.ExtendableScreenBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExtendableScreenBlock extends Block implements EntityBlock {

    public static EnumProperty<SideConnections> ABOVE = EnumProperty.create("above", SideConnections.class);
    public static EnumProperty<SideConnections> BELOW = EnumProperty.create("below", SideConnections.class);
    public static EnumProperty<SideConnections> LEFT = EnumProperty.create("left", SideConnections.class);
    public static EnumProperty<SideConnections> RIGHT = EnumProperty.create("right", SideConnections.class);

    public static ExtendableScreenBlockEntity screenBlock;

    public ExtendableScreenBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull MapColor defaultMapColor() {
        return MapColor.COLOR_GRAY;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new ExtendableScreenBlockEntity(screenBlock.getType(), blockPos, blockState);
    }

    public enum SideConnections implements StringRepresentable {
        NONE("none"),
        SCREEN("screen")
        ;

        private final String serializedName;

        SideConnections(String serializedName)
        {
            this.serializedName = serializedName;
        }

        @Override
        public @NotNull String getSerializedName() {
            return serializedName;
        }
    }

}
