package me.juancarloscp52.bedrockify.mixin.client.features.biggerDraggingItem;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.BedrockifyClientSettings;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
    @Unique
    private static final float MULTIPLIER = 1.3f;

    @WrapOperation(method = "extractCarriedItem", at= @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractFloatingItem(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"))
    private void drawBiggerItem(AbstractContainerScreen<?> instance, GuiGraphicsExtractor drawContext, ItemStack stack, int xPosition, int yPosition, String amountText, Operation<Void> original){
        BedrockifyClientSettings settings = BedrockifyClient.getInstance().settings;
        if(!settings.isBiggerIconsEnabled()){
            original.call(instance, drawContext,stack, xPosition, yPosition, amountText);
            return;
        }
        drawContext.pose().pushMatrix();
        drawContext.pose().scale(MULTIPLIER);
        original.call(instance, drawContext, stack, Mth.ceil(xPosition/ MULTIPLIER)-2, Mth.ceil(yPosition/ MULTIPLIER)-2, amountText);
        drawContext.pose().popMatrix();
    }

}
