package me.juancarloscp52.bedrockify.mixin.client.features.hudOpacity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.hudOpacity.HudOpacity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Shadow @Final private MinecraftClient client;
    @Unique
    private HudOpacity hudOpacity;
    
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(MinecraftClient client, CallbackInfo ci){
        hudOpacity = BedrockifyClient.getInstance().hudOpacity;
    }
    
    @WrapOperation(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"))
    public void setShaderColorOpacity(DrawContext context, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        context.drawGuiTexture(pipeline, sprite, x, y, width, height, hudOpacity.getHudOpacity(false));
    }

//    @Inject(method = "renderHotbar", at = @At("RETURN"))
//    public void resetShaderColorOpacity(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci){
////        RenderSystem.setShaderColor(1,1,1,1);
//    }

    @WrapOperation(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIIIIIII)V"))
    public void setAttackIconColorOpacity(DrawContext instance, RenderPipeline pipeline, Identifier sprite, int textureWidth, int textureHeight, int u, int v, int x, int y, int width, int height, Operation<Void> original){
        instance.drawGuiTexture(pipeline, sprite, textureWidth, textureHeight, u, v, x, y, width, height, ColorHelper.getWhite(hudOpacity.getHudOpacity(false)));
    }

    //region Status Bars
    @WrapOperation(method = "drawHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"))
    public void setHealthBarColorOpacity(DrawContext instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        instance.drawGuiTexture(pipeline, sprite, x, y, width, height, ColorHelper.getWhite(hudOpacity.getHudOpacity(false)));
    }

    @WrapOperation(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"))
    private static void setArmorBarColorOpacity(DrawContext instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        instance.drawGuiTexture(pipeline, sprite, x, y, width, height, ColorHelper.getWhite(BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false)));
    }

    @WrapOperation(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"))
    private void setFoodBarColorOpacity(DrawContext instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        instance.drawGuiTexture(pipeline, sprite, x, y, width, height, ColorHelper.getWhite(hudOpacity.getHudOpacity(false)));
    }

    @WrapOperation(method = "renderAirBubbles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"))
    private void setAirBarColorOpacity(DrawContext instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        instance.drawGuiTexture(pipeline, sprite, x, y, width, height, ColorHelper.getWhite(hudOpacity.getHudOpacity(false)));
    }

    @WrapOperation(method = "renderMountHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"))
    private void setMountHealthBarColorOpacity(DrawContext instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        instance.drawGuiTexture(pipeline, sprite, x, y, width, height, ColorHelper.getWhite(hudOpacity.getHudOpacity(false)));
    }

//    @Unique
//    private void drawGuiTexture(DrawContext context, RenderPipeline pipeline, Identifier sprite, int textureWidth, int textureHeight, int u, int v, int x, int y, int width, int height, int color) {
//        GuiAtlasManager guiAtlasManager = this.client.getGuiAtlasManager();
//        Sprite sprite2 = guiAtlasManager.getSprite(sprite);
//        Scaling scaling = guiAtlasManager.getScaling(sprite2);
//        if (scaling instanceof Scaling.Stretch) {
////            context.drawSpriteRegion(renderLayers, sprite2, textureWidth, textureHeight, u, v, x, y, width, height, color);
//        } else {
//            context.enableScissor(x, y, x + width, y + height);
////            context.drawGuiTexture(renderLayers, sprite, x - u, y - v, textureWidth, textureHeight, color);
//            context.disableScissor();
//        }
//    }
    //endregion

    //region Status Effects
    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"))
    public void setStatusEffectOpacityHead(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci){
//        RenderSystem.setShaderColor(1,1,1, hudOpacity.getHudOpacity(false));
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("RETURN"))
    public void setStatusEffectOpacityReturn(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci){
//        RenderSystem.setShaderColor(1,1,1, 1);
    }

    @ModifyConstant(method = "renderStatusEffectOverlay", constant = @Constant(floatValue = 1.f, ordinal = 0))
    public float setOpacityStatusEffectImage (float f){
        return f * hudOpacity.getHudOpacity(false);
    }
    //endregion
}
