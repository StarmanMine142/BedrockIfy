package me.juancarloscp52.bedrockify.mixin.client.features.editionBranding;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LogoRenderer.class)
public class LogoRendererMixin {

    @Shadow @Final public static Identifier MINECRAFT_EDITION;

    @Redirect(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IFI)V", at= @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIIII)V",ordinal = 1))
    public void drawTexture(GuiGraphicsExtractor drawContext, RenderPipeline pipeline, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, int color){
        if(!BedrockifyClient.getInstance().settings.hideEditionBranding){
            drawContext.blit(pipeline, MINECRAFT_EDITION, x, y, u, v, width, height, textureWidth, textureHeight, color);
        }
    }

}
