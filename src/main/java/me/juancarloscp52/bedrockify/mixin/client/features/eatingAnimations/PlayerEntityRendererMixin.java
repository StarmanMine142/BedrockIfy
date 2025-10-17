package me.juancarloscp52.bedrockify.mixin.client.features.eatingAnimations;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.eatingAnimations.IEatingState;
import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin<AvatarlikeEntity extends PlayerLikeEntity & ClientPlayerLikeEntity> {
    @Inject(method = "updateRenderState", at = @At("HEAD"))
    private void bedrockify$storeEatingState(AvatarlikeEntity avatarlikeEntity, PlayerEntityRenderState playerEntityRenderState, float f, CallbackInfo ci) {
        if (!BedrockifyClient.getInstance().settings.isEatingAnimationsEnabled() || !(playerEntityRenderState instanceof IEatingState state)) {
            return;
        }

        ItemStack mainHandStack = avatarlikeEntity.getMainHandStack();
        ItemStack offHandStack = avatarlikeEntity.getOffHandStack();
        if (bedrockify$checkEatingAction(avatarlikeEntity, playerEntityRenderState, Hand.MAIN_HAND, mainHandStack)) {
            state.setEatingHand(Hand.MAIN_HAND);
        } else if (bedrockify$checkEatingAction(avatarlikeEntity, playerEntityRenderState, Hand.OFF_HAND, offHandStack)) {
            state.setEatingHand(Hand.OFF_HAND);
        } else {
            state.setEatingHand(null);
        }
    }

    @Unique
    private boolean bedrockify$checkEatingAction(AvatarlikeEntity entity, PlayerEntityRenderState state, Hand hand, ItemStack itemStack) {
        return entity.isUsingItem() && state.activeHand == hand && (itemStack.getUseAction() == UseAction.EAT || itemStack.getUseAction() == UseAction.DRINK);
    }
}
