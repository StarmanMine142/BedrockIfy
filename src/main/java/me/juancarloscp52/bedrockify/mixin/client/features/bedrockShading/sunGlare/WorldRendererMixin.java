package me.juancarloscp52.bedrockify.mixin.client.features.bedrockShading.sunGlare;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
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

}
