package me.juancarloscp52.bedrockify.mixin.client.features.useAnimations;

import com.mojang.authlib.GameProfile;
import me.juancarloscp52.bedrockify.client.features.useAnimations.AnimationsHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    public LocalPlayerMixin(ClientLevel world, GameProfile profile) {
        super(world, profile);
    }

    /**
     * Drop animation for Hotbar item.
     */
    @Inject(method = "drop", at = @At("HEAD"))
    private void bedrockify$animateHotbarItemDrop(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        if (entireStack) {
            return;
        }

        AnimationsHelper.doBobbingAnimation(this.getMainHandItem());
    }
}
