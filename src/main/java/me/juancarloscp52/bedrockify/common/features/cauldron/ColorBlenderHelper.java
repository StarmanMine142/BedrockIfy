package me.juancarloscp52.bedrockify.common.features.cauldron;

import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.ItemTags;

import java.util.Arrays;
import java.util.List;

/**
 * Used by {@link me.juancarloscp52.bedrockify.common.block.ColoredWaterCauldronBlock}.
 */
public final class ColorBlenderHelper {
    private ColorBlenderHelper() {
    }

    /**
     * The mostly same as {@link DyedItemColor#applyDyes(ItemStack, List)}.<br>
     * Blend the color, and set it as the {@link ItemStack} color.
     *
     * @param base   The base stack of DyeableItem.
     * @param colors Target colors to mix.
     * @return Blended item stack.
     */
    public static ItemStack blendColors(ItemStack base, int... colors) {
        //TODO: Previously Dyeable item tag, was changed to CAULDRON_CAN_REMOVE_DYE, for now it has the same items, probably for the future we should register our own item tag.
        if (!base.is(ItemTags.CAULDRON_CAN_REMOVE_DYE)) {
            return base;
        }
        DyedItemColor dyedColorComponent = base.get(DataComponents.DYED_COLOR);

        final int[] blendArray;
        if (dyedColorComponent != null) {
            blendArray = Arrays.copyOf(colors, colors.length + 1);
            blendArray[blendArray.length - 1] = dyedColorComponent.rgb();
        } else {
            blendArray = colors;
        }

        base.set(DataComponents.DYED_COLOR, new DyedItemColor(blendColors(blendArray)));
        return base;
    }

    /**
     * This logic is based on {@link DyedItemColor#applyDyes(ItemStack, List)}.
     *
     * @param blender Target colors to mix.
     * @return The blended color.
     */
    public static int blendColors(int... blender) {
        int peekComponent = 0;
        int count = 0;
        int[] blended = new int[3];

        for (int color : blender) {
            final int red = color >> 16 & 255;
            final int green = color >> 8 & 255;
            final int blue = color & 255;
            peekComponent += Math.max(red, Math.max(green, blue));
            blended[0] += red;
            blended[1] += green;
            blended[2] += blue;
            ++count;
        }

        final int normalizedRed = blended[0] / count;
        final int normalizedGreen = blended[1] / count;
        final int normalizedBlue = blended[2] / count;
        final float peekMul = (float) peekComponent / count;
        int peek = Math.max(normalizedRed, Math.max(normalizedGreen, normalizedBlue));
        final int resultRed = (int) (normalizedRed * peekMul / peek);
        final int resultGreen = (int) (normalizedGreen * peekMul / peek);
        final int resultBlue = (int) (normalizedBlue * peekMul / peek);

        return ARGB.color(resultRed, resultGreen, resultBlue);
    }

}
