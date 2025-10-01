package me.juancarloscp52.bedrockify.mixin.client.features.bedrockShading.sunGlare;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.bedrockShading.BedrockSunGlareShading;
import net.minecraft.client.render.SkyRendering;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SkyRendering.class)
public abstract class SkyRenderingMixin {
    @Unique
    private final BedrockSunGlareShading sunGlareShading = BedrockifyClient.getInstance().bedrockSunGlareShading;

    @WrapOperation(method = "renderSun", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4fStack;scale(FFF)Lorg/joml/Matrix4f;"))
    private Matrix4f bedrockify$modifySunRadius(Matrix4fStack instance, float x, float y, float z, Operation<Matrix4f> original) {
        final float scale = MathHelper.lerp(sunGlareShading.getSunBrightnessDelta(), 1.3f, 1f);
        return original.call(instance, x * scale, y, z * scale);
    }

    @ModifyExpressionValue(method = "renderSun", at = @At(value = "NEW", target = "org/joml/Vector4f"))
    private Vector4f bedrockify$modifySunBrightness(Vector4f original) {
        final float brightness = MathHelper.lerp(sunGlareShading.getSunBrightnessDelta(), 1.5f + sunGlareShading.getSunIntensityDelta() * 0.5f, 1.0f);
        return original.mul(brightness, brightness, brightness, 1);
    }
}
