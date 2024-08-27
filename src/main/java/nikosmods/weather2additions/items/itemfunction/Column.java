package nikosmods.weather2additions.items.itemfunction;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

public record Column(int x, int z, Level level) {
    public String cereal() {
        return String.format("%d,%d,%s", x, z, level.dimension().location());
    }
    public static Column uncereal(String string) {
        String [] strings = string.split(",");
        String [] dimension = strings[2].split(":");
        return new Column(
                Integer.parseInt(strings[0]),
                Integer.parseInt(strings[1]),
                ServerLifecycleHooks.getCurrentServer().getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimension[0], dimension[1])))
                );

    }
}
