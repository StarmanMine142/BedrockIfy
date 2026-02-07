package me.juancarloscp52.bedrockify.mixin.common.features.recipes;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.juancarloscp52.bedrockify.Bedrockify;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;
import java.util.Set;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {

    @Unique
    private static Identifier bedrockify$getIdFromRecipeEntry(RecipeHolder<?> entry) {
        return entry.id().identifier();
    }

    @ModifyArg(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/world/item/crafting/RecipeMap;", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeMap;create(Ljava/lang/Iterable;)Lnet/minecraft/world/item/crafting/RecipeMap;"), index = 0)
    public Iterable<RecipeHolder<?>> prepareRecipes(Iterable<RecipeHolder<?>> original){
        final List<RecipeHolder<?>> editable = Lists.newArrayList(original);
        final var editableIter = editable.iterator();

        final boolean bBERecipeEnabled = Bedrockify.getInstance().settings.isBedrockRecipesEnabled();

        // --- Procedure of Recipe modification.

        // namespace equals ${Bedrockify.MOD_ID}
        final Set<String> bedrockifyRecipeIds = Sets.newHashSet(
                editable.stream()
                        .filter(entry -> bedrockify$getIdFromRecipeEntry(entry).getNamespace().equals(Bedrockify.MOD_ID))
                        .map(entry -> bedrockify$getIdFromRecipeEntry(entry).getPath())
                        .toList()
        );

        // Identifier#path contains in both vanilla and bedrockify
        final Set<String> moddedRecipeIds = Sets.newHashSet(
                editable.stream()
                        .filter(entry -> {
                            final Identifier id = bedrockify$getIdFromRecipeEntry(entry);
                            return id.getNamespace().equals(Identifier.DEFAULT_NAMESPACE) && bedrockifyRecipeIds.contains(id.getPath());
                        })
                        .map(entry -> bedrockify$getIdFromRecipeEntry(entry).getPath())
                        .toList()
        );

        // Process all the Recipes.
        while (editableIter.hasNext()) {
            var elem = editableIter.next();
            final Identifier recipeId = elem.id().identifier();
            final boolean bBERecipeIgnore = !bBERecipeEnabled && recipeId.getNamespace().equals(Bedrockify.MOD_ID);
            final boolean bConflictedVanillaRecipe = bBERecipeEnabled && moddedRecipeIds.contains(recipeId.getPath()) && recipeId.getNamespace().equals(Identifier.DEFAULT_NAMESPACE);

            if (bBERecipeIgnore || bConflictedVanillaRecipe) {
                editableIter.remove();
            }
        }

        return editable;
    }
}
