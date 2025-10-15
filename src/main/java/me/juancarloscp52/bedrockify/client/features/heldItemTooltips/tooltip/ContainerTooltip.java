package me.juancarloscp52.bedrockify.client.features.heldItemTooltips.tooltip;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ContainerTooltip extends Tooltip {
    Text itemName;

    public ContainerTooltip(ItemStack itemStack){
        this.primaryValue = itemStack.getCount();

        Item item = itemStack.getItem();
        if (item instanceof PotionItem potionItem) {
            this.itemName = potionItem.getName(itemStack);
        } else {
            this.itemName = Text.translatable(item.getTranslationKey());
        }
    }

    @Override
    public MutableText getTooltipText() {
        MutableText tooltip = this.itemName.copy();
        tooltip.append(" x").append(String.valueOf(primaryValue));
        return tooltip;
    }
}
