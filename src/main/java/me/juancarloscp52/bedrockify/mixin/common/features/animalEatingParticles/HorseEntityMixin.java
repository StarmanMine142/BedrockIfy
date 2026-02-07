package me.juancarloscp52.bedrockify.mixin.common.features.animalEatingParticles;

import me.juancarloscp52.bedrockify.common.features.animalEatingParticles.EatingParticlesUtil;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public class HorseEntityMixin{

    @Inject(method = "fedFood",at=@At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;consume(ILnet/minecraft/world/entity/LivingEntity;)V"))
    public void eat (Player player, ItemStack stack, CallbackInfoReturnable<InteractionResult> cir){
        EatingParticlesUtil.spawnItemParticles(player, stack, ((Animal)(Object)this));
    }

}
