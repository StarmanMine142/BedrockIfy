package me.juancarloscp52.bedrockify.mixin.client.features.loadingScreens;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.loadingScreens.LoadingScreenWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.util.Mth;
import net.minecraft.server.level.progress.ChunkLoadStatusView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelLoadingScreen.class)
public abstract class LevelLoadingScreenMixin extends Screen {
    @Shadow @Final private LevelLoadTracker loadTracker;

    @Shadow private long lastNarration;

    protected LevelLoadingScreenMixin(Component title) {
        super(title);
    }

    /**
     * Draws the loading screen widget and allows to toggle the chunk map loading widget.
     */
    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    public void render(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if(!BedrockifyClient.getInstance().settings.isLoadingScreenEnabled())
            return;

        int xPosition = this.width / 2;
        int yPosition = this.height / 2;
        int loadPercent = Mth.ceil(this.loadTracker.serverProgress() * 100);
        LoadingScreenWidget.getInstance().render(context, xPosition, yPosition, Component.translatable("narrator.loading", Component.translatable("loading.progress", loadPercent).getString()), null, loadPercent);

        long l = Util.getMillis();
        if (l - this.lastNarration > 2000L) {
            this.lastNarration = l;
            this.triggerImmediateNarration(true);
        }

        ChunkLoadStatusView chunkLoadMap = loadTracker.statusView();
        if (BedrockifyClient.getInstance().settings.isShowChunkMapEnabled() && loadTracker.hasProgress() && chunkLoadMap !=null )
            LevelLoadingScreen.extractChunksForRendering(context, xPosition, yPosition + yPosition / 2 + 89 / 4, 1, 0, chunkLoadMap);

        info.cancel();
    }

}
