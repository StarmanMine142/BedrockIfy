package me.juancarloscp52.bedrockify.common.block.cauldron;

import me.juancarloscp52.bedrockify.Bedrockify;
import me.juancarloscp52.bedrockify.common.block.ColoredWaterCauldronBlock;
import me.juancarloscp52.bedrockify.common.block.PotionCauldronBlock;
import me.juancarloscp52.bedrockify.common.block.entity.WaterCauldronBlockEntity;
import me.juancarloscp52.bedrockify.common.features.cauldron.BedrockCauldronBlocks;
import me.juancarloscp52.bedrockify.common.features.cauldron.ColorBlenderHelper;
import me.juancarloscp52.bedrockify.common.payloads.CauldronParticlePayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;
import net.minecraft.tags.ItemTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Defines the behavior of Bedrock's Cauldron.
 */
public interface BedrockCauldronBehavior {
    CauldronInteraction.InteractionMap POTION_CAULDRON_BEHAVIOR = CauldronInteraction.newInteractionMap("potion");
    CauldronInteraction.InteractionMap COLORED_WATER_CAULDRON_BEHAVIOR = CauldronInteraction.newInteractionMap("dye");

    CauldronInteraction DYE_ITEM_BY_COLORED_WATER = (state, world, pos, player, hand, stack) -> {
        if (state == null || world == null || pos == null || !Bedrockify.getInstance().settings.bedrockCauldron) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (!stack.is(ItemTags.DYEABLE)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        Optional<WaterCauldronBlockEntity> entity = retrieveCauldronEntity(world, pos);
        if (entity.isEmpty()) {
            return emptyCauldronFromWrongState(state, world, pos, player, hand, stack);
        }

        final WaterCauldronBlockEntity blockEntity = entity.get();
        if (!world.isClientSide()) {
            ColoredWaterCauldronBlock.decrementWhenDye(state, world, pos);
            player.awardStat(Stats.USE_CAULDRON);
            player.setItemInHand(hand, ColorBlenderHelper.blendColors(stack, blockEntity.getTintColor()));
            world.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.15f, 1.25f);
            world.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
        }

        return InteractionResult.SUCCESS;
    };

    CauldronInteraction DYE_WATER = (state, world, pos, player, hand, stack) -> {
        if (state == null || world == null || pos == null || !Bedrockify.getInstance().settings.bedrockCauldron) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        Item item = stack.getItem();
        if (!(item instanceof DyeItem dyeItem)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        Optional<WaterCauldronBlockEntity> entity = world.getBlockEntity(pos, BedrockCauldronBlocks.WATER_CAULDRON_ENTITY);
        final int level;
        final int nextColor;
        if (entity.isPresent()) {
            // The Cauldron already has color.
            final int currentColor = entity.get().getTintColor();
            nextColor = ColorBlenderHelper.blendColors(currentColor, ColorBlenderHelper.fromDyeItem(dyeItem));
            if (nextColor == currentColor) {
                return InteractionResult.SUCCESS;
            }
            level = state.getValue(ColoredWaterCauldronBlock.LEVEL);
        } else {
            // Otherwise it may be Water Cauldron.
            nextColor = ColorBlenderHelper.fromDyeItem(dyeItem);
            level = ColoredWaterCauldronBlock.getLevelFromWaterCauldronState(state);
        }

        if (!world.isClientSide()) {
            BlockState newState = BedrockCauldronBlocks.COLORED_WATER_CAULDRON.defaultBlockState()
                    .setValue(ColoredWaterCauldronBlock.LEVEL, level);
            world.setBlockAndUpdate(pos, newState);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.awardStat(Stats.ITEM_USED.get(item));
            world.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS);
            world.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
        }
        world.getBlockEntity(pos, BedrockCauldronBlocks.WATER_CAULDRON_ENTITY).ifPresent(blockEntity -> {
            blockEntity.setDyeColor(nextColor);
        });

        return InteractionResult.SUCCESS;
    };

