package me.juancarloscp52.bedrockify.mixin.common.features.fireAspect;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.juancarloscp52.bedrockify.Bedrockify;
import me.juancarloscp52.bedrockify.common.features.fireAspectLight.FireAspectLightHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.MinecartTNT;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class PlayerMixin {

    @ModifyExpressionValue(method = "interactOn",at=@At(value = "INVOKE",target = "Lnet/minecraft/world/entity/Entity;interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/InteractionResult;"))
    private InteractionResult interact(InteractionResult original, Entity entity, InteractionHand hand){
        Player $this = Player.class.cast(this);
        if(entity instanceof MinecartTNT tntMinecart && Bedrockify.getInstance().settings.fireAspectLight && $this.getAbilities().mayBuild){
            ItemStack itemStack = $this.getItemInHand(hand);
            if(!tntMinecart.isPrimed() && (FireAspectLightHelper.canLitWith(itemStack) || (itemStack.is(Items.FLINT_AND_STEEL) || itemStack.is(Items.FIRE_CHARGE)))){
                tntMinecart.primeFuse($this.damageSources().explosion(entity,$this));
                itemStack.hurtAndBreak(1, $this, hand);
                $this.level().playSound($this, $this.blockPosition(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, $this.level().getRandom().nextFloat() * 0.4F + 0.8F);
                return InteractionResult.SUCCESS;
            }
        }
        return original;
    }

}
