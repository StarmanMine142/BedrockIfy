package me.juancarloscp52.bedrockify.mixin.common.features.fertilizableBlocks;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SugarCaneBlock.class)
public class SugarCaneBlockMixin implements BonemealableBlock {
    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        int below = countSugarCaneBelow(world,pos);
        int above = countSugarCaneAbove(world,pos);
        return (below+above)<2;
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        int below = countSugarCaneBelow(world,pos);
        if(below<=1){
            for(int i = 1; i<=2-below; i++){
                BlockPos newPos = pos.above(i);
                if(world.getBlockState(newPos).isAir()){
                    world.setBlockAndUpdate(newPos, state.setValue(BlockStateProperties.AGE_15, 0));
                }
            }
        }
    }

    @Unique
    protected int countSugarCaneAbove(BlockGetter world, BlockPos pos) {
        int i;
        for (i = 0; i < 16 && world.getBlockState(pos.above(i + 1)).is(Blocks.SUGAR_CANE); ++i) { }
        return i;
    }

    @Unique
    protected int countSugarCaneBelow(BlockGetter world, BlockPos pos) {
        int i;
        for (i = 0; i < 16 && world.getBlockState(pos.below(i + 1)).is(Blocks.SUGAR_CANE); ++i) { }
        return i;
    }
}
