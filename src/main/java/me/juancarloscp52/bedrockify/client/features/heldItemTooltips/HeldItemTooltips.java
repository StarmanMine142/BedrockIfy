package me.juancarloscp52.bedrockify.client.features.heldItemTooltips;

import com.google.common.collect.Lists;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.BedrockifyClientSettings;
import me.juancarloscp52.bedrockify.client.features.heldItemTooltips.tooltip.ContainerTooltip;
import me.juancarloscp52.bedrockify.client.features.heldItemTooltips.tooltip.EnchantmentTooltip;
import me.juancarloscp52.bedrockify.client.features.heldItemTooltips.tooltip.PotionTooltip;
import me.juancarloscp52.bedrockify.client.features.heldItemTooltips.tooltip.Tooltip;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HeldItemTooltips {

    private static final int TOOLTIP_SIZE = 6;

    private static final boolean B_DAB_LOADED = FabricLoader.getInstance().isModLoaded("detailab");

    public void drawItemWithCustomTooltips(DrawContext drawContext, TextRenderer fontRenderer, Text text, float x, float y, int color, ItemStack currentStack) {
        final BedrockifyClientSettings settings = BedrockifyClient.getInstance().settings;
        final int screenBorder = settings.getScreenSafeArea();
        int tooltipOffset = 0;

        //Set tooltip position depending on hotbar displayed information
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(null ==player || null==MinecraftClient.getInstance().interactionManager)
            return;
        if(MinecraftClient.getInstance().interactionManager.hasStatusBars()){
            y-=16;
            if(player.getArmor()>0 || (B_DAB_LOADED && PlayerInventory.EQUIPMENT_SLOTS.keySet().intStream().anyMatch(value -> player.getInventory().getStack(value).contains(DataComponentTypes.GLIDER)))){
                y-=10;
            }
            if(player.getAbsorptionAmount()>0){
                y-=10;
            }
        }else if((player.getVehicle()!=null && player.getVehicle() instanceof LivingEntity)){
            y-=16;
        }

        // Draw item tooltips if the option is enabled.
        if(settings.heldItemTooltips) {
            // Get the current held item tooltips and convert to Text.
            final List<Text> tooltips = Lists.newArrayList();
            for (Tooltip tooltip : getTooltips(currentStack)) {
                tooltips.add(tooltip.getTooltipText());
            }
            // Limit the maximum number of shown tooltips to tooltipSize.
            final boolean showMoreTooltip = (tooltips.size() > TOOLTIP_SIZE);
            if (showMoreTooltip) {
                // Store the number of items.
                final int xMore = tooltips.size() - (TOOLTIP_SIZE -1);
                // Trim tooltips.
                tooltips.subList(TOOLTIP_SIZE - 1, tooltips.size()).clear();
                // Add the "and x more..." tooltip.
                tooltips.add(Text.translatable("item.container.more_items", xMore).formatted(Formatting.GRAY));
            }

            tooltipOffset = 12 * tooltips.size();
            //Render background behind tooltip.
            int maxLength = getMaxTooltipLength(tooltips,fontRenderer,currentStack);
            renderBackground(drawContext, y, screenBorder, tooltipOffset, maxLength, color >> 24 & 0xff);


            int i = tooltips.size() - 1;
            for (Text elem : tooltips) {
                // Render the tooltip.
                renderTooltip(drawContext, fontRenderer, y - screenBorder - (12 * i), color, ((MutableText)elem).formatted(Formatting.GRAY));
                --i;
            }
        }

        // Render the item name.
        drawContext.drawTextWithShadow(fontRenderer, text, (int)x, (int)(y - tooltipOffset - screenBorder), color);
    }

    /**
     * Gets a List with the given item tooltips.
     * @param currentStack Current item stack of the player.
     * @return List with the tooltip information.
     */
    public static List<Tooltip> getTooltips(ItemStack currentStack) {
        final Item item = currentStack.getItem();
        final List<Tooltip> result = Lists.newArrayList();
        if (item == Items.ENCHANTED_BOOK || currentStack.hasEnchantments()) {
            var enchantmentsComponent = EnchantmentHelper.getEnchantments(currentStack);
            enchantmentsComponent.getEnchantments().forEach(enchantment -> result.add(new EnchantmentTooltip(enchantment.value(), enchantmentsComponent.getLevel(enchantment))));

        } else if (item instanceof PotionItem || item instanceof TippedArrowItem) {
            result.addAll(generateTooltipsForPotion(currentStack));

        } else if (item == Items.OMINOUS_BOTTLE) {
            var ominousComponent = currentStack.getComponents().get(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER);
            if (ominousComponent != null) {
                List<StatusEffectInstance> list = List.of(new StatusEffectInstance(StatusEffects.BAD_OMEN, 120000, ominousComponent.value(), false, false, true));
                result.addAll(generateTooltipsForPotion(currentStack, list));
            }

        } else if(currentStack.getComponents().contains(DataComponentTypes.CONTAINER)){
            var container = currentStack.get(DataComponentTypes.CONTAINER);
            if(container != null){
                generateTooltipsFromContainer(container.stream().toList(), result);
            }

        } else if (currentStack.getComponents().contains(DataComponentTypes.BUNDLE_CONTENTS)){
            var container = currentStack.getComponents().get(DataComponentTypes.BUNDLE_CONTENTS);
            if(container != null){
                generateTooltipsFromContainer(container.stream().toList(), result);
            }
        }

        return result;
    }

    /**
     * Checks if the tooltips of two items are equal.
     */
    public boolean equals(ItemStack item1, ItemStack item2){
        List<Tooltip> itemTooltips1 = getTooltips(item1);
        List<Tooltip> itemTooltips2 = getTooltips(item2);
        // Overriding Object#equals in the class Tooltip allows the use of utility classes provided by Java.
        return Objects.equals(itemTooltips1, itemTooltips2);
    }

    private static void generateTooltipsFromContainer(List<ItemStack> items, List<Tooltip> instance){
        for(ItemStack item : items){
            if(!item.isEmpty())
                instance.add(new ContainerTooltip(item));
        }
    }

    private static List<PotionTooltip> generateTooltipsForPotion(ItemStack stack, Iterable<StatusEffectInstance> effects){
        List<PotionTooltip> tooltips = new ArrayList<>();
        for (StatusEffectInstance statusEffectInstance : effects) {
            RegistryEntry<StatusEffect> registryEntry = statusEffectInstance.getEffectType();
            int i = statusEffectInstance.getAmplifier();
            MutableText mutableText = PotionContentsComponent.getEffectText(registryEntry, i);
            if (!statusEffectInstance.isDurationBelow(20)) {
                mutableText = Text.translatable("potion.withDuration", mutableText, StatusEffectUtil.getDurationText(statusEffectInstance, stack.getComponents().getOrDefault(DataComponentTypes.POTION_DURATION_SCALE, 1.0F), MinecraftClient.getInstance().world.getTickManager().getTickRate()));
            }

            tooltips.add(new PotionTooltip(mutableText.formatted(registryEntry.value().getCategory().getFormatting())));
        }

        if (tooltips.isEmpty()) {
            tooltips.add(new PotionTooltip(Text.translatable("effect.none")));
        }
        return tooltips;
    }

    private static List<PotionTooltip> generateTooltipsForPotion(ItemStack stack) {
        return generateTooltipsForPotion(stack, stack.get(DataComponentTypes.POTION_CONTENTS).getEffects());
    }

    private void renderBackground(DrawContext drawContext, float y, int screenBorder, int tooltipOffset, int maxLength, int alpha) {
        MinecraftClient client = MinecraftClient.getInstance();
        int background = MathHelper.lerp(alpha / 255f, 0, MathHelper.ceil((255.0D * BedrockifyClient.getInstance().settings.heldItemTooltipBackground))) << 24;
        drawContext.fill(MathHelper.ceil((client.getWindow().getScaledWidth()-maxLength)/2f-3),MathHelper.ceil(y - tooltipOffset -5- screenBorder), MathHelper.ceil((client.getWindow().getScaledWidth()+maxLength)/2f+1),MathHelper.ceil(y - tooltipOffset -4- screenBorder),background);
        drawContext.fill(MathHelper.ceil((client.getWindow().getScaledWidth()-maxLength)/2f-3),MathHelper.ceil(y+12-screenBorder), MathHelper.ceil((client.getWindow().getScaledWidth()+maxLength)/2f+1),MathHelper.ceil(y+13-screenBorder),background);
        drawContext.fill(MathHelper.ceil((client.getWindow().getScaledWidth()-maxLength)/2f-4), MathHelper.ceil(y - tooltipOffset -4- screenBorder),MathHelper.ceil((client.getWindow().getScaledWidth()+maxLength)/2f+2), MathHelper.ceil(y+12-screenBorder),background);
    }

    /**
     * Renders an item tooltip with the given text and height in screen.
     */
    private void renderTooltip(DrawContext drawContext, TextRenderer fontRenderer, float y, int color, Text text) {
        int enchantX = (MinecraftClient.getInstance().getWindow().getScaledWidth() - fontRenderer.getWidth(text)) / 2;
        drawContext.drawTextWithShadow(fontRenderer, text, enchantX, (int)y, color);
    }

    private int getMaxTooltipLength(List<Text> tooltips, TextRenderer textRenderer, ItemStack itemStack){
        int maxLength=textRenderer.getWidth(itemStack.getName());
        for(Text elem : tooltips){
            int tipLength = textRenderer.getWidth(elem);
            if(maxLength<tipLength)
                maxLength=tipLength;
        }
        return maxLength;
    }
}
