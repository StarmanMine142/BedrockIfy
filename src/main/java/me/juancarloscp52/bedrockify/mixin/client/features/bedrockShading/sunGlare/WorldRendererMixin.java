package me.juancarloscp52.bedrockify.mixin.client.features.bedrockShading.sunGlare;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.bedrockShading.BedrockSunGlareShading;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.ObjectAllocator;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow
    private @Final MinecraftClient client;

    /**
     * Inject and Observe the reload event to be compatible with Iris shaders.
     */
    @Inject(method = "reload()V", at = @At("HEAD"))
    private void bedrockify$reloadWorldRendererCallback(CallbackInfo ci) {
        BedrockifyClient.getInstance().bedrockSunGlareShading.reloadCustomShaderState();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private static void bedrockify$updateSunAngleDiff(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f matrix4f, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci) {
        final BedrockSunGlareShading sunGlareShading = BedrockifyClient.getInstance().bedrockSunGlareShading;
        sunGlareShading.updateSunBrightnessDelta(tickCounter.getTickProgress(false));
    }
}
