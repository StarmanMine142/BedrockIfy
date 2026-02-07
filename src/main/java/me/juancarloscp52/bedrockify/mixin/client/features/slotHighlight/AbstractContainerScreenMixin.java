package me.juancarloscp52.bedrockify.mixin.client.features.slotHighlight;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.BedrockifyClientSettings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.inventory.CartographyTableScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
    @Unique
    private static final int LINE_RENDER_WIDTH = 1;
    @Unique
    private static final int SLOT_RENDER_SIZE = 16;
    @Unique
    private Slot currentSlot;

    @Shadow
    protected int leftPos;
    @Shadow
    protected int topPos;

    @Shadow
    abstract boolean isHovering(Slot slot, double pointX, double pointY);

    @Inject(method = {"renderSlotHighlightBack", "renderSlotHighlightFront"}, at = @At("HEAD"), cancellable = true)
    private static void bedrockify$cancelVanillaHighlight(CallbackInfo ci) {
        BedrockifyClientSettings settings = BedrockifyClient.getInstance().settings;
        if (settings.isSlotHighlightEnabled()) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "renderContents", at = @At("STORE"))
    private Slot bedrockify$storeSlotInLoop(Slot slot) {
        this.currentSlot = slot;
        return slot;
    }

    /**
     * Draw the current slot in green.
     */
    @Inject(method = "renderContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderSlotHighlightBack(Lnet/minecraft/client/gui/GuiGraphics;)V"))
    private void bedrockify$customHighlightColor(GuiGraphics drawContext, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        BedrockifyClientSettings settings = BedrockifyClient.getInstance().settings;
        if (!settings.isSlotHighlightEnabled() || this.currentSlot == null) {
            return;
        }
        if (!this.isHovering(currentSlot, mouseX, mouseY) || !currentSlot.isActive()) {
            return;
        }

        final AbstractContainerScreen<?> $this = AbstractContainerScreen.class.cast(this);
        final int highlight1 = settings.getHighLightColor1();
        final int highlight2 = settings.getHighLightColor2();

        final int expandStartX, expandStartY, expandEndX, expandEndY;
        if (($this instanceof AbstractFurnaceScreen && currentSlot.index == 2) ||
                ($this instanceof CraftingScreen && currentSlot.index == 0) ||
                ($this instanceof StonecutterScreen && currentSlot.index == 1) ||
                ($this instanceof CartographyTableScreen && currentSlot.index == 2)
        ) {
            expandStartX = expandEndX = 4;
            expandStartY = expandEndY = 4;
        } else if ($this instanceof LoomScreen && currentSlot.index == 3) {
            expandStartX = expandEndX = 4;
            expandStartY = 4;
            expandEndY = 4;
        } else if ($this instanceof MerchantScreen && currentSlot.index == 2) {
            expandStartX = expandEndX = 4;
            expandStartY = 4;
            expandEndY = 4;
        } else {
            expandStartX = expandEndX = expandStartY = expandEndY = 0;
        }

        final int fillStartX = currentSlot.x - expandStartX;
        final int fillStartY = currentSlot.y - expandStartY;
        final int fillEndX = currentSlot.x + expandEndX + SLOT_RENDER_SIZE;
        final int fillEndY = currentSlot.y + expandEndY + SLOT_RENDER_SIZE;
        final int outlineLeftX = fillStartX - LINE_RENDER_WIDTH;
        final int outlineTopY = fillStartY - LINE_RENDER_WIDTH;
        final int outlineRightX = fillEndX + LINE_RENDER_WIDTH;
        final int outlineBottomY = fillEndY + LINE_RENDER_WIDTH;

        // ** outlines
        // Top-horizontal
        drawContext.fill(outlineLeftX, outlineTopY, outlineRightX, outlineTopY + LINE_RENDER_WIDTH, highlight1);
        // Bottom-horizontal
        drawContext.fill(outlineLeftX, outlineBottomY - LINE_RENDER_WIDTH, outlineRightX, outlineBottomY, highlight1);
        // Left-vertical
        drawContext.fill(outlineLeftX, outlineTopY, outlineLeftX + LINE_RENDER_WIDTH, outlineBottomY, highlight1);
        // Right-vertical
        drawContext.fill(outlineRightX - LINE_RENDER_WIDTH, outlineTopY, outlineRightX, outlineBottomY, highlight1);
        // ** end of outlines

        // highlight
        drawContext.fill(fillStartX, fillStartY, fillEndX, fillEndY, highlight2);
    }
}
