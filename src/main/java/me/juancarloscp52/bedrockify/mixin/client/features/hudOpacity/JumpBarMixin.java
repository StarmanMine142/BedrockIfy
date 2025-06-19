package me.juancarloscp52.bedrockify.mixin.client.features.hudOpacity;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.hudOpacity.HudOpacity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.JumpBar;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(JumpBar.class)
public abstract class JumpBarMixin {
    @Unique
    private final HudOpacity hudOpacity = BedrockifyClient.getInstance().hudOpacity;

    @Redirect(method = "renderBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"))
    private void setMountJumpBarColorOpacity(DrawContext instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height){
        instance.drawGuiTexture(pipeline, sprite, x, y, width, height, ColorHelper.getWhite(hudOpacity.getHudOpacity(false)));
    }

    @Redirect(method = "renderBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIIIIIII)V"))
    private void setMountJumpBarColorOpacity(DrawContext instance, RenderPipeline pipeline, Identifier sprite, int textureWidth, int textureHeight, int u, int v, int x, int y, int width, int height){
        instance.drawGuiTexture(pipeline, sprite, textureWidth, textureHeight, u, v, x, y, width, height, ColorHelper.getWhite(hudOpacity.getHudOpacity(false)));
    }
}
