package me.juancarloscp52.bedrockify.mixin.client.features.useAnimations;

import me.juancarloscp52.bedrockify.client.features.useAnimations.AnimationsHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    /**
     * Animate always by receiving S2C packet.<br>
     * Original method prevents the bobbing animation when decrementing and damaging.
     *
     * @see ClientPacketListener#handleContainerSetSlot
     */
    @Inject(method = "handleContainerSetSlot", at = @At("RETURN"))
    private void bedrockify$animateAlwaysSlotUpdate(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
        final ItemStack itemStack = packet.getItem();
        final int slotIdx = packet.getSlot();
        if (packet.getContainerId() != 0 && !InventoryMenu.isHotbarSlot(slotIdx) || itemStack == null) {
            return;
        }

        AnimationsHelper.doBobbingAnimation(itemStack);
    }

    /**
     * Animate always by receiving S2C packet.<br>
     * This handles a packet that could not be caught by {@link ClientPacketListener#handleContainerSetSlot}.
     *
     * @see ClientPacketListener#handleContainerContent
     */
    @Inject(method = "handleContainerContent", at = @At("RETURN"))
    private void bedrockify$animateAlwaysInventory(ClientboundContainerSetContentPacket packet, CallbackInfo ci) {
        final Player player = Minecraft.getInstance().player;
        if (packet.containerId() != 0 || player == null) {
            return;
        }

        final int target = AnimationsHelper.consumeChangedSlot();
        if (!Inventory.isHotbarSlot(target) && target != Inventory.SLOT_OFFHAND) {
            return;
        }

        AnimationsHelper.doBobbingAnimation(player.getInventory().getItem(target));
    }
}
