package me.juancarloscp52.bedrockify.common.features.fernBonemeal;

import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;


public class FernBonemeal {
    public static final ResourceKey<ConfiguredFeature<?, ?>> SINGLE_PIECE_OF_FERN = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath("bedrockify", "single_piece_of_fern"));
    public static final ResourceKey<PlacedFeature> SINGLE_PIECE_OF_FERN_PLACED = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath("bedrockify", "single_piece_of_fern_placed"));

}
