package me.juancarloscp52.bedrockify.client.features.heldItemTooltips.tooltip;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;

public class PotionTooltip extends Tooltip {

    Component tooltip;

    public PotionTooltip (Component tooltip){
        this.tooltip = tooltip;
    }

    @Override
    public MutableComponent getTooltipText() {
        return (MutableComponent) this.tooltip;
    }
}
