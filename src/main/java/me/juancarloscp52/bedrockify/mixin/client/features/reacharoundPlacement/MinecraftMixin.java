package me.juancarloscp52.bedrockify.mixin.client.features.reacharoundPlacement;


import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.reacharoundPlacement.ReachAroundPlacement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Minecraft.class)
public abstract class MinecraftMixin extends ReentrantBlockableEventLoop<Runnable> {
    @Shadow
    public LocalPlayer player;
    @Shadow
    @Nullable
    public HitResult hitResult;
    @Shadow
    public abstract boolean isLocalServer();

    public MinecraftMixin(String string) {
        super(string);
    }


    /**
     * Allows the player to use the reachAround placement feature if enabled.
     */
    @Inject(method = "startUseItem", at = @At("HEAD"))
    private void bedrockify$modifyCrosshairTarget(CallbackInfo ci) {
        if (this.player == null) {
            return;
        }
        if (!BedrockifyClient.getInstance().settings.isReacharoundEnabled() || (!isLocalServer() && !BedrockifyClient.getInstance().settings.isReacharoundMultiplayerEnabled())) {
            return;
        }

        if (BedrockifyClient.getInstance().reachAroundPlacement.canReachAround()) {
            final LocalPlayer player = this.player;
            final BlockPos targetPos = ReachAroundPlacement.getFacingSteppingBlockPos(player);
            this.hitResult = new BlockHitResult(new Vec3(targetPos.getX(), player.getY(), targetPos.getZ()), player.getDirection(), targetPos, false);
        }
    }
}
