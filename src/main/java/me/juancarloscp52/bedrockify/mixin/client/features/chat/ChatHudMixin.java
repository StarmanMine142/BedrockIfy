package me.juancarloscp52.bedrockify.mixin.client.features.chat;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.BedrockifyClientSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

    @Shadow @Final private List<ChatHudLine.Visible> visibleMessages;
    @Shadow protected abstract boolean isChatFocused();
    @Shadow public abstract double getChatScale();
    @Shadow @Final private MinecraftClient client;
    @Shadow private int scrolledLines;
    @Shadow public abstract int getVisibleLineCount();
    @Shadow protected abstract int getLineHeight();
    @Shadow abstract int forEachVisibleLine(ChatHud.OpacityRule opacityRule, ChatHud.LineConsumer lineConsumer);
    @Shadow abstract int getWidth();

    @Unique
    private double bottomY;
    @Unique
    BedrockifyClientSettings settings = BedrockifyClient.getInstance().settings;
    @Unique
    private static final String DRAW_CONTEXT_FILL_METHOD_SIGNATURE = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V";

    @Unique
    private int bedrockify$getSafeArea() {
        return settings.overlayIgnoresSafeArea ? 0 : settings.getScreenSafeArea();
    }

    @Unique
    private float bedrockify$getHudOpacity() {
        return BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false);
    }

    @Unique
    private int bedrockify$calcChatHudTopOffset() {
        final int safeArea = this.bedrockify$getSafeArea();
        return settings.getPositionHUDHeight() + ((settings.getPositionHUDHeight() < 50) ? 50 : 0) + (settings.isShowPositionHUDEnabled() ? 10 : 0) + ((settings.getFPSHUDoption() == 2) ? 10 : 0) + safeArea - 6;
    }

    @ModifyExpressionValue(method = "forEachVisibleLine", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud$OpacityRule;calculate(Lnet/minecraft/client/gui/hud/ChatHudLine$Visible;)F"))
    private float bedrockify$changeHudOpacity(float original) {
        if (this.isChatFocused()) {
            return original;
        }

        return original * this.bedrockify$getHudOpacity();
    }

    @ModifyReturnValue(method = "getVisibleLineCount", at = @At("RETURN"))
    private int bedrockify$modifyLineCount(int original) {
        if (!settings.isBedrockChatEnabled() || client.inGameHud.getDebugHud().shouldShowDebugHud()) {
            return original;
        }

        final int height = this.client.getWindow().getScaledHeight() - this.bedrockify$calcChatHudTopOffset();
        final int lines = MathHelper.ceil((float) height / this.getLineHeight()) - 4;
        return Math.min(original, lines);
    }

    @ModifyExpressionValue(method = "render(Lnet/minecraft/client/gui/hud/ChatHud$Backend;IIZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;floor(F)I"))
    private int bedrockify$moveChatHud(int original) {
        if (!settings.isBedrockChatEnabled() || client.inGameHud.getDebugHud().shouldShowDebugHud()) {
            return original;
        }

        return (int) this.bottomY;
    }

    /**
     * Use bedrock-like chat if enabled.
     */
    @Inject(method = "render(Lnet/minecraft/client/gui/hud/ChatHud$Backend;IIZ)V", at = @At("HEAD"))
    private void bedrockify$gatherInfo(ChatHud.Backend backend, int windowHeight, int currentTick, boolean expanded, CallbackInfo ci) {
        if (!settings.isBedrockChatEnabled() || client.inGameHud.getDebugHud().shouldShowDebugHud()) {
            return;
        }

        int notifications = 0;
        for (ChatHudLine.Visible line : this.visibleMessages) {
            if (currentTick - line.addedTime() < 200) {
                ++notifications;
            }
        }
        final int visibleLines = this.getVisibleLineCount();
        final int shownLines = this.isChatFocused() ?
                Math.min(visibleLines, this.visibleMessages.size() - this.scrolledLines) :
                Math.min(visibleLines, notifications);
        final double shownHeight = shownLines * (this.getLineHeight() + this.client.options.getChatLineSpacing().getValue());
        this.bottomY = (8 + shownHeight + this.bedrockify$calcChatHudTopOffset()) / this.getChatScale();
    }
}
