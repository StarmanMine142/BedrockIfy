package me.juancarloscp52.bedrockify.mixin.common.features.animalEatingParticles;

import me.juancarloscp52.bedrockify.common.features.animalEatingParticles.EatingParticlesUtil;
import me.juancarloscp52.bedrockify.mixin.common.features.animalEatingParticles.ExtendMobMixin;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Axolotl.class)
public class AxolotlMixin extends ExtendMobMixin {
    @Override
    protected void bedrockify$mobEat_AtHead(Player player, InteractionHand hand, ItemStack stack, CallbackInfo ci) {
        if (stack.is(Items.TROPICAL_FISH_BUCKET)) {
            EatingParticlesUtil.spawnItemParticles(player, new ItemStack(Items.TROPICAL_FISH), Animal.class.cast(this));
        }
    }
}
