package me.juancarloscp52.bedrockify.mixin.client.features.biggerDraggingItem;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.BedrockifyClientSettings;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
    @Unique
    private static final float MULTIPLIER = 1.3f;

    @WrapOperation(method = "renderCursorStack", at= @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawItem(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"))
    private void drawBiggerItem(HandledScreen<?> instance, DrawContext drawContext, ItemStack stack, int xPosition, int yPosition, String amountText, Operation<Void> original){
        BedrockifyClientSettings settings = BedrockifyClient.getInstance().settings;
        if(!settings.isBiggerIconsEnabled()){
            original.call(instance, drawContext,stack, xPosition, yPosition, amountText);
            return;
        }
        drawContext.getMatrices().pushMatrix();
        drawContext.getMatrices().scale(MULTIPLIER);
        original.call(instance, drawContext, stack, MathHelper.ceil(xPosition/ MULTIPLIER)-2, MathHelper.ceil(yPosition/ MULTIPLIER)-2, amountText);
        drawContext.getMatrices().popMatrix();
    }

}
