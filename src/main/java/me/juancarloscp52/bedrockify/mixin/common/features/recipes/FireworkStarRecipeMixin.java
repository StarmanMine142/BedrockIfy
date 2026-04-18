package me.juancarloscp52.bedrockify.mixin.common.features.recipes;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.juancarloscp52.bedrockify.Bedrockify;
import me.juancarloscp52.bedrockify.common.features.recipes.DyeHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.FireworkStarRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(FireworkStarRecipe.class)
public class FireworkStarRecipeMixin {

    @Shadow @Final private Map<Item, FireworkExplosion.Shape> shapes;

    @Shadow @Final private Ingredient twinkle;

    @Shadow @Final private Ingredient trail;

    @Shadow @Final private Ingredient fuel;

    @ModifyReturnValue(method = "matches(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/world/level/Level;)Z", at = @At("RETURN"))
    public boolean matches(boolean original, CraftingInput craftingInventory, Level world) {
        if(!Bedrockify.getInstance().settings.isBedrockRecipesEnabled())
            return original;
        boolean bl = false;
        boolean bl2 = false;
        boolean bl3 = false;
        boolean bl4 = false;
        boolean bl5 = false;

        for(int i = 0; i < craftingInventory.size(); ++i) {
            ItemStack itemStack = craftingInventory.getItem(i);
            if (!itemStack.isEmpty()) {
                if (this.shapes.containsKey(itemStack.getItem())) {
                    if (bl3) {
                        return original;
                    }

                    bl3 = true;
                } else if (this.twinkle.test(itemStack)) {
                    if (bl5) {
                        return original;
                    }

                    bl5 = true;
                } else if (this.trail.test(itemStack)) {
                    if (bl4) {
                        return original;
                    }

                    bl4 = true;
                } else if (this.fuel.test(itemStack)) {
                    if (bl) {
                        return original;
                    }

                    bl = true;
                } else {
                    if (!(DyeHelper.isDyeableItem(itemStack.getItem()))) {
                        return original;
                    }

                    bl2 = true;
                }
            }
        }
        return original || (bl && bl2);
    }

    @ModifyReturnValue(method = "assemble(Lnet/minecraft/world/item/crafting/CraftingInput;)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"))
    public ItemStack craft(ItemStack original, CraftingInput craftingInventory) {
        if(!Bedrockify.getInstance().settings.isBedrockRecipesEnabled())
            return original;
        ItemStack itemStack = new ItemStack(Items.FIREWORK_STAR);
        FireworkExplosion.Shape type = FireworkExplosion.Shape.SMALL_BALL;
        IntList list = new IntArrayList();
        boolean hasTwinkleMod = false;
        boolean hasTrailMod = false;

        for(int i = 0; i < craftingInventory.size(); ++i) {
            ItemStack itemStack2 = craftingInventory.getItem(i);
            if (!itemStack2.isEmpty()) {
                if (this.shapes.containsKey(itemStack2.getItem())) {
                    type = this.shapes.get(itemStack2.getItem());
                } else if (this.twinkle.test(itemStack2)) {
                    hasTwinkleMod = true;
                } else if (this.trail.test(itemStack2)) {
                    hasTrailMod = true;
                } else if (DyeHelper.isDyeableItem(itemStack2.getItem())) {
                    DyeHelper.getDyeItem(itemStack2.getItem()).map(dyeItem -> dyeItem.getDefaultInstance().get(DataComponents.DYE)).ifPresent(dyeColor -> {
                        list.add(dyeColor.getFireworkColor());
                    });
                }
            }
        }

        itemStack.set(DataComponents.FIREWORK_EXPLOSION, new FireworkExplosion(type, list, IntList.of(), hasTrailMod, hasTwinkleMod));
        return itemStack;
    }

}
