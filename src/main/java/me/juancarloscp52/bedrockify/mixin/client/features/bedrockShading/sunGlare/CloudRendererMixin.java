package me.juancarloscp52.bedrockify.mixin.client.features.bedrockShading.sunGlare;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.bedrockShading.BedrockSunGlareShading;
import net.minecraft.client.renderer.CloudRenderer;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CloudRenderer.class)
public abstract class CloudRendererMixin {
    @ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 0)
    private int bedrockify$modifyCloudColor(int original) {
        BedrockSunGlareShading sunGlareShading = BedrockifyClient.getInstance().bedrockSunGlareShading;
        Vector3f color = ARGB.vector3fFromRGB24(original);
        color.mul(Mth.lerp(sunGlareShading.getSunBrightnessDelta(), 0.8f, 1f));
        return ARGB.color(new Vec3(color.x, color.y, color.z));
    }
}
