package me.juancarloscp52.bedrockify.mixin.client.features.useAnimations;

import me.juancarloscp52.bedrockify.client.features.useAnimations.AnimationsHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    /**
     * This targets the lambda of {@link net.minecraft.client.multiplayer.prediction.PredictiveAction} in {@link MultiPlayerGameMode#useItem}.<br>
     * It is not possible to retrieve their changes in {@link net.minecraft.client.multiplayer.ClientPacketListener#handleContainerSetSlot}.<br>
     * This result is used in {@link AnimationsHelper#consumeChangedSlot}.
     */
    @Inject(method = "useItem", at = @At("RETURN"))
    private void bedrockify$consumeItem(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (player == null || !(cir.getReturnValue() instanceof InteractionResult.Success)) {
            return;
        }

        final ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(Items.GOAT_HORN)) {
            AnimationsHelper.doBobbingAnimation(itemStack);
            return;
        }

        if (player.isCreative()) {
            return;
        }

        AnimationsHelper.notifyChangedSlot((hand == InteractionHand.OFF_HAND) ? Inventory.SLOT_OFFHAND : player.getInventory().getSelectedSlot());
    }
}
