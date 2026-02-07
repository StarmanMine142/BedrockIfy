package me.juancarloscp52.bedrockify.mixin.client.features.bedrockShading.sunGlare;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.bedrockShading.BedrockSunGlareShading;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.biome.Biomes;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SkyRenderer.class)
public abstract class SkyRendererMixin {
    @Unique
    private final BedrockSunGlareShading sunGlareShading = BedrockifyClient.getInstance().bedrockSunGlareShading;

    @WrapOperation(method = "renderSun", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4fStack;scale(FFF)Lorg/joml/Matrix4f;"))
    private Matrix4f bedrockify$modifySunRadius(Matrix4fStack instance, float x, float y, float z, Operation<Matrix4f> original) {
        final float scale = Mth.lerp(sunGlareShading.getSunBrightnessDelta(), 1.3f, 1f);
        return original.call(instance, x * scale, y, z * scale);
    }

    @ModifyExpressionValue(method = "renderSun", at = @At(value = "NEW", target = "org/joml/Vector4f"))
    private Vector4f bedrockify$modifySunBrightness(Vector4f original) {
        final float brightness = Mth.lerp(sunGlareShading.getSunBrightnessDelta(), 1.5f + sunGlareShading.getSunIntensityDelta() * 0.5f, 1.0f);
        return original.mul(brightness, brightness, brightness, original.w);
    }

    @ModifyVariable(method = "renderSkyDisc", at = @At("HEAD"))
    private int bedrockify$modifySkyColor(int original) {
        Minecraft client = Minecraft.getInstance();
        final BedrockSunGlareShading sunGlareShading = BedrockifyClient.getInstance().bedrockSunGlareShading;
        if (!sunGlareShading.shouldApplyShading() || client.level == null) {
            return original;
        }

        final float delta = sunGlareShading.getSunBrightnessDelta();
        final float skyAtt = Mth.lerp(sunGlareShading.getSunIntensityDelta(), 1f, 0.6f);
        Vector3f color = ARGB.vector3fFromRGB24(original);

        // Closer to the Sun, Darken the Sky, based on camera angle. Use a different multiplier for each channel in order to better match bedrock edition sky color.
        float multiplierBlue = Mth.lerp(delta, skyAtt, 1f);
        float multiplierRed = Mth.lerp(delta, skyAtt - 0.16f, 1f);
        float multiplierGreen = Mth.lerp(delta, skyAtt - 0.06f, 1f);

        // Use same dimming for all three channels when the biome is PALE GARDEN, with some extra darkness.
        if (client.player!=null && client.level.getBiome(client.player.blockPosition()).is(Biomes.PALE_GARDEN.identifier())){
            final float darker = Mth.lerp(sunGlareShading.getSunIntensityDelta(), 1f, 0.4f);
            multiplierRed = Mth.lerp(delta, darker, 1f);
            multiplierGreen = Mth.lerp(delta, darker, 1f);
            multiplierBlue = Mth.lerp(delta, darker, 1f);
        }
        color.mul(multiplierRed, multiplierGreen, multiplierBlue);
        return ARGB.color(new Vec3(color.x, color.y, color.z));
    }
}
