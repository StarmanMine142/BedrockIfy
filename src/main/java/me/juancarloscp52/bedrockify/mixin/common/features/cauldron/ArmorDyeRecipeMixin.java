package me.juancarloscp52.bedrockify.mixin.common.features.cauldron;

import me.juancarloscp52.bedrockify.Bedrockify;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.world.item.crafting.ArmorDyeRecipe;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorDyeRecipe.class)
public abstract class ArmorDyeRecipeMixin {
    /**
     * Revokes all dye recipes for the DyeableItem while bedrockCauldron feature is enabled.
     */
    @Inject(method = "matches(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/world/level/Level;)Z", at = @At("HEAD"), cancellable = true)
    private void bedrockify$revokeOriginalDyeingRecipe(CraftingInput craftingRecipeInput, Level world, CallbackInfoReturnable<Boolean> cir) {
        if (!Bedrockify.getInstance().settings.bedrockCauldron) {
            return;
        }

        cir.setReturnValue(false);
        cir.cancel();
    }
}
