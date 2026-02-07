package me.juancarloscp52.bedrockify.common.features.fireAspectLight;

import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;

public final class FireAspectLightHelper {
    public static boolean canLitWith(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        if (itemStack.isEmpty()) {
            return false;
        }

        return EnchantmentHelper.getEnchantmentsForCrafting(itemStack).keySet().stream().anyMatch(e -> e.is(Enchantments.FIRE_ASPECT.identifier()));
    }
}
