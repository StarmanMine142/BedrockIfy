package me.juancarloscp52.bedrockify.mixin.client.features.bedrockShading.sunGlare;

import com.mojang.blaze3d.systems.RenderSystem;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.bedrockShading.BedrockSunGlareShading;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow
    private @Final MinecraftClient client;

    @Unique
    private float sunRadiusDelta = 1f;
    @Unique
    private static final String RENDER_SKY_METHOD_SIGNATURE = "renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V";

    /**
     * Inject and Observe the reload event to be compatible with Iris shaders.
     */
    @Inject(method = "reload()V", at = @At("HEAD"))
    private void bedrockify$reloadWorldRendererCallback(CallbackInfo ci) {
        BedrockifyClient.getInstance().bedrockSunGlareShading.reloadCustomShaderState();
    }

    /**
     * Calculate the angle difference between Camera and Sun, and Store the delta including the rain factor.
     */
    @Inject(method = RENDER_SKY_METHOD_SIGNATURE, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getSkyColor(Lnet/minecraft/util/math/Vec3d;F)Lnet/minecraft/util/math/Vec3d;"))
    private void bedrockify$updateSunAngleDiff(Matrix4f matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        if (this.client.world == null) {
            return;
        }

        final BedrockSunGlareShading sunGlareShading = BedrockifyClient.getInstance().bedrockSunGlareShading;
        final float rainGradient = this.client.world.getRainGradient(tickDelta);
        if (MathHelper.approximatelyEquals(rainGradient, 1f) || !sunGlareShading.shouldApplyShading()) {
            this.sunRadiusDelta = 1f;
            return;
        }

        sunGlareShading.updateAngleDiff();
        this.sunRadiusDelta = sunGlareShading.getSunAngleDiff() + rainGradient;
    }

    /**
     * Modify the Sun radius from stored delta.<br>
     * Original radius is <code>30.0F</code>.
     */
    @ModifyConstant(method = RENDER_SKY_METHOD_SIGNATURE, constant = @Constant(floatValue = 30.0f, ordinal = 0))
    private float bedrockify$modifySunRadius(float original) {
        if (!BedrockifyClient.getInstance().bedrockSunGlareShading.shouldApplyShading() || this.sunRadiusDelta >= 1f) {
            return original;
        }

        return MathHelper.clampedLerp(original * 1.3f, original, this.sunRadiusDelta);
    }

    @Redirect(method = "renderSky", at=@At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V",ordinal = 2))
    private void bedrockify$modifySunIntensity(float red, float green, float blue, float alpha){
        float value = MathHelper.clampedLerp(2.0f, 1.0f, this.sunRadiusDelta);
        RenderSystem.setShaderColor(value,value,value,alpha);
    }

    @Redirect(method = "renderClouds", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getCloudsColor(F)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d a(ClientWorld instance, float tickDelta){
        return instance.getCloudsColor(tickDelta).multiply(MathHelper.clampedLerp(0.8d, 1.0d, this.sunRadiusDelta));
    }
}
