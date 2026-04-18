package me.juancarloscp52.bedrockify.mixin.client.features.loadingScreens;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.loadingScreens.LoadingScreenWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProgressScreen.class)
public class ProgressScreenMixin extends Screen {


    @Shadow private Component stage;
    @Shadow private int progress;
    protected ProgressScreenMixin(Component title) {
        super(title);
    }

    /**
     * Renders the loading screen widgets with progress bar if necessary.
     */
    @Inject(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"), cancellable = true)
    public void renderLoadScreen(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if(!BedrockifyClient.getInstance().settings.isLoadingScreenEnabled() || minecraft == null){
            return;
        }
        super.extractRenderState(context, mouseX, mouseY, delta);
        if (title != null) {
            if (this.stage != null && this.progress != 0) {
                LoadingScreenWidget.getInstance().render(context, minecraft.getWindow().getGuiScaledWidth() / 2, minecraft.getWindow().getGuiScaledHeight() / 2, this.title, this.stage, this.progress);
            } else {
                LoadingScreenWidget.getInstance().render(context, minecraft.getWindow().getGuiScaledWidth() / 2, minecraft.getWindow().getGuiScaledHeight() / 2, this.title, null, -1);
            }
        } else if (this.stage != null && this.progress != 0) {
            LoadingScreenWidget.getInstance().render(context, minecraft.getWindow().getGuiScaledWidth() / 2, minecraft.getWindow().getGuiScaledHeight() / 2, this.stage, null, this.progress);
        } else {
            LoadingScreenWidget.getInstance().render(context, minecraft.getWindow().getGuiScaledWidth() / 2, minecraft.getWindow().getGuiScaledHeight() / 2, Component.literal(""), null, -1);
        }

        info.cancel();
    }

}
