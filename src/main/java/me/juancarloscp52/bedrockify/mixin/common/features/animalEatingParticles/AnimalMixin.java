package me.juancarloscp52.bedrockify.mixin.common.features.animalEatingParticles;

import me.juancarloscp52.bedrockify.common.features.animalEatingParticles.EatingParticlesUtil;
import me.juancarloscp52.bedrockify.mixin.common.features.animalEatingParticles.ExtendMobMixin;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public class AnimalMixin extends ExtendMobMixin {
    @Override
    protected void bedrockify$mobEat_AtHead(Player player, InteractionHand hand, ItemStack stack, CallbackInfo ci) {
        EatingParticlesUtil.spawnItemParticles(player, stack, Animal.class.cast(this));
    }
}
