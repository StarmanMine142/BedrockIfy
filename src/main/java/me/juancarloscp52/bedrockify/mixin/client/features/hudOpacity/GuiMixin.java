package me.juancarloscp52.bedrockify.mixin.client.features.hudOpacity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.hudOpacity.HudOpacity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Gui.class, priority = 500)
public class GuiMixin {

    @Unique
    private HudOpacity hudOpacity;
    
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(Minecraft client, CallbackInfo ci){
        hudOpacity = BedrockifyClient.getInstance().hudOpacity;
    }
    
    @WrapOperation(method = "extractItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    public void setShaderColorOpacity(GuiGraphicsExtractor context, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        context.blitSprite(pipeline, sprite, x, y, width, height, hudOpacity.getHudOpacity(false));
    }

    @WrapOperation(method = "extractItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIII)V"))
    public void setAttackIconColorOpacity(GuiGraphicsExtractor instance, RenderPipeline pipeline, Identifier sprite, int textureWidth, int textureHeight, int u, int v, int x, int y, int width, int height, Operation<Void> original){
        instance.blitSprite(pipeline, sprite, textureWidth, textureHeight, u, v, x, y, width, height, ARGB.white(hudOpacity.getHudOpacity(false)));
    }

    //region Status Bars
    @WrapOperation(method = "extractHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    public void setHealthBarColorOpacity(GuiGraphicsExtractor instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        instance.blitSprite(pipeline, sprite, x, y, width, height, ARGB.white(hudOpacity.getHudOpacity(false)));
    }

    @WrapOperation(method = "extractArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private static void setArmorBarColorOpacity(GuiGraphicsExtractor instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        instance.blitSprite(pipeline, sprite, x, y, width, height, ARGB.white(BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false)));
    }

    @WrapOperation(method = "extractFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private void setFoodBarColorOpacity(GuiGraphicsExtractor instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        instance.blitSprite(pipeline, sprite, x, y, width, height, ARGB.white(hudOpacity.getHudOpacity(false)));
    }

    @WrapOperation(method = "extractAirBubbles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private void setAirBarColorOpacity(GuiGraphicsExtractor instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        instance.blitSprite(pipeline, sprite, x, y, width, height, ARGB.white(hudOpacity.getHudOpacity(false)));
    }

    @WrapOperation(method = "extractVehicleHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private void setMountHealthBarColorOpacity(GuiGraphicsExtractor instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        instance.blitSprite(pipeline, sprite, x, y, width, height, ARGB.white(hudOpacity.getHudOpacity(false)));
    }
    //endregion

    //region Status Effects
    @WrapOperation(method = "extractEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    public void setStatusEffectOpacityHead(GuiGraphicsExtractor instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        instance.blitSprite(pipeline, sprite, x, y, width, height, ARGB.white(hudOpacity.getHudOpacity(false)));
    }

    @ModifyConstant(method = "extractEffects", constant = @Constant(floatValue = 1.f, ordinal = 0))
    public float setOpacityStatusEffectImage (float f){
        return f * hudOpacity.getHudOpacity(false);
    }
    //endregion
}
