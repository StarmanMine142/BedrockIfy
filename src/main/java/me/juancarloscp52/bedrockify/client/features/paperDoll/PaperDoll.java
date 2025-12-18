package me.juancarloscp52.bedrockify.client.features.paperDoll;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.BedrockifyClientSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.include.com.google.common.collect.Sets;

import java.util.Set;

public class PaperDoll {
    private final MinecraftClient client;
    private final int size = 20;
    private long lastTimeShown = 0;
    private final BedrockifyClientSettings settings = BedrockifyClient.getInstance().settings;

    private static final Set<String> TARGET_POSE_NAMES = Sets.newHashSet(EntityPose.GLIDING.name(), EntityPose.SWIMMING.name(), "CRAWLING");
    private static final float SCREEN_PIXEL_TO_GL_SCALE = 0.0625f;

    public PaperDoll(MinecraftClient client) {
        this.client = client;
    }

    /**
     * Render the player at the top left of the screen.
     * The player will be rendered only when the player is not riding another entity, and it is sneaking, running, using elytra, using an item, underwater, or using a shield.
     */
    public void renderPaperDoll(DrawContext drawContext) {
        if (!settings.isShowPaperDollEnabled())
            return;

        if (this.client.currentScreen instanceof InventoryScreen || this.client.currentScreen instanceof CreativeInventoryScreen) {
            return;
        }

        if (client.player != null) {
            //If the player does an action that must show the player entity gui, set the counter to the current time.
            if (shouldShow(client.player))
                lastTimeShown = System.currentTimeMillis();

            // If the difference between the current game ticks and showTicks is less than 100 ticks, draw the player entity.
            if ((!client.player.isRiding() && !client.player.isSleeping() && System.currentTimeMillis() - lastTimeShown < 2000))
                drawPaperDoll(drawContext);
        }
    }

    /**
     * Checks player's action.
     *
     * @param player An instance of a {@link ClientPlayerEntity}.
     * @return {@code true} if condition matches.
     */
    private static boolean shouldShow(ClientPlayerEntity player) {
        return player.isSneaking() ||
                player.isSprinting() ||
                player.isSubmergedInWater() ||
                player.getAbilities().flying ||  // flying in Creative mode
                player.isBlocking() ||
                player.isUsingItem() ||
                TARGET_POSE_NAMES.contains(player.getPose().name());
    }

    /**
     * Draw the player entity in the specified position on screen.
     */
    private void drawPaperDoll(DrawContext drawContext) {
        ClientPlayerEntity player = client.player;
        if (player == null)
            return;

        // Position the entity on screen.
        int posX = 10;
        int offsetX = -7;
        int renderBottomPosY;
        int offsetY = 2;

        // Determine the position of the doll depending on the position of the overlay text.
        int textPosY = settings.getPositionHUDHeight();
        if (textPosY >= 2 * size + 10) {
            renderBottomPosY = textPosY;
        } else {
            renderBottomPosY = textPosY + size * 2 + 5;
            if (settings.getFPSHUDoption()==2)
                renderBottomPosY += 10;
            if (settings.isShowPositionHUDEnabled())
                renderBottomPosY += 10;
        }

        // If the player is elytra flying, the entity must be manually centered depending on the pitch.
        if (player.getPose().equals(EntityPose.GLIDING)) {
            posX = 0;
            offsetY = 15 - MathHelper.ceil(size * 2 * toMaxAngleRatio(player.getPitch()));
        }
        // If the player is swimming, the entity must also be centered in the Y axis.
        else if (player.isSwimming()) {
            offsetY = -2;
        }
        int safeArea = settings.overlayIgnoresSafeArea? 0 : settings.getScreenSafeArea();
        int x1 = posX + safeArea;
        int y2 = renderBottomPosY + safeArea;
        int x2 = x1 + MathHelper.ceil(size * 3.25f);
        int y1 = Math.max(safeArea,  y2 - size * 3);
        drawContext.enableScissor(x1, y1, x2, y2);

        // Store previous entity rotations.
        float bodyYaw = player.bodyYaw;
        float yaw = player.getYaw();
        float headYaw = player.headYaw;


        // Set the entity desired rotation for drawing.
        float angle = 145;
        if (player.getPose().equals(EntityPose.GLIDING) || player.isBlocking()) {
            player.headYaw = angle;
        } else {
            player.setYaw(headYaw - bodyYaw + angle);
            player.headYaw = player.getYaw();
        }
        player.bodyYaw = angle;

        Vector3f translation = new Vector3f(offsetX * SCREEN_PIXEL_TO_GL_SCALE, player.getHeight() * 0.5f + offsetY * SCREEN_PIXEL_TO_GL_SCALE, 0.0F);
        Quaternionf rotation = new Quaternionf().rotateZ((float)Math.PI);

        // Draw the entity.
        EntityRenderManager entityRenderManager = MinecraftClient.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? super LivingEntity, ?> entityRenderer = entityRenderManager.getRenderer(player);
        EntityRenderState entityRenderState = entityRenderer.getAndUpdateRenderState(player, 1.0F);
        entityRenderState.light = 15728880;
        entityRenderState.shadowPieces.clear();
        entityRenderState.outlineColor = 0;

        drawContext.addEntity(entityRenderState, (float)size, translation, rotation, null, x1, y1, x2, y2);

        // Restore previous entity rotations.
        player.bodyYaw = bodyYaw;
        player.setYaw(yaw);
        player.headYaw = headYaw;

        drawContext.disableScissor();
    }

    private float toMaxAngleRatio(float angle) {
        return (90 + angle) / 180;
    }

}
