package me.juancarloscp52.bedrockify.mixin.client.features.bedrockShading.sunGlare;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.bedrockShading.BedrockSunGlareShading;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow
    private @Final Minecraft minecraft;

    /**
     * Inject and Observe the reload event to be compatible with Iris shaders.
     */
    @Inject(method = "allChanged()V", at = @At("HEAD"))
    private void bedrockify$reloadWorldRendererCallback(CallbackInfo ci) {
        BedrockifyClient.getInstance().bedrockSunGlareShading.reloadCustomShaderState();
    }

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private static void bedrockify$updateSunAngleDiff(GraphicsResourceAllocator allocator, DeltaTracker tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f matrix4f, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci) {
        final BedrockSunGlareShading sunGlareShading = BedrockifyClient.getInstance().bedrockSunGlareShading;
        sunGlareShading.updateSunBrightnessDelta(tickCounter.getGameTimeDeltaPartialTick(false));
    }
}
