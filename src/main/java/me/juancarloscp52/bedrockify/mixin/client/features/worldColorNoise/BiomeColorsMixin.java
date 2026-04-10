package me.juancarloscp52.bedrockify.mixin.client.features.worldColorNoise;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeColors.class)
public class BiomeColorsMixin {

    @Inject(method = "getAverageGrassColor", at=@At("RETURN"), cancellable = true)
    private static void getGrassColorWithNoise(BlockAndTintGetter world, BlockPos pos, CallbackInfoReturnable<Integer> info){
        info.setReturnValue(BedrockifyClient.getInstance().worldColorNoiseSampler.applyNoise(pos,info.getReturnValue(),15f,0.06f));
    }

    @Inject(method = "getAverageWaterColor", at=@At("RETURN"), cancellable = true)
    private static void getWaterColorWithNoise(BlockAndTintGetter world, BlockPos pos, CallbackInfoReturnable<Integer> info){
        info.setReturnValue(BedrockifyClient.getInstance().worldColorNoiseSampler.applyNoise(pos,info.getReturnValue(),15f,0.06f));
    }
}
