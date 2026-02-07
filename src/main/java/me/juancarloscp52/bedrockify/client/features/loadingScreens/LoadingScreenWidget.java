package me.juancarloscp52.bedrockify.client.features.loadingScreens;

import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameType;

import java.util.List;
import java.util.Random;
import java.util.Set;


public class LoadingScreenWidget {

    private static LoadingScreenWidget instance = null;
    private GameType gameMode = GameType.SURVIVAL;
    private static final int TIPS_NUM = 108 +1;
    private static final int CREATIVE_TIPS_NUM = 23 +1;
    private final Identifier WIDGET_TEXTURE = Identifier.fromNamespaceAndPath("bedrockify", "textures/gui/bedrockify_widgets.png");
    private Component tip;
    private static final Set<Integer> EXCLUDED_TIPS = Sets.newHashSet();
    private long lastTipUpdate = 0;
    private final ExternalLoadingTips externalLoadingTips;
    private final LogoRenderer logoDrawer;

    private LoadingScreenWidget() {
        externalLoadingTips = ExternalLoadingTips.loadSettings();
        externalLoadingTips.saveSettings();
        logoDrawer = new LogoRenderer(false);
    }

    public static LoadingScreenWidget getInstance() {
        if (instance == null) {
            instance = new LoadingScreenWidget();
        }
        return instance;
    }

    /**
     * Retrieve a loading screen tip. This tip will change every 6 seconds.
     * @return Text with the current tip.
     */
    private Component getTip() {
        // Check if gamemode has changed. Force new tooltip if gamemode has changed.
        if(hasChangedGameMode())
            tip = null;

        if (tip == null || System.currentTimeMillis() - lastTipUpdate > 6000) {
            Random randomGenerator = new Random();
            IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
            if(server !=null && server.getDefaultGameType() == GameType.CREATIVE){
                tip = Component.translatable("bedrockify.loadingTips.creative." + randomGenerator.nextInt(1,CREATIVE_TIPS_NUM));
            }
            else {
                int externalTipsLength = externalLoadingTips.length();
                int random = randomGenerator.nextInt(1,TIPS_NUM + 1 + externalTipsLength);
                if(externalTipsLength>0 && (random>TIPS_NUM || externalLoadingTips.alwaysExternalTips )){
                    tip = Component.literal(externalLoadingTips.get(randomGenerator.nextInt(externalTipsLength)));
                }else{
                    if(EXCLUDED_TIPS.contains(random))
                        return getTip();

                    tip = Component.translatable("bedrockify.loadingTips." + random);
                }
            }
            lastTipUpdate = System.currentTimeMillis();
        }
        return tip;
    }

    private boolean hasChangedGameMode(){
        GameType current = getCurrentGameMode();
        if(gameMode != current){
            gameMode = current;
            return true;
        }
        return false;
    }

    private GameType getCurrentGameMode(){
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        return server == null? GameType.SURVIVAL : server.getDefaultGameType();
    }

    /**
     * Renders the bedrockify loading screen.
     * @param drawContext Current draw context.
     * @param width window width
     * @param height window height
     * @param title Title of the loading screen.
     * @param message Message of the loading screen. Set to null to use a random tip.
     * @param progress Loading screen progress. Set to -1 is the screen has no progress bar.
     */
    public void render(GuiGraphics drawContext, int width, int height, Component title, Component message, int progress) {
        Minecraft client = Minecraft.getInstance();

        logoDrawer.renderLogo(drawContext,client.getWindow().getGuiScaledWidth(),1,(height/2) - (89 / 2));
        renderLoadingWidget(drawContext, width, height);

        Font textRenderer = Minecraft.getInstance().font;
        drawContext.drawString(textRenderer, title, width - textRenderer.width(title) / 2, height - 9 / 2 - 32, ARGB.opaque(76 | (76 << 8) | (76 << 16)),false);
        renderTextBody(drawContext, width, height, message, textRenderer);

        if (progress >= 0) {
            renderLoadingBar(drawContext, width, height, progress);
        }
    }

    private void renderLoadingWidget(GuiGraphics drawContext, int x, int y) {
        drawContext.blit(RenderPipelines.GUI_TEXTURED, WIDGET_TEXTURE, x - 256 / 2, y - 89 / 2, 0, 0, 256, 89, 256, 256);
    }


    private void renderTextBody(GuiGraphics drawContext, int x, int y, Component message, Font textRenderer) {
        if (message == null)
            message = getTip();
        List<FormattedCharSequence> text = textRenderer.split(message, 230);
        int maxLineWidth = getMaxLineWidth(textRenderer, text);
        for (int i = 0; i < 4 && i < text.size(); i++) {
            drawContext.drawString(textRenderer, text.get(i), x - maxLineWidth / 2, y - 15 + (i * 9), -1,false);
        }

    }

    private int getMaxLineWidth(Font textRenderer, List<FormattedCharSequence> text) {
        int maxLineWidth = 0;
        for (int i = 0; i < 4 && i < text.size(); i++) {
            int lineWidth = textRenderer.width(text.get(i));
            if (lineWidth > maxLineWidth)
                maxLineWidth = lineWidth;
        }
        return maxLineWidth;
    }


    private void renderLoadingBar(GuiGraphics drawContext, int x, int y, int progress) {
        int barProgress = (int) ((Mth.clamp(progress,0,100)/100.0f) * 223.0f);
        drawContext.blit(RenderPipelines.GUI_TEXTURED, WIDGET_TEXTURE, x - 111, y + 26, 0, 89, 222, 5, 256, 256);
        if (barProgress > 0)
            drawContext.blit(RenderPipelines.GUI_TEXTURED, WIDGET_TEXTURE, x - 111, y + 26, 0, 94, barProgress, 5, 256, 256);
    }

}
