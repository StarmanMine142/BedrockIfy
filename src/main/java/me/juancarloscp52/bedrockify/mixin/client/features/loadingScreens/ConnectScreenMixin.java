package me.juancarloscp52.bedrockify.mixin.client.features.loadingScreens;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.loadingScreens.LoadingScreenWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ConnectScreen.class)
public class ConnectScreenMixin extends Screen {

    @Shadow
    Connection connection;
    @Shadow boolean aborted;
    @Shadow @Final
    Screen parent;

    protected ConnectScreenMixin(Component title) {
        super(title);
    }

    /**
     * Draws the loading screen widget.
     */
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"))
    public void drawLoadingScreenWidget(GuiGraphics drawContext, Font textRenderer, Component text, int x, int y, int color) {
        if(BedrockifyClient.getInstance().settings.isLoadingScreenEnabled()){
            LoadingScreenWidget.getInstance().render(drawContext, Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2, Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2, Component.literal(text.getString()), null, -1);
        }else{
            drawContext.drawCenteredString(textRenderer, text, x, y, color);
        }
    }

    /**
     * Move the cancel bottom down.
     */
    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/ConnectScreen;addDrawableChild(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;"))
    public <T extends GuiEventListener & Renderable & NarratableEntry> T addDrawableChild(ConnectScreen connectScreen, T drawableElement) {
        if(BedrockifyClient.getInstance().settings.isLoadingScreenEnabled()){
            return (T) this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (buttonWidget) -> {
                this.aborted = true;
                if (this.connection != null) {
                    this.connection.disconnect(Component.translatable("connect.aborted"));
                }
                this.minecraft.setScreen(this.parent);
            }).pos(this.width / 2 - 100, (int) Math.ceil(Minecraft.getInstance().getWindow().getGuiScaledHeight() * 0.75D)).width(200).build());
        }else{
            return this.addRenderableWidget(drawableElement);
        }
    }
}
