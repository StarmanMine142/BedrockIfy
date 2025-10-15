package me.juancarloscp52.bedrockify.mixin.client.features.editionBranding;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LogoDrawer.class)
public class LogoDrawerMixin {

    @Shadow @Final public static Identifier EDITION_TEXTURE;

    @Redirect(method = "draw(Lnet/minecraft/client/gui/DrawContext;IFI)V", at= @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIFFIIIII)V",ordinal = 1))
    public void drawTexture(DrawContext drawContext, RenderPipeline pipeline, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, int color){
        if(!BedrockifyClient.getInstance().settings.hideEditionBranding){
            drawContext.drawTexture(pipeline, EDITION_TEXTURE, x, y, u, v, width, height, textureWidth, textureHeight, color);
        }
    }

}
