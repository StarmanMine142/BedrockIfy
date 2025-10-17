package me.juancarloscp52.bedrockify.mixin.common.features.animalEatingParticles;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class ExtendMobEntityMixin {
    @Inject(method = "eat", at = @At("HEAD"), cancellable = true)
    protected void bedrockify$mobEat_AtHead(PlayerEntity player, Hand hand, ItemStack stack, CallbackInfo ci) {
        // an empty body for overridable injection point
    }
}
