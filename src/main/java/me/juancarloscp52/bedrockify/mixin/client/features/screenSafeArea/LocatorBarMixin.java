package me.juancarloscp52.bedrockify.mixin.client.features.screenSafeArea;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.minecraft.client.gui.hud.bar.LocatorBar;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LocatorBar.class)
public class LocatorBarMixin {

    @ModifyArg(method = "renderBar", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"),index = 3)
    public int modifyTextureLocatorBar(int y){
        return y - BedrockifyClient.getInstance().settings.getScreenSafeArea();
    }

    @WrapOperation(method = "renderAddons", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/bar/LocatorBar;getCenterY(Lnet/minecraft/client/util/Window;)I"))
    private int modfiyLocator(LocatorBar instance, Window window, Operation<Integer> original){
        return original.call(instance, window) - BedrockifyClient.getInstance().settings.getScreenSafeArea();
    }
}
