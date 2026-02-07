package me.juancarloscp52.bedrockify.common.features.cauldron;

import net.minecraft.world.level.block.state.properties.IntegerProperty;

public final class BedrockCauldronProperties {
    private BedrockCauldronProperties() {
    }

    public static final int MAX_LEVEL_6 = 6;
    public static final int MAX_LEVEL_8 = 8;

    public static final IntegerProperty LEVEL_6 = IntegerProperty.create("c_level", 1, MAX_LEVEL_6);
    public static final IntegerProperty LEVEL_8 = IntegerProperty.create("c_level", 1, MAX_LEVEL_8);
}
