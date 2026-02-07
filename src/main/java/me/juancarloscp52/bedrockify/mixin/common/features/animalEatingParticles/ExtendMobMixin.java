package me.juancarloscp52.bedrockify.mixin.common.features.animalEatingParticles;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class ExtendMobMixin {
    @Inject(method = "usePlayerItem", at = @At("HEAD"), cancellable = true)
    protected void bedrockify$mobEat_AtHead(Player player, InteractionHand hand, ItemStack stack, CallbackInfo ci) {
        // an empty body for overridable injection point
    }
}
