package me.juancarloscp52.bedrockify.mixin.client.features.pickupAnimations;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin{

    @Unique
    private float pickedItemCooldownLeft =0.0f;

    @Inject(method = "renderHotbarItem", at=@At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getBobbingAnimationTime()I"))
    private void captureItemStack(DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci){
        pickedItemCooldownLeft = stack.getBobbingAnimationTime()-tickCounter.getTickProgress(true);
    }
    @WrapOperation(method = "renderHotbarItem", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3x2fStack;scale(FF)Lorg/joml/Matrix3x2f;"))
    private Matrix3x2f applyAnimation(Matrix3x2fStack instance, float x, float y, Operation<Matrix3x2f> original){
        if(BedrockifyClient.getInstance().settings.isPickupAnimationsEnabled() && pickedItemCooldownLeft >0.0f){
            float animation = 1.0f + pickedItemCooldownLeft / 12.5f;
            return original.call(instance, animation, animation);
        }
        return original.call(instance, x,y);
    }
}
