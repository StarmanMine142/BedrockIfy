package me.juancarloscp52.bedrockify.mixin.client.features.bedrockShading.lightBlock;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractTerrainRenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
/**
 * @author Shaddatic
 */
@Mixin(AbstractTerrainRenderContext.class)
public class AbstractTerrainRenderContextMixin {

    @Shadow @Final protected BlockRenderInfo blockInfo;

    @Redirect(method = "shadeFlatQuad", at=@At(value = "INVOKE",target = "Lnet/minecraft/world/level/BlockAndTintGetter;getShade(Lnet/minecraft/core/Direction;Z)F"))
    private float getBlockShade(BlockAndTintGetter blockRenderView, Direction direction, boolean shaded){

        if(blockInfo.blockState.getLightEmission()>2 && shaded && BedrockifyClient.getInstance().settings.bedrockShading)
            return BedrockifyClient.getInstance().bedrockBlockShading.getBlockShade(direction);
        else
            return blockRenderView.getShade(direction, shaded);
    }

}
