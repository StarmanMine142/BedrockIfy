package me.juancarloscp52.bedrockify.mixin.common.features.worldGeneration;


import net.minecraft.world.level.block.Block;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TreeFeatures.class)
public interface TreeFeaturesInvoker {

    @Invoker("createStraightBlobTree")
    static TreeConfiguration.TreeConfigurationBuilder invokeCreateStraightBlobTree(Block log, Block leaves, int baseHeight, int firstRandomHeight, int secondRandomHeight, int radius){
        throw new AssertionError();
    }

}
