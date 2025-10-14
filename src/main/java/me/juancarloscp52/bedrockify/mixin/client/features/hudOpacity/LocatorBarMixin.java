package me.juancarloscp52.bedrockify.mixin.client.features.hudOpacity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.hudOpacity.HudOpacity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.LocatorBar;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = LocatorBar.class, priority = 500)
public class LocatorBarMixin {

    private final HudOpacity hudOpacity = BedrockifyClient.getInstance().hudOpacity;

    @WrapOperation(method = "renderBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"))
    public void renderBarOpacity(DrawContext instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        instance.drawGuiTexture(pipeline, sprite, x, y, width, height, ColorHelper.getWhite(hudOpacity.getHudOpacity(false)));
    }

    @ModifyArg(method = "method_70870", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIIII)V"),index = 6)
    private static int renderBarAddonsOpacity(int color){
        return ColorHelper.withAlpha(BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false), color);
    }
    @WrapOperation(method = "method_70870", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"))
    private static void renderBarAddonsOpacity(DrawContext instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        instance.drawGuiTexture(pipeline, sprite, x, y, width, height, ColorHelper.getWhite(BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false)));
    }
}

