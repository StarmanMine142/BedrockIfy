package me.juancarloscp52.bedrockify.mixin.client.features.heldItemTooltips;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.heldItemTooltips.HeldItemTooltips;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public class ItemTooltipsMixin {
    @Shadow private ItemStack lastToolHighlight;

    @Shadow private int toolHighlightTimer;

    @Shadow @Final private Minecraft minecraft;

    /**
     * Draw custom tooltips for effects and enchantments before the heldItemTooltip is rendered.
     */
    @Redirect(method = "extractSelectedItemName", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;textWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)V"))
    private void drawCustomTooltips(GuiGraphicsExtractor instance, Font textRenderer, Component text, int x, int y, int width, int color) {
        BedrockifyClient.getInstance().heldItemTooltips.drawItemWithCustomTooltips(instance,textRenderer, text, x, Minecraft.getInstance().getWindow().getGuiScaledHeight() - 38, color, lastToolHighlight);
    }

    /**
     * Show the item tooltip when changing from an item to another of the same type and name IFF different tooltips.
     */
    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 1))
    private boolean interceptItemStack(ItemStack itemStack) {
        ItemStack nextItem = this.minecraft.player.getMainHandItem();
        HeldItemTooltips heldItemTooltips = BedrockifyClient.getInstance().heldItemTooltips;
        if(itemStack.getItem() == this.lastToolHighlight.getItem() && !heldItemTooltips.equals(lastToolHighlight,nextItem)){
            this.toolHighlightTimer = 41;
            return true;
        }

        return lastToolHighlight.isEmpty();
    }
}
