package me.juancarloscp52.bedrockify.mixin.client.features.loadingScreens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ExtendScreenMixin {
    @Shadow
    protected @Final Component title;

    @Inject(method = "renderWithTooltipAndSubtitles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderBackground(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", shift = At.Shift.AFTER), cancellable = true)
    protected void bedrockify$screenRender_AfterRenderBG(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // Empty body for overridable mixin
    }
}
