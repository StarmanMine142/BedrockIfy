package me.juancarloscp52.bedrockify.client.features.savingOverlay;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.BedrockifyClientSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class SavingOverlay{

    private final Identifier WIDGET_TEXTURE = Identifier.fromNamespaceAndPath("bedrockify", "textures/gui/bedrockify_widgets.png");
    private boolean saving = false;
    private long timer=0;
    private float renderTimer=0;
    private final Minecraft client = Minecraft.getInstance();

    public void render(GuiGraphicsExtractor drawContext){
        final BedrockifyClientSettings settings = BedrockifyClient.getInstance().settings;
        if(saving || System.currentTimeMillis()-timer<3000){
//            RenderSystem.setShaderColor(1,1,1,BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false));
            // Draw chest
            drawContext.blit(RenderPipelines.GUI_TEXTURED, WIDGET_TEXTURE, client.getWindow().getGuiScaledWidth()-(21+settings.getScreenSafeArea()), 19 + settings.getScreenSafeArea(), 0, 99, 16, 17, 256, 256);
            // Draw arrow
            renderTimer+= BedrockifyClient.getInstance().deltaTime*0.000000001f;
            drawContext.blit(RenderPipelines.GUI_TEXTURED, WIDGET_TEXTURE, client.getWindow().getGuiScaledWidth()-(19+settings.getScreenSafeArea()), 5 + settings.getScreenSafeArea() + Mth.floor(Mth.abs(Mth.sin(renderTimer * 3.1415926F) * 6)), 16, 100, 12, 15, 256, 256);
//            RenderSystem.setShaderColor(1,1,1,1);
        }
    }

    public void setSaving(boolean saving) {
        if(this.saving && !saving)
            timer=System.currentTimeMillis();
        this.saving = saving;
    }
}
