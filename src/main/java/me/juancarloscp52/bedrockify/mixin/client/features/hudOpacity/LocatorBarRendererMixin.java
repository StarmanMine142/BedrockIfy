package me.juancarloscp52.bedrockify.mixin.client.features.hudOpacity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.hudOpacity.HudOpacity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.contextualbar.LocatorBarRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = LocatorBarRenderer.class, priority = 500)
public class LocatorBarRendererMixin {

    private final HudOpacity hudOpacity = BedrockifyClient.getInstance().hudOpacity;

    @WrapOperation(method = "renderBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    public void renderBarOpacity(GuiGraphics instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        instance.blitSprite(pipeline, sprite, x, y, width, height, ARGB.white(hudOpacity.getHudOpacity(false)));
    }

    @ModifyArg(method = "method_70870", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIII)V"),index = 6)
    private static int renderBarAddonsOpacity(int color){
        return ARGB.color(BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false), color);
    }
    @WrapOperation(method = "method_70870", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private static void renderBarAddonsOpacity(GuiGraphics instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        instance.blitSprite(pipeline, sprite, x, y, width, height, ARGB.white(BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false)));
    }
}

