package me.juancarloscp52.bedrockify.mixin.client.features.loadingScreens;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.loadingScreens.LoadingScreenWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import net.minecraft.client.world.ClientChunkLoadProgress;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.world.chunk.ChunkLoadMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelLoadingScreen.class)
public abstract class LevelLoadingScreenMixin extends Screen {
    @Shadow @Final private ClientChunkLoadProgress chunkLoadProgress;

    @Shadow private long lastNarrationTime;

    protected LevelLoadingScreenMixin(Text title) {
        super(title);
    }

    /**
     * Draws the loading screen widget and allows to toggle the chunk map loading widget.
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if(!BedrockifyClient.getInstance().settings.isLoadingScreenEnabled())
            return;

        int xPosition = this.width / 2;
        int yPosition = this.height / 2;
        LoadingScreenWidget.getInstance().render(context, xPosition, yPosition, Text.translatable("narrator.loading", Text.translatable("loading.progress", (this.chunkLoadProgress.getLoadProgress()*100.0F)).getString()), null, (int)(this.chunkLoadProgress.getLoadProgress()*100.0F));

        long l = Util.getMeasuringTimeMs();
        if (l - this.lastNarrationTime > 2000L) {
            this.lastNarrationTime = l;
            this.narrateScreenIfNarrationEnabled(true);
        }

        ChunkLoadMap chunkLoadMap = chunkLoadProgress.getChunkLoadMap();
        if (BedrockifyClient.getInstance().settings.isShowChunkMapEnabled() && chunkLoadProgress.hasProgress() && chunkLoadMap !=null )
            LevelLoadingScreen.drawChunkMap(context, xPosition, yPosition + yPosition / 2 + 89 / 4, 1, 0, chunkLoadMap);

        info.cancel();
    }

}
