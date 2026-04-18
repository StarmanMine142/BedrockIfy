package me.juancarloscp52.bedrockify.common.features.recipes;

import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Optional;

public class DyeHelper {

    public static boolean isDyeableItem(Item item) {
        return item instanceof DyeItem || item instanceof BoneMealItem || item.equals(Items.COCOA_BEANS) || item.equals(Items.LAPIS_LAZULI) || item.equals(Items.INK_SAC);
    }

    public static Optional<DyeItem> getDyeItem(Item item) {
        if (item instanceof DyeItem)
            return Optional.of((DyeItem) item);
        if (item instanceof BoneMealItem)
            return Optional.of((DyeItem) Items.WHITE_DYE);
        if (item.equals(Items.INK_SAC))
            return Optional.of((DyeItem) Items.BLACK_DYE);
        if (item.equals(Items.LAPIS_LAZULI))
            return Optional.of((DyeItem) Items.BLUE_DYE);
        if (item.equals(Items.COCOA_BEANS))
            return Optional.of((DyeItem) Items.BROWN_DYE);
        return Optional.empty();
    }

}
