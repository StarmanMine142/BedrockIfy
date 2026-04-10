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

    @Inject(method = "mobInteract",at=@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/wolf/Wolf;tryToTame(Lnet/minecraft/world/entity/player/Player;)V"))
    public void bedrockify$onTryToTame(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        EatingParticlesUtil.spawnItemParticles(player, player.getItemInHand(hand), ((Animal) (Object) this));
    }
    @Inject(method = "mobInteract",at=@At(value = "INVOKE",target = "Lnet/minecraft/world/entity/animal/wolf/Wolf;feed(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;FF)V"))
    public void bedrockify$onFeed(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        EatingParticlesUtil.spawnItemParticles(player,player.getItemInHand(hand),((Animal)(Object)this));
    }
}
