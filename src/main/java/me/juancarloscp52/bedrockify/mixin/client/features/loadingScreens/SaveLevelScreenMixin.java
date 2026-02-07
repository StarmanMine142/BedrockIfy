package me.juancarloscp52.bedrockify.mixin.client.features.loadingScreens;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.loadingScreens.LoadingScreenWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GenericMessageScreen.class)
public class SaveLevelScreenMixin extends ExtendScreenMixin {

    /**
     * Renders the loading screen widget.
     */
    @Override
    protected void bedrockify$screenRender_AfterRenderBG(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        super.bedrockify$screenRender_AfterRenderBG(context, mouseX, mouseY, delta, ci);
        if(BedrockifyClient.getInstance().settings.isLoadingScreenEnabled()){
            LoadingScreenWidget.getInstance().render(context, Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2, Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2, Component.literal(title.getString()), null, -1);
            ci.cancel();
        }
    }
}
