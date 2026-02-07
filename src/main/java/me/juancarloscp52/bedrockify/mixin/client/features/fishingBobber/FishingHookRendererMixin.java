package me.juancarloscp52.bedrockify.mixin.client.features.fishingBobber;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.fishingBobber.FishingBobber3DModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.client.renderer.entity.state.FishingHookRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingHookRenderer.class)
public abstract class FishingHookRendererMixin {
    @Unique
    private Model<FishingHookRenderState> bobberModel;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void bedrockify$injectCtor(EntityRendererProvider.Context context, CallbackInfo ci) {
        this.bobberModel = new FishingBobber3DModel<>(context.bakeLayer(FishingBobber3DModel.MODEL_LAYER));
    }

    @Inject(method = "vertex", at = @At("HEAD"), cancellable = true)
    private static void bedrockify$cancelOriginalBobberRendering(CallbackInfo ci) {
        if (!BedrockifyClient.getInstance().settings.fishingBobber3D) {
            return;
        }

        ci.cancel();
    }

    @Inject(method = "submit", at = @At("RETURN"))
    private void bedrockify$render3DBobber(FishingHookRenderState fishingBobberEntityState, PoseStack matrixStack, SubmitNodeCollector orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci) {
        if (!BedrockifyClient.getInstance().settings.fishingBobber3D) {
            return;
        }

        matrixStack.pushPose();
        matrixStack.translate(0f, -0.0075f, 0f);
        orderedRenderCommandQueue.submitModel(this.bobberModel, fishingBobberEntityState, matrixStack, FishingBobber3DModel.RENDER_LAYER, fishingBobberEntityState.lightCoords, OverlayTexture.NO_OVERLAY, fishingBobberEntityState.outlineColor, null);
        matrixStack.popPose();
    }
}
