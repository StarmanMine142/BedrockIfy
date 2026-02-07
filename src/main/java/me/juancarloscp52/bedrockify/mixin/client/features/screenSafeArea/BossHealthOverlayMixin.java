package me.juancarloscp52.bedrockify.mixin.client.features.screenSafeArea;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.minecraft.client.gui.components.BossHealthOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BossHealthOverlay.class)

/*
  Apply screen safe area to BossBar Hud.
 */
public abstract class BossHealthOverlayMixin {

    @ModifyArg(method = "render", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;drawBar(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/BossEvent;)V"),index = 2)
    public int applyScreenBorderToBossBar(int y){
        return y + BedrockifyClient.getInstance().settings.getScreenSafeArea();
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"),index = 3)
    public int applyScreenBorderToBossName(int y){
        return y + BedrockifyClient.getInstance().settings.getScreenSafeArea();
    }

}
