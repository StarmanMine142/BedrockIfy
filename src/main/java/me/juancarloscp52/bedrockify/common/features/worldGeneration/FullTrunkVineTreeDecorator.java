package me.juancarloscp52.bedrockify.common.features.worldGeneration;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator.Context;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class FullTrunkVineTreeDecorator extends TreeDecorator {
    public static final FullTrunkVineTreeDecorator INSTANCE = new FullTrunkVineTreeDecorator();

    public static final MapCodec<FullTrunkVineTreeDecorator> CODEC = MapCodec.unit(() -> INSTANCE);

    protected TreeDecoratorType<?> type() {
        return DyingTrees.VINE_DECORATOR;
    }

    @Override
    public void place(Context generator) {
        generator.logs().forEach((pos) -> setVines(generator.level(),generator,pos));
    }



    public void setVines(LevelSimulatedReader world, Context generator, BlockPos trunkPos){
        for(Direction direction: Direction.Plane.HORIZONTAL){
            setVineOnTrunk(world,generator,trunkPos,direction);
        }
    }

    public void setVineOnTrunk(LevelSimulatedReader world, Context generator, BlockPos trunkPos, Direction direction){
        BlockPos vinePos = trunkPos.relative(direction.getOpposite());
        if (world.isStateAtPosition(vinePos, BlockBehaviour.BlockStateBase::isAir))
            generator.setBlock(vinePos, Blocks.VINE.defaultBlockState().setValue(VineBlock.getPropertyForFace(direction),true));
    }
}