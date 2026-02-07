package me.juancarloscp52.bedrockify.mixin.client.features.savingOverlay;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
/*
 * Updates the saving status for the saving overlay.
 */
public abstract class MinecraftServerMixin {

    @Shadow public abstract boolean isDedicatedServer();

    @Inject(method = "saveAllChunks", at= @At("HEAD"))
    private void startSaving(CallbackInfoReturnable<Boolean> info){
        if(!this.isDedicatedServer() && BedrockifyClient.getInstance().settings.isSavingOverlayEnabled())
            BedrockifyClient.getInstance().overlay.savingOverlay.setSaving(true);
    }

    @Inject(method = "saveAllChunks", at= @At("RETURN"))
    private void stopSaving(CallbackInfoReturnable<Boolean> info){
        if(!this.isDedicatedServer() && BedrockifyClient.getInstance().settings.isSavingOverlayEnabled())
            BedrockifyClient.getInstance().overlay.savingOverlay.setSaving(false);
    }
}
