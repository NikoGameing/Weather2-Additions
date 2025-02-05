// first person mod compat
package nikosmods.weather2additions.mixin;

import net.minecraft.world.item.ItemStack;
import nikosmods.weather2additions.items.itemreg.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = dev.tr7zw.firstperson.LogicHandler.class, remap = false)
public abstract class LogicHandler {

    @Inject(method = "showVanillaHands(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void showVanillaHands(ItemStack mainHand, ItemStack offHand, CallbackInfoReturnable<Boolean> cir) {
        if (mainHand.getItem().asItem() == Items.TABLET.get() || offHand.getItem().asItem() == Items.TABLET.get()) {
            cir.cancel();
            cir.setReturnValue(true);
        }
    }
}
