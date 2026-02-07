package me.juancarloscp52.bedrockify.mixin.common.features.animalEatingParticles;

import me.juancarloscp52.bedrockify.common.features.animalEatingParticles.EatingParticlesUtil;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Wolf.class)
public class WolfMixin {

    @Inject(method = "mobInteract",at=@At(value = "RETURN", ordinal = 0))
    public void interactMob(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        if (cir.getReturnValue() == InteractionResult.CONSUME) {
            EatingParticlesUtil.spawnItemParticles(player, player.getItemInHand(hand), ((Animal) (Object) this));
        }
    }
    @Inject(method = "mobInteract",at=@At(value = "INVOKE",target = "Lnet/minecraft/world/entity/animal/wolf/Wolf;heal(F)V"))
    public void interactMobOnHeal(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        EatingParticlesUtil.spawnItemParticles(player,player.getItemInHand(hand),((Animal)(Object)this));
    }
}
