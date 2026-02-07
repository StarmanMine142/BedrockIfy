package me.juancarloscp52.bedrockify.mixin.client.features.hudOpacity;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {

    @ModifyArg(method = "submitBlitFromItemAtlas", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/state/BlitRenderState;<init>(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/gui/render/TextureSetup;Lorg/joml/Matrix3x2f;IIIIFFFFILnet/minecraft/client/gui/navigation/ScreenRectangle;Lnet/minecraft/client/gui/navigation/ScreenRectangle;)V"), index = 11)
    private int setAlpha(int color){
        return ARGB.color(BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false), color);
    }

    @ModifyArg(method = "submitBlitFromItemAtlas", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/state/BlitRenderState;<init>(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/gui/render/TextureSetup;Lorg/joml/Matrix3x2f;IIIIFFFFILnet/minecraft/client/gui/navigation/ScreenRectangle;Lnet/minecraft/client/gui/navigation/ScreenRectangle;)V"), index = 0)
    private RenderPipeline setAlpha(RenderPipeline pipeline){
        return RenderPipelines.GUI_TEXTURED;
    }

}
