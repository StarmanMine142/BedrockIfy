package me.juancarloscp52.bedrockify.mixin.client.features.hudOpacity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.hudOpacity.HudOpacity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InGameHud.class, priority = 500)
public class InGameHudMixin {

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
    //endregion

    //region Status Effects
    @WrapOperation(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"))
    public void setStatusEffectOpacityHead(DrawContext instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        instance.drawGuiTexture(pipeline, sprite, x, y, width, height, ColorHelper.getWhite(hudOpacity.getHudOpacity(false)));
    }

    @ModifyConstant(method = "renderStatusEffectOverlay", constant = @Constant(floatValue = 1.f, ordinal = 0))
    public float setOpacityStatusEffectImage (float f){
        return f * hudOpacity.getHudOpacity(false);
    }
    //endregion
}
