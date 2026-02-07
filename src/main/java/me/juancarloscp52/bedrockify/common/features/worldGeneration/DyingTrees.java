package me.juancarloscp52.bedrockify.common.features.worldGeneration;

import com.google.common.collect.Maps;
import me.juancarloscp52.bedrockify.Bedrockify;
import net.fabricmc.fabric.api.biome.v1.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class DyingTrees {

    public static final TreeDecoratorType<FullTrunkVineTreeDecorator> VINE_DECORATOR = TreeDecoratorType.register("bedrockify:vinedecorator", FullTrunkVineTreeDecorator.CODEC);

    public static final ResourceKey<ConfiguredFeature<?, ?>> DYING_OAK_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath("bedrockify", "dying_oak_tree"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> DYING_BIRCH_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath("bedrockify", "dying_birch_tree"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> DYING_SPRUCE_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath("bedrockify", "dying_spruce_tree"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> DYING_PINE_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath("bedrockify", "dying_pine_tree"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> DYING_DARK_OAK_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath("bedrockify", "dying_dark_oak_tree"));


    public static final ResourceKey<PlacedFeature> DYING_BIRCH_TREE_PF = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath("bedrockify", "dying_birch_tree"));
    public static final ResourceKey<PlacedFeature> DYING_OAK_TREE_PF = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath("bedrockify", "dying_oak_tree"));
    public static final ResourceKey<PlacedFeature> DYING_OAK_TREE_PLAINS_PF = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath("bedrockify", "dying_oak_tree_plains"));
    public static final ResourceKey<PlacedFeature> DYING_SPRUCE_TREE_PF = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath("bedrockify", "dying_spruce_tree"));
    public static final ResourceKey<PlacedFeature> DYING_PINE_TREE_PF = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath("bedrockify", "dying_pine_tree"));
    public static final ResourceKey<PlacedFeature> DYING_DARK_OAK_TREE_PF = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath("bedrockify", "dying_dark_oak_tree"));

    private static final Predicate<BiomeSelectionContext> BIRCH_BIOME_SELECTION_CONTEXT = BiomeSelectors.includeByKey(Biomes.FOREST, Biomes.BIRCH_FOREST, Biomes.DARK_FOREST, Biomes.FLOWER_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST);
    private static final Predicate<BiomeSelectionContext> OAK_BIOME_SELECTION_CONTEXT = BiomeSelectors.includeByKey(Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.DARK_FOREST, Biomes.WINDSWEPT_FOREST);
    private static final Predicate<BiomeSelectionContext> OAK_PLAINS_BIOME_SELECTION_CONTEXT = BiomeSelectors.includeByKey(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS);
    private static final Predicate<BiomeSelectionContext> SPRUCE_BIOME_SELECTION_CONTEXT = BiomeSelectors.includeByKey(Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.TAIGA, Biomes.WINDSWEPT_FOREST, Biomes.TAIGA);
    private static final Predicate<BiomeSelectionContext> DARK_OAK_BIOME_SELECTION_CONTEXT = BiomeSelectors.includeByKey(Biomes.DARK_FOREST);

    /**
     * Register modifiers for dying tree<br>
     * BiomeModification -&gt; BiomeDecorator
     *
     * @see BiomeModification
     * @see DyingTrees.BiomeDecorator
     */
    private static final Map<BiomeModification, BiomeDecorator> DYING_TREE_DECORATORS = Util.make(Maps.newHashMap(), map -> {
        map.put(
                BiomeModifications.create(Identifier.parse("bedrockify:dyingtrees_birch")),
                new BiomeDecorator(BIRCH_BIOME_SELECTION_CONTEXT, DYING_BIRCH_TREE_PF)
        );
        map.put(
                BiomeModifications.create(Identifier.parse("bedrockify:dyingtrees_oak")),
                new BiomeDecorator(OAK_BIOME_SELECTION_CONTEXT, DYING_OAK_TREE_PF)
        );
        map.put(
                BiomeModifications.create(Identifier.parse("bedrockify:dyingtrees_oak_plains")),
                new BiomeDecorator(OAK_PLAINS_BIOME_SELECTION_CONTEXT, DYING_OAK_TREE_PLAINS_PF)
        );
        map.put(
                BiomeModifications.create(Identifier.parse("bedrockify:dyingtrees_spruce")),
                new BiomeDecorator(SPRUCE_BIOME_SELECTION_CONTEXT, DYING_SPRUCE_TREE_PF, DYING_PINE_TREE_PF)
        );
        map.put(
                BiomeModifications.create(Identifier.parse("bedrockify:dyingtrees_dark_oak")),
                new BiomeDecorator(DARK_OAK_BIOME_SELECTION_CONTEXT, DYING_DARK_OAK_TREE_PF)
        );
    });

    /**
     * A data only class that records BiomeSelectionContext and List of PlacedFeature
     *
     * @see BiomeSelectionContext
     * @see PlacedFeature
     */
    private static final class BiomeDecorator {
        public final Predicate<BiomeSelectionContext> selector;
        public final List<ResourceKey<PlacedFeature>> features;

        @SafeVarargs
        public BiomeDecorator(Predicate<BiomeSelectionContext> selector, ResourceKey<PlacedFeature>... features) {
            this.selector = selector;
            this.features = Arrays.stream(features).filter(Objects::nonNull).toList();
        }
    }

    public static void registerTrees(){
        registerDyingTrees();
    }

    private static void registerDyingTrees (){
        DYING_TREE_DECORATORS.forEach((modification, decorator) -> {
            modification.add(ModificationPhase.ADDITIONS, decorator.selector, biomeModificationContext -> {
                if (Bedrockify.getInstance().settings.dyingTrees) {
                    decorator.features.forEach(feature -> {
                        biomeModificationContext.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, feature);
                    });
                } else {
                    decorator.features.forEach(feature -> {
                        biomeModificationContext.getGenerationSettings().removeFeature(GenerationStep.Decoration.VEGETAL_DECORATION, feature);
                    });
                }
            });
        });
    }

}
