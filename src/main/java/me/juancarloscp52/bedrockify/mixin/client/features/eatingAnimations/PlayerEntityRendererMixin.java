package me.juancarloscp52.bedrockify.mixin.client.features.eatingAnimations;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.eatingAnimations.IEatingState;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
    @Inject(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V", at = @At("HEAD"))
    private void bedrockify$storeEatingState(LivingEntity livingEntity, LivingEntityRenderState livingEntityRenderState, float f, CallbackInfo ci) {
        if (!BedrockifyClient.getInstance().settings.isEatingAnimationsEnabled() || !(livingEntityRenderState instanceof IEatingState state)) {
            return;
        }

        ItemStack mainHandStack = livingEntity.getMainHandStack();
        ItemStack offHandStack = livingEntity.getOffHandStack();
        if (bedrockify$checkEatingAction(livingEntityRenderState, Hand.MAIN_HAND, mainHandStack)) {
            state.setEatingHand(Hand.MAIN_HAND);
        } else if (bedrockify$checkEatingAction(livingEntityRenderState, Hand.OFF_HAND, offHandStack)) {
            state.setEatingHand(Hand.OFF_HAND);
        } else {
            state.setEatingHand(null);
        }
    }

    @Unique
    private boolean bedrockify$checkEatingAction(LivingEntityRenderState state, Hand hand, ItemStack itemStack) {
        PlayerEntityRenderState playerState = (PlayerEntityRenderState) state;
        return playerState.itemUseTime > 0 && playerState.activeHand == hand && (itemStack.getUseAction() == UseAction.EAT || itemStack.getUseAction() == UseAction.DRINK);
    }
}
