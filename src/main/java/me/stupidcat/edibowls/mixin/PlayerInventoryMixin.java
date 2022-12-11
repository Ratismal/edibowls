package me.stupidcat.edibowls.mixin;

import me.stupidcat.edibowls.LivingEntityAccessor;
import me.stupidcat.edibowls.access.LivingEntityAccess;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    @Shadow @Final public PlayerEntity player;

    @Inject(at = @At(value = "HEAD"), method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", cancellable = true)
    private void insertStack(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.isOf(Items.BOWL) && ((LivingEntityAccess)player).isConsumingFood()) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
