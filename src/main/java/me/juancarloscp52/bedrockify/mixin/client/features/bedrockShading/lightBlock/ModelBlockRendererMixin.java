package me.juancarloscp52.bedrockify.mixin.client.features.bedrockShading.lightBlock;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
/**
 * @author Shaddatic
 */
@Mixin(ModelBlockRenderer.class)
public class ModelBlockRendererMixin {
//    private boolean luminant = false;
//    @Inject(method = "tesselateFlat",at=@At(value = "INVOKE",target = "Lnet/minecraft/world/level/BlockAndTintGetter;getShade(Lnet/minecraft/core/Direction;Z)F",ordinal = 0))
//    private void getLuminant(BlockAndTintGetter world, BlockState state, BlockPos pos, int light, int overlay, boolean useWorldLight, PoseStack matrices, VertexConsumer vertexConsumer, List<BakedQuad> quads, ModelBlockRenderer.CommonRenderStorage lightmap, CallbackInfo ci){
//        this.luminant = state.getLightEmission() > 2;
//    }
//
//    @Redirect(method = "tesselateFlat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/BlockAndTintGetter;getShade(Lnet/minecraft/core/Direction;Z)F"))
//    private float getBlockShade(BlockAndTintGetter blockRenderView, Direction direction, boolean shaded){
//        if(luminant && shaded && BedrockifyClient.getInstance().settings.bedrockShading)
//            return BedrockifyClient.getInstance().bedrockBlockShading.getBlockShade(direction);
//        else
//            return blockRenderView.getShade(direction, shaded);
//    }
}
