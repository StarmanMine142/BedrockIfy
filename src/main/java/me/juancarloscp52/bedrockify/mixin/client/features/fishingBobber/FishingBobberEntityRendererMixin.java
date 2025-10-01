package me.juancarloscp52.bedrockify.mixin.client.features.fishingBobber;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.fishingBobber.FishingBobber3DModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.client.render.entity.state.FishingBobberEntityState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntityRenderer.class)
public abstract class FishingBobberEntityRendererMixin {
    @Unique
    private Model<FishingBobberEntityState> bobberModel;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void bedrockify$injectCtor(EntityRendererFactory.Context context, CallbackInfo ci) {
        this.bobberModel = new FishingBobber3DModel<>(context.getPart(FishingBobber3DModel.MODEL_LAYER));
    }

    @Inject(method = "vertex", at = @At("HEAD"), cancellable = true)
    private static void bedrockify$cancelOriginalBobberRendering(CallbackInfo ci) {
        if (!BedrockifyClient.getInstance().settings.fishingBobber3D) {
            return;
        }

        ci.cancel();
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void bedrockify$render3DBobber(FishingBobberEntityState fishingBobberEntityState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci) {
        if (!BedrockifyClient.getInstance().settings.fishingBobber3D) {
            return;
        }

        matrixStack.push();
        matrixStack.translate(0f, -0.0075f, 0f);
        orderedRenderCommandQueue.submitModel(this.bobberModel, fishingBobberEntityState, matrixStack, FishingBobber3DModel.RENDER_LAYER, fishingBobberEntityState.light, OverlayTexture.DEFAULT_UV, fishingBobberEntityState.outlineColor, null);
        matrixStack.pop();
    }
}
