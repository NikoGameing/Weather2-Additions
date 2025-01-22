package nikosmods.weather2additions.keyreg;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

public class KeyRegistries {
    public static final Lazy<KeyMapping> cycleMapForward = Lazy.of(() -> new KeyMapping("key.weather2_additions.cyclemapforward", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET,"key.categories.misc"));
    public static final Lazy<KeyMapping> cycleMapBackward = Lazy.of(() -> new KeyMapping("key.weather2_additions.cyclemapbackward", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_BRACKET,"key.categories.misc"));
    public static void register(RegisterKeyMappingsEvent RegKeyMap) {
        RegKeyMap.register(cycleMapForward.get());
        RegKeyMap.register(cycleMapBackward.get());
    }
}
