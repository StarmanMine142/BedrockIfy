package me.juancarloscp52.bedrockify.client.features.heldItemTooltips.tooltip;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;

public class EnchantmentTooltip extends Tooltip {
    boolean showLevels=true;
    MutableComponent text;

    public EnchantmentTooltip(Enchantment enchantment, int level){
        this.text = enchantment.description().copy();
        this.primaryValue = level;
        if(enchantment.getMaxLevel()==1)
            showLevels=false;
    }

    @Override
    public MutableComponent getTooltipText(){
        MutableComponent tooltip =text;
        if(showLevels)
            tooltip.append(" ").append(Component.translatable("enchantment.level." + primaryValue));
        return tooltip;
    }
}
