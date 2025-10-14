package me.juancarloscp52.bedrockify.mixin.client.features.hudOpacity.detailArmorBar;

import com.redlimerl.detailab.render.ArmorBarRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(ArmorBarRenderer.class)
public class ArmorBarRendererMixin {
// Disabled until DAB updates
//    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"),index = 3)
//    private float clampAlpha(float value){
//        return BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false)*value;
//    }
}