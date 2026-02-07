package me.juancarloscp52.bedrockify.mixin.client.features.eatingAnimations;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.eatingAnimations.IEatingState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public class PlayerModelMixin extends HumanoidModel<AvatarRenderState> {
    public PlayerModelMixin(ModelPart root) {
        super(root);
    }

    @Inject(method = "setupAnim", at=@At("TAIL"))
    private void applyEatingAnimation(AvatarRenderState playerEntityRenderState, CallbackInfo info){
        if(!BedrockifyClient.getInstance().settings.isEatingAnimationsEnabled())
            return;

        if (!(playerEntityRenderState instanceof IEatingState state)) {
            return;
        }

        final var eatingHand = state.getEatingHand();
        final HumanoidArm mainArm = playerEntityRenderState.mainArm;

        eatingHand.ifPresent(hand -> {
            if (hand == InteractionHand.MAIN_HAND) {
                playEatingAnimation(playerEntityRenderState, mainArm);
            } else if (hand == InteractionHand.OFF_HAND) {
                playEatingAnimation(playerEntityRenderState, mainArm.getOpposite());
            }
        });
    }

    @Unique
    private static final float ITEM_START_TIME = 8f/20f; //in second
    @Unique
    private static final float ITEM_INTERVAL_TIME = 4f/20f; //in second

    @Unique
    private void playEatingAnimation(AvatarRenderState state, HumanoidArm targetArm) {
        final float ticks = BedrockifyClient.getInstance().deltaTime * 0.000000001f;
        final float itemUseTime = state.ticksUsingItem;
        float smoothingTicks = false ? (float) (ticks - Math.floor(ticks)) : 0; //if you want to add an option for spothing the anim, it's already here, just replace the false
//        float itemStartProgress = Math.min(livingEntity.getItemUseTime() + smoothingTicks, 20f* ITEM_START_TIME)/20f/ ITEM_START_TIME;
        float itemStartProgress = Math.min(itemUseTime + smoothingTicks, 20f * ITEM_START_TIME) * 0.05f / ITEM_START_TIME;
        float itemIntervalProgress = (itemUseTime * 0.05f < ITEM_START_TIME) ? 0.0f : (((itemUseTime - (int) ITEM_START_TIME *20) % (int) (ITEM_INTERVAL_TIME *20)) + smoothingTicks)* ITEM_INTERVAL_TIME;
        float animPitch = itemStartProgress * -degToMatAngle(60.0f) + itemIntervalProgress * degToMatAngle(11.25f);
        float animYaw = itemStartProgress * -degToMatAngle(22.5f) + itemIntervalProgress * degToMatAngle(11.25f);
        float animRoll = itemStartProgress * -degToMatAngle(5.625f) + itemIntervalProgress * degToMatAngle(11.25f);

        if (targetArm == HumanoidArm.RIGHT) {
            this.rightArm.xRot += animPitch;
            this.rightArm.yRot += animYaw;
            this.rightArm.zRot += animRoll;
        } else {
            this.leftArm.xRot += animPitch;
            this.leftArm.yRot -= animYaw;
            this.leftArm.zRot += animRoll;
        }
    }

    @Unique
    private float degToMatAngle(float angle)
    {
//        return 7.07f * angle / 360;
        return 7.07f * angle * 0.002777778f;
    }
}
