package me.juancarloscp52.bedrockify.common.block.entity;

import me.juancarloscp52.bedrockify.Bedrockify;
import me.juancarloscp52.bedrockify.common.block.ColoredWaterCauldronBlock;
import me.juancarloscp52.bedrockify.common.features.cauldron.BedrockCauldronBlocks;
import me.juancarloscp52.bedrockify.common.features.cauldron.ColorBlenderHelper;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Allows to keep Dyes and Potions.
 */
public class WaterCauldronBlockEntity extends BlockEntity {
    public static final String KEY_FLUID_TINT = "tint_color";
    public static final String KEY_FLUID_ITEM = "item_id";
    public static final String KEY_POTION_TYPE = "potion_type";

    private static final int COLOR_WHEN_ERROR = 0xff000000;

    private int tintColor;
    private Identifier fluidId;
    private Identifier potionTypeId;

    public WaterCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(BedrockCauldronBlocks.WATER_CAULDRON_ENTITY, pos, state);
    }

    public int getTintColor() {
        return this.tintColor;
    }

    @Nullable
    public Identifier getFluidId() {
        return this.fluidId;
    }

    public Item getPotionType() {
        Item item = BuiltInRegistries.ITEM.getValue(this.potionTypeId);
        if (Objects.equals(BuiltInRegistries.ITEM.getKey(item), BuiltInRegistries.ITEM.getDefaultKey())) {
            item = Items.POTION;
        }
        return item;
    }

    public void setPotion(ItemStack potionItem) {
        final var component = potionItem.get(DataComponents.POTION_CONTENTS);
        if (component == null) {
            return;
        }
        var optionalPotion = component.potion();
        if (optionalPotion.isEmpty()) {
            return;
        }
        var potion = optionalPotion.get();
        this.potionTypeId = BuiltInRegistries.ITEM.getKey(potionItem.getItem());
        this.fluidId = BuiltInRegistries.POTION.getKey(potion.value());
        this.setTintColor(component.getColor());
    }

    public void setDyeColor(int itemColor) {
        final int resultColor;
        if (Objects.equals(this.fluidId, BuiltInRegistries.BLOCK.getKey(BedrockCauldronBlocks.COLORED_WATER_CAULDRON))) {
            resultColor = ColorBlenderHelper.blendColors(this.getTintColor(), itemColor);
        } else {
            this.fluidId = BuiltInRegistries.BLOCK.getKey(BedrockCauldronBlocks.COLORED_WATER_CAULDRON);
            resultColor = itemColor;
        }
        this.setTintColor(resultColor);
    }

    private void setTintColor(int tintColor) {
        this.tintColor = tintColor;
        this.setChanged();
        this.updateListeners();
    }

    /**
     * Defines the validity of the ID used for {@link WaterCauldronBlockEntity#fluidId}.
     */
    private void checkExactIds() {
        boolean valid = false;
        // These branches could be simpler, but please do not simplify them.
        // Reason: To maintain forward compatibility from Bedrockify v1.7
        // Check commit e37f57564d736d455e4a06dcdce259ea0be377de
        if (BuiltInRegistries.ITEM.getValue(this.getFluidId()) instanceof DyeItem dyeItem) {
            this.setDyeColor(Objects.requireNonNull(new ItemStack(dyeItem).get(DataComponents.DYE)).getTextureDiffuseColor());
            valid = true;
        } else if (BuiltInRegistries.BLOCK.getValue(this.getFluidId()) instanceof ColoredWaterCauldronBlock) {
            valid = true;
        } else {
            var potionEntry = BuiltInRegistries.POTION.get(this.getFluidId());
            if (potionEntry.isPresent()) {
                valid = true;
                this.setTintColor(Objects.requireNonNull(PotionContents.createItemStack(Items.GLASS_BOTTLE, potionEntry.get()).get(DataComponents.POTION_CONTENTS)).getColor());
            }
        }
        if (!valid) {
            this.setTintColor(COLOR_WHEN_ERROR);
        }
    }

    private void updateListeners() {
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Nullable
    private static Identifier getIdFromDataView(ValueInput view, String key) {
        try {
            final String id = view.getStringOr(key, "");
            return id.isEmpty() ? null : Identifier.tryParse(id);
        } catch (Exception ex) {
            Bedrockify.LOGGER.error("getIdFromDataView(): Error when parsing identifier: key = {}", key);
        }
        return null;
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        this.tintColor = view.getIntOr(KEY_FLUID_TINT, COLOR_WHEN_ERROR);
        this.fluidId = getIdFromDataView(view, KEY_FLUID_ITEM);
        this.potionTypeId = getIdFromDataView(view, KEY_POTION_TYPE);
        this.checkExactIds();
        this.updateListeners();
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        view.putInt(KEY_FLUID_TINT, this.tintColor);
        view.putString(KEY_FLUID_ITEM, (this.fluidId == null) ? "<NULL>" : this.fluidId.toString());
        view.putString(KEY_POTION_TYPE, (this.potionTypeId == null) ? "<NULL>" : this.potionTypeId.toString());

        super.saveAdditional(view);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return saveWithoutMetadata(registryLookup);
    }
}
