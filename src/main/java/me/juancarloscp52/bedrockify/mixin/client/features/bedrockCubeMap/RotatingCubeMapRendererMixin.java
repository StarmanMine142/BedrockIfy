package me.juancarloscp52.bedrockify.mixin.client.features.bedrockCubeMap;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RotatingCubeMapRenderer.class)
public abstract class RotatingCubeMapRendererMixin {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/CubeMapRenderer;draw(Lnet/minecraft/client/MinecraftClient;FF)V"))
    private void bedrockify$onCubeMapRender(CubeMapRenderer instance, MinecraftClient client, float x, float y, Operation<Void> original) {
        if (BedrockifyClient.getInstance().settings.bedrockCubeMap) {
            original.call(instance, client, x, -y);
        } else {
            original.call(instance, client, x, y);
        }
    }
}
