package me.juancarloscp52.bedrockify.mixin.client.features.hudOpacity;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.SubtitlesHud;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SubtitlesHud.class)
public class SubtitlesHudMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"), index=4)
    public int setOpacityFill(int color){
        return ColorHelper.withAlpha(client.options.getTextBackgroundOpacity(0.8f) * BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false), color);
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"), index=4)
    public int setOpacityText(int color){
        return ColorHelper.withAlpha(BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false), color);
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"), index=4)
    public int setOpacityText2(int color){
        return ColorHelper.withAlpha(BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false), color);
    }
}
