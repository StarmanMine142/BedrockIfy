package me.juancarloscp52.bedrockify.mixin.common.features.cauldron;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.juancarloscp52.bedrockify.Bedrockify;
import net.minecraft.world.item.crafting.DyeRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DyeRecipe.class)
public abstract class ArmorDyeRecipeMixin {
    /**
     * Revokes all dye recipes for the DyeableItem while bedrockCauldron feature is enabled.
     */
    @ModifyReturnValue(method = "matches(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/world/level/Level;)Z", at = @At("RETURN"))
    private boolean bedrockify$revokeOriginalDyeingRecipe(boolean original) {
        return !Bedrockify.getInstance().settings.bedrockCauldron && original;
    }
}
