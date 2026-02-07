package me.juancarloscp52.bedrockify.mixin.client.features.bedrockCubeMap;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PanoramaRenderer.class)
public abstract class PanoramaRendererMixin {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/CubeMap;render(Lnet/minecraft/client/Minecraft;FF)V"))
    private void bedrockify$onCubeMapRender(CubeMap instance, Minecraft client, float x, float y, Operation<Void> original) {
        if (BedrockifyClient.getInstance().settings.bedrockCubeMap) {
            original.call(instance, client, x, -y);
        } else {
            original.call(instance, client, x, y);
        }
    }
}