    /**
     * Increases the water level and replaces the block with the vanilla Water Cauldron.
     */
    CauldronInteraction PLACE_WATER_BY_POTION = (state, world, pos, player, hand, stack) -> {
        if (state == null || world == null || pos == null || !Bedrockify.getInstance().settings.bedrockCauldron) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        var component = stack.get(DataComponents.POTION_CONTENTS);
        if (component != null && !component.is(Potions.WATER)) {
            return InteractionResult.SUCCESS;
        }

        final int nextLevel;
        if (state.is(Blocks.WATER_CAULDRON)) {
            if (state.getValue(LayeredCauldronBlock.LEVEL) >= LayeredCauldronBlock.MAX_FILL_LEVEL) {
                return InteractionResult.SUCCESS;
            }
            nextLevel = state.cycle(LayeredCauldronBlock.LEVEL).getValue(LayeredCauldronBlock.LEVEL);
        } else if (state.is(BedrockCauldronBlocks.COLORED_WATER_CAULDRON)) {
            if (state.getValue(ColoredWaterCauldronBlock.LEVEL) >= ColoredWaterCauldronBlock.MAX_LEVEL) {
                return InteractionResult.SUCCESS;
            }
            nextLevel = Math.min(ColoredWaterCauldronBlock.convertToWaterCauldronLevel(state) + 1, LayeredCauldronBlock.MAX_FILL_LEVEL);
        } else {
            nextLevel = Blocks.WATER_CAULDRON.defaultBlockState().getValue(LayeredCauldronBlock.LEVEL);
        }

        if (!world.isClientSide()) {
            // Replace with Water Cauldron.
            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
            world.setBlockAndUpdate(pos, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, nextLevel));
            world.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS);
            world.gameEvent(null, GameEvent.FLUID_PLACE, pos);
        }

        return InteractionResult.SUCCESS;
    };

    /**
     * Decreases the fluid level and takes out from the Potion Cauldron.
     */
    CauldronInteraction PICK_POTION_FLUID = (state, world, pos, player, hand, stack) -> {
        if (state == null || world == null || pos == null || !Bedrockify.getInstance().settings.bedrockCauldron) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        Optional<WaterCauldronBlockEntity> entity = retrieveCauldronEntity(world, pos);
        if (entity.isEmpty()) {
            return emptyCauldronFromWrongState(state, world, pos, player, hand, stack);
        }

        final WaterCauldronBlockEntity blockEntity = entity.get();
        final Item item = stack.getItem();
        Optional<Potion> optionalPotion = retrievePotion(blockEntity);
        if (optionalPotion.isEmpty()) {
            return emptyCauldronFromWrongState(state, world, pos, player, hand, stack);
        }

        final Potion potion = optionalPotion.get();
        Holder<Potion> potionEntry = BuiltInRegistries.POTION.wrapAsHolder(potion);
        final Item potionType = blockEntity.getPotionType();
        if (!world.isClientSide()) {
            if (!PotionCauldronBlock.tryPickFluid(state, world, pos)) {
                return InteractionResult.SUCCESS_SERVER;
            }
            final ItemStack potionStack = PotionContents.createItemStack(potionType, potionEntry);
            if (!potionStack.has(DataComponents.POTION_CONTENTS)) {
                return InteractionResult.SUCCESS_SERVER;
            }
            final int potionColor = Objects.requireNonNull(potionStack.get(DataComponents.POTION_CONTENTS)).getColor();
            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(item));
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, potionStack));
            world.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS);
            world.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
            addPotionParticle(world.getBlockState(pos), world, pos, potionColor);
        }

        return InteractionResult.SUCCESS;
    };

    /**
     * Replaces Empty Cauldron with Potion Cauldron, or increases its fluid.
     */
    CauldronInteraction PLACE_POTION_FLUID = (state, world, pos, player, hand, stack) -> {
        if (state == null || world == null || pos == null || !Bedrockify.getInstance().settings.bedrockCauldron) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (!(stack.getItem() instanceof PotionItem)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        var component = stack.get(DataComponents.POTION_CONTENTS);
        if (component == null) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        var optionalPotion = component.potion();
        if (optionalPotion.isEmpty()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        var potionEntry = optionalPotion.get();
        if (potionEntry.value().getEffects().isEmpty() && !component.is(Potions.WATER)) {
            // Prevent to place the fluid of Awkward Potion, Mundane Potion, etc.
            return InteractionResult.SUCCESS;
        }

        final BlockState newState;
        final Identifier inStackPotionId = BuiltInRegistries.POTION.getKey(potionEntry.value());
        if (state.hasProperty(PotionCauldronBlock.LEVEL)) {
            // The cauldron already has fluid.
            Optional<WaterCauldronBlockEntity> entity = retrieveCauldronEntity(world, pos);
            if (entity.isEmpty()) {
                return emptyCauldronFromWrongState(state, world, pos, player, hand, stack);
            }

            final Identifier currentPotionId = entity.get().getFluidId();
            // Not the same potion.
            if (!Objects.equals(currentPotionId, inStackPotionId)) {
                return evaporateCauldron(state, world, pos, player, hand, stack, new ItemStack(Items.GLASS_BOTTLE));
            }

            // The cauldron is full.
            final int currentLevel = state.getValue(PotionCauldronBlock.LEVEL);
            if (currentLevel >= PotionCauldronBlock.MAX_LEVEL) {
                return InteractionResult.SUCCESS_SERVER;
            }

            final int nextLevel = Math.min(currentLevel + PotionCauldronBlock.BOTTLE_LEVEL, PotionCauldronBlock.MAX_LEVEL);
            newState = BedrockCauldronBlocks.POTION_CAULDRON.defaultBlockState().setValue(PotionCauldronBlock.LEVEL, nextLevel);
        } else {
            // The cauldron is empty.
            newState = BedrockCauldronBlocks.POTION_CAULDRON.defaultBlockState();
        }

        if (component.is(Potions.WATER)) {
            // Redirect to the behavior of Water Cauldron.
            return PLACE_WATER_BY_POTION.interact(state, world, pos, player, hand, stack);
        }

        final ItemStack processing = stack.copy();
        if (!world.isClientSide()) {
            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
            world.setBlockAndUpdate(pos, newState);
            world.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS);
            world.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            addPotionParticle(newState, world, pos, component.getColor());
        }

        // BlockEntity spawns after replacing the block with BedrockCauldronBlocks#POTION_CAULDRON.
        world.getBlockEntity(pos, BedrockCauldronBlocks.WATER_CAULDRON_ENTITY).ifPresent(blockEntity -> {
            blockEntity.setPotion(processing);
        });

        return InteractionResult.SUCCESS;
    };

    /**
     * Creates tipped arrows.
     */
    CauldronInteraction TIPPED_ARROW_WITH_POTION = (state, world, pos, player, hand, stack) -> {
        if (state == null || world == null || pos == null || !Bedrockify.getInstance().settings.bedrockCauldron) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (!stack.is(Items.ARROW)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        Optional<WaterCauldronBlockEntity> entity = retrieveCauldronEntity(world, pos);
        if (entity.isEmpty()) {
            return emptyCauldronFromWrongState(state, world, pos, player, hand, stack);
        }

        final WaterCauldronBlockEntity blockEntity = entity.get();
        Optional<Potion> optionalPotion = retrievePotion(blockEntity);
        if (optionalPotion.isEmpty()) {
            return emptyCauldronFromWrongState(state, world, pos, player, hand, stack);
        }

        final Potion potion = optionalPotion.get();
        Holder<Potion> potionEntry = BuiltInRegistries.POTION.wrapAsHolder(potion);
        if (!world.isClientSide()) {
            // Determine arrow count and fluid level to decrease.
            final int tippedArrowCount = PotionCauldronBlock.getMaxTippedArrowCount(stack, state);
            if (tippedArrowCount <= 0) {
                return InteractionResult.SUCCESS_SERVER;
            }
            final int consumedFluidLevel = PotionCauldronBlock.getDecLevelByStack(stack, tippedArrowCount);
            final int afterFluidLevel = state.getValue(PotionCauldronBlock.LEVEL) - consumedFluidLevel;

            // Create tipped arrows.
            final ItemStack result = new ItemStack(Items.TIPPED_ARROW, tippedArrowCount);
            final ItemStack lingeringEffect = PotionContents.createItemStack(Items.LINGERING_POTION, potionEntry);
            result.set(DataComponents.POTION_CONTENTS, lingeringEffect.get(DataComponents.POTION_CONTENTS));

            if (player.isCreative()) {
                if (!player.getInventory().contains(result)) {
                    player.getInventory().add(result);
                }

                player.setItemInHand(hand, stack);
            } else {
                // Exchange an item.
                stack.shrink(tippedArrowCount);

                if (stack.isEmpty()) {
                    player.setItemInHand(hand, result);
                } else {
                    if (!player.getInventory().add(result)) {
                        player.drop(result, false);
                    }
                    player.setItemInHand(hand, stack);
                }
            }

            // Consume the fluid.
            if (afterFluidLevel <= 1) {
                world.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
            } else {
                world.setBlockAndUpdate(pos, BedrockCauldronBlocks.POTION_CAULDRON.defaultBlockState().setValue(PotionCauldronBlock.LEVEL, afterFluidLevel));
            }
            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(Items.ARROW), tippedArrowCount);
            world.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.15f, 1.25f);
            world.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
        }

        return InteractionResult.SUCCESS;
    };

    CauldronInteraction PICK_COLORED_WATER = (state, world, pos, player, hand, stack) -> {
        if (!world.isClientSide()) {
            if (!ColoredWaterCauldronBlock.tryPickFluid(state, world, pos)) {
                return InteractionResult.SUCCESS_SERVER;
            }
            Item item = stack.getItem();
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, PotionContents.createItemStack(Items.POTION, Potions.WATER)));
            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(item));
            world.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS);
            world.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
        }

        return InteractionResult.SUCCESS;
    };

    CauldronInteraction FILL_BUCKET_WITH_COLORED_WATER = (state, world, pos, player, hand, stack) -> {
        return CauldronInteraction.fillBucket(state, world, pos, player, hand, stack, new ItemStack(Items.WATER_BUCKET), (statex) -> {
            return statex.getValue(ColoredWaterCauldronBlock.LEVEL) == ColoredWaterCauldronBlock.MAX_LEVEL;
        }, SoundEvents.BUCKET_FILL);
    };

    /**
     * Helper method that checks and retrieves the {@link WaterCauldronBlockEntity}.<br>
     * There are no Entity, the error log will appear.
     *
     * @return {@link Optional} of WaterCauldronBlockEntity.
     */
    static Optional<WaterCauldronBlockEntity> retrieveCauldronEntity(Level world, BlockPos pos) {
        Optional<WaterCauldronBlockEntity> entity = world.getBlockEntity(pos, BedrockCauldronBlocks.WATER_CAULDRON_ENTITY);
        if (entity.isEmpty()) {
            // Increasing the fluid requires to retrieve the potion.
            Bedrockify.LOGGER.error(
                    "[{}] something went wrong to get the fluid in cauldron",
                    Bedrockify.class.getSimpleName(),
                    new NullPointerException("RegistryWorldView#getBlockEntity is not present at " + pos));
            return Optional.empty();
        }
        return entity;
    }

    /**
     * Helper method that checks exact potion fluid and returns {@link Potion}.
     *
     * @return {@link Optional} of Potion.
     */
    static Optional<Potion> retrievePotion(WaterCauldronBlockEntity blockEntity) {
        final Identifier potionId = blockEntity.getFluidId();
        final Potion potion = BuiltInRegistries.POTION.getValue(potionId);
        if (!BuiltInRegistries.POTION.containsKey(potionId) || potion == null) {
            Bedrockify.LOGGER.error(
                    "[{}] something went wrong to get the potion from Registries",
                    Bedrockify.class.getSimpleName(),
                    new IllegalStateException("potion has disappeared, maybe the mod is gone?"));
            return Optional.empty();
        }
        return Optional.of(potion);
    }

    static InteractionResult emptyCauldronFromWrongState(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        return evaporateCauldron(state, world, pos, player, hand, stack, stack.copy());
    }

    static void registerEvaporateBucketBehavior(Map<Item, CauldronInteraction> map) {
        map.put(Items.WATER_BUCKET, (state, world, pos, player, hand, stack) -> {
            return evaporateCauldron(state, world, pos, player, hand, stack, new ItemStack(Items.BUCKET));
        });
        map.put(Items.LAVA_BUCKET, (state, world, pos, player, hand, stack) -> {
            return evaporateCauldron(state, world, pos, player, hand, stack, new ItemStack(Items.BUCKET));
        });
        map.put(Items.POWDER_SNOW_BUCKET, (state, world, pos, player, hand, stack) -> {
            return evaporateCauldron(state, world, pos, player, hand, stack, new ItemStack(Items.BUCKET));
        });
    }

    /**
     * Empties the cauldron by spawning particles and sound.
     */
    static InteractionResult evaporateCauldron(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack current, ItemStack after) {
        if (!world.isClientSide()) {
            final Identifier particleId = BuiltInRegistries.PARTICLE_TYPE.getKey(ParticleTypes.POOF);
            for (int i = 0; i < 10; ++i) {
                CauldronParticlePayload particlePayload = new CauldronParticlePayload();
                particlePayload.setParticleType(particleId);
                particlePayload.setPosition(new Vec3(pos.getX() + 0.25 + Math.random() * 0.5, pos.getY() + 0.35, pos.getZ() + 0.25 + Math.random() * 0.5));
                particlePayload.setVelocity(new Vec3(Math.random() * 0.075, 0, Math.random() * 0.075));

                PlayerLookup.level((ServerLevel) world).forEach(serverPlayerEntity -> ServerPlayNetworking.send(serverPlayerEntity, particlePayload));
            }
        }
        return CauldronInteraction.fillBucket(state, world, pos, player, hand, current, after, statex -> true, SoundEvents.FIRE_EXTINGUISH);
    }

    /**
     * Spawns particles after interacting with a potion.
     */
    static void addPotionParticle(BlockState state, Level world, BlockPos pos, int color) {
        if (world.isClientSide()) {
            return;
        }

        final double offsetY;
        if (state.getBlock() instanceof PotionCauldronBlock potionCauldronBlock) {
            offsetY = 0.05 + potionCauldronBlock.getContentHeight(state);
        } else {
            offsetY = 0.5;
        }
        final double red = ((color >> 16) & 0xff) / 255.;
        final double green = ((color >> 8) & 0xff) / 255.;
        final double blue = (color & 0xff) / 255.;
        final Identifier particleId = BuiltInRegistries.PARTICLE_TYPE.getKey(ParticleTypes.ENTITY_EFFECT);
        for (int i = 0; i < 7; ++i) {
            CauldronParticlePayload particlePayload = new CauldronParticlePayload();
            particlePayload.setParticleType(particleId);
            particlePayload.setPosition(new Vec3(pos.getX() + 0.15 + Math.random() * 0.7, pos.getY() + offsetY, pos.getZ() + 0.15 + Math.random() * 0.7));
            particlePayload.setVelocity(new Vec3(red, green, blue));

            PlayerLookup.level((ServerLevel) world).forEach(serverPlayerEntity -> ServerPlayNetworking.send(serverPlayerEntity, particlePayload));
        }
    }

    /**
     * Registers the behavior of Bedrock's cauldron from all items and potions.<br>
     * This method needs to be executed after all the registries are ready.
     */
    static void registerBehavior() {
        final var dyeableBehaviorMap = COLORED_WATER_CAULDRON_BEHAVIOR.map();
        final var potionBehaviorMap = POTION_CAULDRON_BEHAVIOR.map();
        final var vanillaWaterBehaviorMap = CauldronInteraction.WATER.map();
        final var vanillaEmptyBehaviorMap = CauldronInteraction.EMPTY.map();

        // Clear all modded maps and get ready to enter the world.
        BuiltInRegistries.ITEM.stream().filter(item -> item instanceof DyeItem).forEach(vanillaWaterBehaviorMap::remove);
        BuiltInRegistries.ITEM.stream().filter(item -> item instanceof PotionItem).forEach(potionItem -> {
            vanillaEmptyBehaviorMap.remove(potionItem);
            vanillaWaterBehaviorMap.remove(potionItem);
        });
        dyeableBehaviorMap.clear();
        potionBehaviorMap.clear();

        // Behavior of the dye item for water cauldron.
        BuiltInRegistries.ITEM.stream().filter(item -> item instanceof DyeItem).forEach(dyeItem -> {
            vanillaWaterBehaviorMap.putIfAbsent(dyeItem, DYE_WATER);
            dyeableBehaviorMap.putIfAbsent(dyeItem, DYE_WATER);
        });

        // Behavior of the colored cauldron.
        BuiltInRegistries.ITEM.stream().filter(item -> item.getDefaultInstance().is(ItemTags.DYEABLE)).forEach(item -> {
            dyeableBehaviorMap.putIfAbsent(item, DYE_ITEM_BY_COLORED_WATER);
        });
        dyeableBehaviorMap.putIfAbsent(Items.BUCKET, FILL_BUCKET_WITH_COLORED_WATER);
        dyeableBehaviorMap.putIfAbsent(Items.GLASS_BOTTLE, PICK_COLORED_WATER);
        CauldronInteraction.addDefaultInteractions(dyeableBehaviorMap);

        // Behavior of the potion.
        BuiltInRegistries.ITEM.stream().filter(item -> item instanceof PotionItem).forEach(potionItem -> {
            vanillaEmptyBehaviorMap.putIfAbsent(potionItem, PLACE_POTION_FLUID);
            potionBehaviorMap.putIfAbsent(potionItem, PLACE_POTION_FLUID);
            // Allows to accept any potion type for the Water Cauldron.
            vanillaWaterBehaviorMap.putIfAbsent(potionItem, PLACE_WATER_BY_POTION);
            dyeableBehaviorMap.putIfAbsent(potionItem, PLACE_WATER_BY_POTION);
        });
        potionBehaviorMap.putIfAbsent(Items.GLASS_BOTTLE, PICK_POTION_FLUID);
        potionBehaviorMap.putIfAbsent(Items.ARROW, TIPPED_ARROW_WITH_POTION);
        registerEvaporateBucketBehavior(potionBehaviorMap);
    }
}
