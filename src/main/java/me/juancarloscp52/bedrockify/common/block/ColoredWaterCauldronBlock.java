package me.juancarloscp52.bedrockify.common.block;

import com.mojang.serialization.MapCodec;
import me.juancarloscp52.bedrockify.Bedrockify;
import me.juancarloscp52.bedrockify.common.block.cauldron.BedrockCauldronBehavior;
import me.juancarloscp52.bedrockify.common.features.cauldron.BedrockCauldronProperties;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * Allows to dye using Cauldron.<br>
 * Dye items for which {@link net.minecraft.world.item.DyeItem} is implemented.
 */
public class ColoredWaterCauldronBlock extends AbstractBECauldronBlock {
    public static final MapCodec<ColoredWaterCauldronBlock> CODEC = ColoredWaterCauldronBlock.simpleCodec(ColoredWaterCauldronBlock::new);

    public static final IntegerProperty LEVEL = BedrockCauldronProperties.LEVEL_6;
    public static final int MAX_LEVEL = BedrockCauldronProperties.MAX_LEVEL_6;

    private static final int BOTTLE_LEVEL = MAX_LEVEL / LayeredCauldronBlock.MAX_FILL_LEVEL;

    public ColoredWaterCauldronBlock(Properties settings) {
        super(settings, BedrockCauldronBehavior.COLORED_WATER_CAULDRON_BEHAVIOR);
        this.registerDefaultState(this.getStateDefinition().any().setValue(LEVEL, BOTTLE_LEVEL));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    public boolean isFull(BlockState state) {
        return state.getValue(LEVEL) == MAX_LEVEL;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        return (int) Math.ceil((float) state.getValue(LEVEL) / MAX_LEVEL * LayeredCauldronBlock.MAX_FILL_LEVEL);
    }

    @Override
    protected MapCodec<? extends AbstractCauldronBlock> codec() {
        return CODEC;
    }

    @Override
    public double getContentHeight(BlockState state) {
        return Mth.lerp((float) state.getValue(LEVEL) / MAX_LEVEL, 0.375, 0.9375);
    }

    public static void decrementWhenDye(BlockState state, Level world, BlockPos pos) {
        final int decremented = state.getValue(LEVEL) - 1;
        final BlockState newState = (decremented == 0) ? Blocks.CAULDRON.defaultBlockState() : state.setValue(LEVEL, decremented);
        world.setBlockAndUpdate(pos, newState);
        world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
    }

    public static int getLevelFromWaterCauldronState(BlockState state) {
        if (!state.hasProperty(LayeredCauldronBlock.LEVEL)) {
            Bedrockify.LOGGER.error(
                    "[%s] state conversion failed".formatted(Bedrockify.class.getSimpleName()),
                    new IllegalStateException("BlockState of %s does not have state: %s".formatted(state.getBlock(), LayeredCauldronBlock.LEVEL)));
            return 0;
        }

        return Mth.lerpInt((float) state.getValue(LayeredCauldronBlock.LEVEL) / LayeredCauldronBlock.MAX_FILL_LEVEL, 0, MAX_LEVEL);
    }

    public static int convertToWaterCauldronLevel(BlockState state) {
        if (!state.hasProperty(LEVEL)) {
            Bedrockify.LOGGER.error(
                    "[%s] state conversion failed".formatted(Bedrockify.class.getSimpleName()),
                    new IllegalStateException("BlockState of %s does not have state: %s".formatted(state.getBlock(), LEVEL)));
            return 0;
        }

        return (int) Math.floor((float) state.getValue(LEVEL) / MAX_LEVEL * LayeredCauldronBlock.MAX_FILL_LEVEL);
    }

    public static boolean tryPickFluid(BlockState state, Level world, BlockPos pos) {
        final int nextLevel = state.getValue(LEVEL) - BOTTLE_LEVEL;
        if (nextLevel < 0) {
            return false;
        }
        BlockState blockState = (nextLevel == 0) ? Blocks.CAULDRON.defaultBlockState() : state.setValue(LEVEL, nextLevel);
        world.setBlockAndUpdate(pos, blockState);
        world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState));
        return true;
    }
}
