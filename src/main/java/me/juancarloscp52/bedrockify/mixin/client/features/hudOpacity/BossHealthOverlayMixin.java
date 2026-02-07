package me.juancarloscp52.bedrockify.mixin.client.features.hudOpacity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = BossHealthOverlay.class, priority = 500)
public class BossHealthOverlayMixin {

    @WrapOperation(method = "drawBar(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/BossEvent;I[Lnet/minecraft/resources/Identifier;[Lnet/minecraft/resources/Identifier;)V", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIII)V"))
    public void applyAlphaBossBar (GuiGraphics instance, RenderPipeline pipeline, Identifier sprite, int textureWidth, int textureHeight, int u, int v, int x, int y, int width, int height, Operation<Void> original){
        instance.blitSprite(pipeline, sprite, textureWidth, textureHeight, u, v, x, y, width, height, ARGB.white(BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false)));
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"),index = 4)
    public int applyAlphaTextBossBar(int color){
        return ARGB.color(BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false), color);
    }

}
