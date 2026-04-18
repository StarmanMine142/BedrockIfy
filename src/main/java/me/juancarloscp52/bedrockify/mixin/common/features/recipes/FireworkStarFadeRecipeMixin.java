package me.juancarloscp52.bedrockify.mixin.common.features.recipes;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.juancarloscp52.bedrockify.Bedrockify;
import me.juancarloscp52.bedrockify.common.features.recipes.DyeHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.FireworkStarFadeRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FireworkStarFadeRecipe.class)
public class FireworkStarFadeRecipeMixin {
    @Shadow @Final private Ingredient target;

    @ModifyReturnValue(method = "matches(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/world/level/Level;)Z", at = @At("RETURN"))
    public boolean matches(boolean original, CraftingInput craftingRecipeInput, Level world) {
        if(!Bedrockify.getInstance().settings.isBedrockRecipesEnabled())
            return original;
        boolean bl = false;
        boolean bl2 = false;

        for(int i = 0; i < craftingRecipeInput.size(); ++i) {
            ItemStack itemStack = craftingRecipeInput.getItem(i);
            if (!itemStack.isEmpty()) {
                if (DyeHelper.isDyeableItem(itemStack.getItem())) {
                    bl = true;
                } else {
                    if (!this.target.test(itemStack)) {
                        return original;
                    }

                    if (bl2) {
                        return original;
                    }

                    bl2 = true;
                }
            }
        }

        return original || (bl2 && bl);
    }

    @ModifyReturnValue(method = "assemble(Lnet/minecraft/world/item/crafting/CraftingInput;)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"))
    public ItemStack craft(ItemStack original, CraftingInput craftingRecipeInput) {
        if(!Bedrockify.getInstance().settings.isBedrockRecipesEnabled())
            return original;
        IntList list = new IntArrayList();
        ItemStack itemStack = null;

        for(int i = 0; i < craftingRecipeInput.size(); ++i) {
            ItemStack itemStack2 = craftingRecipeInput.getItem(i);
            Item item = itemStack2.getItem();
            if (DyeHelper.isDyeableItem(item)) {
                DyeHelper.getDyeItem(item).map(dyeItem -> dyeItem.getDefaultInstance().get(DataComponents.DYE)).ifPresent(dyeColor -> {
                    list.add(dyeColor.getFireworkColor());
                });
            } else if (this.target.test(itemStack2)) {
                itemStack = itemStack2.copy();
                itemStack.setCount(1);
            }
        }

        if (itemStack != null && !list.isEmpty()) {
            itemStack.update(DataComponents.FIREWORK_EXPLOSION, FireworkExplosion.DEFAULT, list, FireworkExplosion::withFadeColors);
            return itemStack;
        } else {
            return ItemStack.EMPTY;
        }
    }
}
