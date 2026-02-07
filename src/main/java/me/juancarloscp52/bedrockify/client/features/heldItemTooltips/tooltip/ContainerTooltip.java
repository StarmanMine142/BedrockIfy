package me.juancarloscp52.bedrockify.client.features.heldItemTooltips.tooltip;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;

public class ContainerTooltip extends Tooltip {
    Component itemName;

    public ContainerTooltip(ItemStack itemStack){
        this.primaryValue = itemStack.getCount();

        Item item = itemStack.getItem();
        if (item instanceof PotionItem potionItem) {
            this.itemName = potionItem.getName(itemStack);
        } else {
            this.itemName = Component.translatable(item.getDescriptionId());
        }
    }

    @Override
    public MutableComponent getTooltipText() {
        MutableComponent tooltip = this.itemName.copy();
        tooltip.append(" x").append(String.valueOf(primaryValue));
        return tooltip;
    }
}
