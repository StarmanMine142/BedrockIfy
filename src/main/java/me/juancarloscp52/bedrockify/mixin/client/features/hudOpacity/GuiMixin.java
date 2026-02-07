package me.juancarloscp52.bedrockify.mixin.client.features.hudOpacity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.hudOpacity.HudOpacity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Gui;
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
    
    @WrapOperation(method = "renderItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    public void setShaderColorOpacity(GuiGraphics context, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        context.blitSprite(pipeline, sprite, x, y, width, height, hudOpacity.getHudOpacity(false));
    }

    @WrapOperation(method = "renderItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIII)V"))
    public void setAttackIconColorOpacity(GuiGraphics instance, RenderPipeline pipeline, Identifier sprite, int textureWidth, int textureHeight, int u, int v, int x, int y, int width, int height, Operation<Void> original){
        instance.blitSprite(pipeline, sprite, textureWidth, textureHeight, u, v, x, y, width, height, ARGB.white(hudOpacity.getHudOpacity(false)));
    }

    //region Status Bars
    @WrapOperation(method = "renderHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    public void setHealthBarColorOpacity(GuiGraphics instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        instance.blitSprite(pipeline, sprite, x, y, width, height, ARGB.white(hudOpacity.getHudOpacity(false)));
    }

    @WrapOperation(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private static void setArmorBarColorOpacity(GuiGraphics instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        instance.blitSprite(pipeline, sprite, x, y, width, height, ARGB.white(BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false)));
    }

    @WrapOperation(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private void setFoodBarColorOpacity(GuiGraphics instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        instance.blitSprite(pipeline, sprite, x, y, width, height, ARGB.white(hudOpacity.getHudOpacity(false)));
    }

    @WrapOperation(method = "renderAirBubbles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private void setAirBarColorOpacity(GuiGraphics instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        instance.blitSprite(pipeline, sprite, x, y, width, height, ARGB.white(hudOpacity.getHudOpacity(false)));
    }

    @WrapOperation(method = "renderVehicleHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private void setMountHealthBarColorOpacity(GuiGraphics instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        instance.blitSprite(pipeline, sprite, x, y, width, height, ARGB.white(hudOpacity.getHudOpacity(false)));
    }
    //endregion

    //region Status Effects
    @WrapOperation(method = "renderEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    public void setStatusEffectOpacityHead(GuiGraphics instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original){
        instance.blitSprite(pipeline, sprite, x, y, width, height, ARGB.white(hudOpacity.getHudOpacity(false)));
    }

    @ModifyConstant(method = "renderEffects", constant = @Constant(floatValue = 1.f, ordinal = 0))
    public float setOpacityStatusEffectImage (float f){
        return f * hudOpacity.getHudOpacity(false);
    }
    //endregion
}
