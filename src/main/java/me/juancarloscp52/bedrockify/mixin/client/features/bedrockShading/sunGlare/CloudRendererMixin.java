package me.juancarloscp52.bedrockify.mixin.client.features.bedrockShading.sunGlare;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.bedrockShading.BedrockSunGlareShading;
import net.minecraft.client.render.CloudRenderer;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CloudRenderer.class)
public abstract class CloudRendererMixin {
    @ModifyVariable(method = "renderClouds", at = @At("HEAD"), ordinal = 0)
    private int bedrockify$modifyCloudColor(int original) {
        BedrockSunGlareShading sunGlareShading = BedrockifyClient.getInstance().bedrockSunGlareShading;
        Vector3f color = ColorHelper.toRgbVector(original);
        color.mul(MathHelper.lerp(sunGlareShading.getSunBrightnessDelta(), 0.8f, 1f));
        return ColorHelper.getArgb(new Vec3d(color.x, color.y, color.z));
    }
}
