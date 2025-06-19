package me.juancarloscp52.bedrockify.mixin.client.features.screenSafeArea;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.minecraft.client.gui.hud.bar.ExperienceBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ExperienceBar.class)
public abstract class ExperienceBarMixin {
    /**
     * Apply screen border offset to experience bars.
     */
    @ModifyArg(method = "renderBar", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"),index = 3)
    public int modifyTextureExperienceBar(int y){
        return y - BedrockifyClient.getInstance().settings.getScreenSafeArea();
    }

    @ModifyArg(method = "renderBar", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIIIIIII)V"),index = 7)
    public int modifyTextureExperienceBar2(int y){
        return y - BedrockifyClient.getInstance().settings.getScreenSafeArea();
    }
}
