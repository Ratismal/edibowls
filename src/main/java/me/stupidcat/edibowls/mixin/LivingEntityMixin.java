package me.stupidcat.edibowls.mixin;

import me.stupidcat.edibowls.LivingEntityAccessor;
import me.stupidcat.edibowls.access.LivingEntityAccess;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements LivingEntityAccess {
    @Shadow protected ItemStack activeItemStack;

    public boolean isConsumingFood = false;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;finishUsing(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/item/ItemStack;", shift = At.Shift.BEFORE), method = "consumeItem()V")
    private void finishUsingBefore(CallbackInfo ci) {
        if (this.activeItemStack.isFood()) {
            isConsumingFood = true;
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setStackInHand(Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)V", shift = At.Shift.BEFORE), method = "consumeItem()V", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void finishUsingAfter(CallbackInfo ci, Hand hand, ItemStack stack) {
        var user = (LivingEntity)(Object)(this);

        if (stack.isOf(Items.BOWL)) {
            stack.setCount(0);
            user.setStackInHand(hand, Items.AIR.getDefaultStack());
            user.clearActiveItem();
            ci.cancel();
        }
    }

    @Inject(at = @At(value = "RETURN"), method = "consumeItem()V")
    private void finishUsingFinal(CallbackInfo ci) {
        isConsumingFood = false;
    }

    @Override
    public boolean isConsumingFood() {
        return isConsumingFood;
    }
}
