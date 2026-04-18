package me.juancarloscp52.bedrockify.mixin.common.features.fertilizableBlocks;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(FlowerBlock.class)
public class FlowerBlockMixin implements BonemealableBlock {
    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        return !state.is(Blocks.WITHER_ROSE);
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        int amount = random.nextIntBetweenInclusive(1,5);
        System.out.println("HEY");
        for(int i = 0; i<amount;i++){
            int x = random.nextIntBetweenInclusive(-3,3);
            int z = random.nextIntBetweenInclusive(-3,3);
            BlockPos newPos = pos.offset(x,0,z);
            if(world.getBlockState(newPos).isAir() && world.getBlockState(newPos.below()).is(BlockTags.SUPPORTS_VEGETATION)){
                world.setBlockAndUpdate(newPos, state.getBlock().defaultBlockState());
            }
        }
    }
}
