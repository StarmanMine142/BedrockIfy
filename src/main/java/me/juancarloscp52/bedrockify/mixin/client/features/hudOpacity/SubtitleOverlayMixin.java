package me.juancarloscp52.bedrockify.mixin.client.features.hudOpacity;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SubtitleOverlay.class)
public class SubtitleOverlayMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"), index=4)
    public int setOpacityFill(int color){
        return ARGB.color(minecraft.options.getBackgroundOpacity(0.8f) * BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false), color);
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"), index=4)
    public int setOpacityText(int color){
        return ARGB.color(BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false), color);
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V"), index=4)
    public int setOpacityText2(int color){
        return ARGB.color(BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false), color);
    }
}
