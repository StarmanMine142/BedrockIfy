package me.juancarloscp52.bedrockify.client.features.bedrockShading;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

/**
 * @author Shaddatic
 */
public class BedrockBlockShading {

    public float getBlockShade (Direction direction){
        Minecraft client = Minecraft.getInstance();
        boolean isNether = client.player != null && client.player.level().dimension() == Level.NETHER;
        return switch (direction) {
            case UP -> 1.0f;
            case DOWN -> isNether ? 0.9f : 0.87f;
            case NORTH, SOUTH -> 0.95f;
            default -> 0.9f;
        };
    }
    public float getLiquidShade(Direction direction, boolean isLuminous){
        return switch (direction) {
            case UP -> 1.0f;
            case DOWN -> isLuminous ? 0.9f : 0.5f;
            default -> isLuminous ? 0.9f : 0.6f;
        };
    }
}
