package me.juancarloscp52.bedrockify.mixin.client.features.screenSafeArea;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.Bar;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Bar.class)
public interface BarMixin {
    @WrapOperation(method = "drawExperienceLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)V"))
    private static void drawExperienceBar(DrawContext drawContext, TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow, Operation<Void> original) {
        int alpha = (int) Math.ceil(BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false)*255);
        int screenBorder = BedrockifyClient.getInstance().settings.getScreenSafeArea();

        if(!BedrockifyClient.getInstance().settings.isExpTextStyle()){
            original.call(drawContext, textRenderer, text, x, y-screenBorder, color | ((alpha) << 24),false);
        } else if(color == -8323296) {
            drawContext.drawTextWithShadow(textRenderer, text, x, y - screenBorder - 3, ColorHelper.getArgb(alpha, 127, 252, 32));
        }
    }
}
