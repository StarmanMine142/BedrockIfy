package me.juancarloscp52.bedrockify.mixin.client.features.hudOpacity;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiGraphicsExtractor.class)
public interface GuiGraphicsAccessor {
    @Invoker("itemBar")
    void invokeItemBar(ItemStack itemStack, int x, int y);

    @Invoker("itemCooldown")
    void invokeItemCooldown(ItemStack itemStack, int x, int y);
}
