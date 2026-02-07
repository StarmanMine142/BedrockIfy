package me.juancarloscp52.bedrockify.mixin.common.features.fernBonemeal;

import me.juancarloscp52.bedrockify.Bedrockify;
import me.juancarloscp52.bedrockify.common.features.fernBonemeal.FernBonemeal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GrassBlock.class)
public class GrassBlockMixin {
    @ModifyVariable(method = "performBonemeal",at=@At("STORE"), ordinal = 0)
    public Holder<?> addFern(Holder<?> registryEntry, ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {

        if(!Bedrockify.getInstance().settings.fernBonemeal)
            return registryEntry;

        if(registryEntry.unwrapKey().isPresent() && registryEntry.unwrapKey().get() instanceof ResourceKey<?> key){
            ResourceKey<Biome> biome = world.getBiome(pos).unwrapKey().orElseThrow();
            if(biome.equals(Biomes.TAIGA) || biome.equals(Biomes.OLD_GROWTH_SPRUCE_TAIGA) || biome.equals(Biomes.SNOWY_TAIGA) || biome.equals(Biomes.OLD_GROWTH_PINE_TAIGA) || biome.equals(Biomes.JUNGLE) || biome.equals(Biomes.BAMBOO_JUNGLE) || biome.equals(Biomes.SPARSE_JUNGLE)) {
                if (key.identifier().getPath().equals("grass_bonemeal") && random.nextInt(4) == 0) {
                    return world.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE).get(FernBonemeal.SINGLE_PIECE_OF_FERN_PLACED.identifier()).orElse(null);
                }
            }
        }

        return registryEntry;
    }

}
