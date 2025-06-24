package me.juancarloscp52.bedrockify.mixin.client.features.bedrockShading.sunGlare;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.bedrockShading.BedrockSunGlareShading;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.BiomeKeys;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
    @Shadow
    private @Final MinecraftClient client;

    /**
     * Modify the Sky color based on Camera angle.
     *
     * @return modified sky color
     */
    @ModifyReturnValue(method = "getSkyColor", at = @At("RETURN"))
    private int bedrockify$modifySkyColor(int original) {
        final BedrockSunGlareShading sunGlareShading = BedrockifyClient.getInstance().bedrockSunGlareShading;
        if (!sunGlareShading.shouldApplyShading() || this.client.world == null) {
            return original;
        }

        final float delta = sunGlareShading.getSunBrightnessDelta();
        final float skyAtt = MathHelper.lerp(sunGlareShading.getSunIntensityDelta(), 1f, 0.6f);
        Vec3d color = Vec3d.unpackRgb(original);

        // Closer to the Sun, Darken the Sky, based on camera angle. Use a different multiplier for each channel in order to better match bedrock edition sky color.
        float multiplierBlue = MathHelper.lerp(delta, skyAtt, 1f);
        float multiplierRed = MathHelper.lerp(delta, skyAtt - 0.16f, 1f);
        float multiplierGreen = MathHelper.lerp(delta, skyAtt - 0.06f, 1f);

        // Use same dimming for all three channels when the biome is PALE GARDEN, with some extra darkness.
        if (client.player!=null && client.world.getBiome(client.player.getBlockPos()).matchesId(BiomeKeys.PALE_GARDEN.getValue())){
            final float darker = MathHelper.lerp(sunGlareShading.getSunIntensityDelta(), 1f, 0.4f);
            multiplierRed = MathHelper.lerp(delta, darker, 1f);
            multiplierGreen = MathHelper.lerp(delta, darker, 1f);
            multiplierBlue = MathHelper.lerp(delta, darker, 1f);
        }

        return ColorHelper.getArgb(color.multiply(multiplierRed, multiplierGreen, multiplierBlue));
    }

    @ModifyReturnValue(method = "getCloudsColor", at = @At("RETURN"))
    private int bedrockify$modifyCloudsColor(int original) {
        BedrockSunGlareShading sunGlareShading = BedrockifyClient.getInstance().bedrockSunGlareShading;
        Vec3d color = Vec3d.unpackRgb(original);
        return ColorHelper.getArgb(color.multiply(MathHelper.clampedLerp(0.8d, 1.0d, sunGlareShading.getSunBrightnessDelta())));
    }

}
