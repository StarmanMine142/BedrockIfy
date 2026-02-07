package me.juancarloscp52.bedrockify.mixin.client.features.bedrockShading.lightBlock;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/**
 * @author Shaddatic
 */
@Mixin(LiquidBlockRenderer.class)
public class LiquidBlockRendererMixin {
    private boolean isLuminous;

    @Inject(method = "tesselate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/BlockAndTintGetter;getShade(Lnet/minecraft/core/Direction;Z)F", ordinal = 0))
    private void getFluidType(BlockAndTintGetter world, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState, CallbackInfo ci) {
        this.isLuminous = 0 < world.getLightEmission(pos); //state.isIn(FluidTags.LAVA);
    }

    @Redirect(method = "tesselate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/BlockAndTintGetter;getShade(Lnet/minecraft/core/Direction;Z)F"))
    private float getLavaShade(BlockAndTintGetter blockRenderView, Direction direction, boolean shaded) {
        if(!BedrockifyClient.getInstance().settings.bedrockShading)
            return blockRenderView.getShade(direction,shaded);

        return BedrockifyClient.getInstance().bedrockBlockShading.getLiquidShade(direction,isLuminous);
    }
}
