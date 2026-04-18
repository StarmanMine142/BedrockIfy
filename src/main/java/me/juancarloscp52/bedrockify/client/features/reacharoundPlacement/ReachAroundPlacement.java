package me.juancarloscp52.bedrockify.client.features.reacharoundPlacement;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ReachAroundPlacement {
    private final Minecraft client;

    public ReachAroundPlacement(Minecraft client) {
        this.client = client;
    }

    public void renderIndicator(GuiGraphicsExtractor drawContext) {
        if (BedrockifyClient.getInstance().settings.isReacharoundIndicatorEnabled() && BedrockifyClient.getInstance().settings.isReacharoundEnabled() && (client.isLocalServer() || BedrockifyClient.getInstance().settings.isReacharoundMultiplayerEnabled()) && this.canReachAround() ) {
            drawContext.fill((client.getWindow().getGuiScaledWidth() / 2) - 5, (client.getWindow().getGuiScaledHeight() / 2) + 5, (client.getWindow().getGuiScaledWidth() / 2) + 4, (client.getWindow().getGuiScaledHeight() / 2) + 6, (100 << 24) + (255 << 8));
        }
    }

    public boolean canReachAround() {
        if (client.player == null || client.level == null || client.hitResult == null)
            return false;

        // crosshairTarget must be MISS.
        if (!client.hitResult.getType().equals(HitResult.Type.MISS)) {
            return false;
        }

        final LocalPlayer player = client.player;
        final BlockPos targetPos = getFacingSteppingBlockPos(player);

        // Not sneaking and must sneak in settings.
        if (!player.isShiftKeyDown() && BedrockifyClient.getInstance().settings.isReacharoundSneakingEnabled()) {
            return false;
        }
        // Player may be flying, climbing the ladder or vines, or on the Fluid with sneaking.
        if (!player.onGround()) {
            return false;
        }
        // There is a non-replaceable block at the ReachAround target position.
        if (!client.level.getBlockState(targetPos).canBeReplaced()) {
            return false;
        }

        return getRaycastIntersection(player).isPresent();
    }

    /**
     * Helper method that retrieves Reach-Around block position.
     *
     * @return The position of the block to be placed.
     */
    public static BlockPos getFacingSteppingBlockPos(@NotNull Entity player) {
        return player.getOnPos().relative(player.getDirection());
    }

    /**
     * Draws a vector from the player's eyes to the end of the reach distance, in the direction the player is facing. We can use this to check if the block is valid for placement.
     * @return The position of the intersection between the raycast and the surface of the target block.
     * @author axialeaa
     */
    private Optional<Vec3> getRaycastIntersection(@NotNull LocalPlayer player) {
        Vec3 rayStartPos = player.getEyePosition();
        Vec3 rayEndPos = player.getViewVector(1.0F).scale(player.blockInteractionRange()).add(rayStartPos);

        return new AABB(getFacingSteppingBlockPos(player)).clip(rayStartPos, rayEndPos);
    }
}
