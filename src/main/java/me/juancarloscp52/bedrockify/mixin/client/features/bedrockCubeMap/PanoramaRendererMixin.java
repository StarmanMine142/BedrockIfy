package me.juancarloscp52.bedrockify.mixin.client.features.bedrockCubeMap;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.minecraft.client.renderer.Panorama;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Panorama.class)
public abstract class PanoramaRendererMixin {
    @ModifyExpressionValue(method = "extractRenderState", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/state/OptionsRenderState;panoramaSpeed:D", opcode = Opcodes.GETFIELD))
    private double bedrockify$onCubeMapRender(double original) {
        if (BedrockifyClient.getInstance().settings.bedrockCubeMap) {
            return -original;
        } else {
            return original;
        }
    }
}
