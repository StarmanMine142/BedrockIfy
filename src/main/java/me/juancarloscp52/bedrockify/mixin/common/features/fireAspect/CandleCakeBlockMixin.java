package me.juancarloscp52.bedrockify.mixin.common.features.fireAspect;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.juancarloscp52.bedrockify.Bedrockify;
import me.juancarloscp52.bedrockify.common.features.fireAspectLight.FireAspectLightHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CandleCakeBlock.class)
public class CandleCakeBlockMixin {

    @ModifyReturnValue(method = "useItemOn", at=@At("RETURN"))
    private InteractionResult onUse(InteractionResult original, ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit){
        if(!Bedrockify.getInstance().settings.fireAspectLight || !player.getAbilities().mayBuild)
            return original;
        ItemStack itemStack = player.getItemInHand(hand);
        if(CandleCakeBlock.candleHit(hit) && FireAspectLightHelper.canLitWith(itemStack)){
            if(!CandleCakeBlock.isLit(state) && CandleCakeBlock.canLight(state)){
                if(world.setBlock(pos, state.setValue(BlockStateProperties.LIT, true), Block.UPDATE_ALL | Block.UPDATE_IMMEDIATE)){
                    itemStack.hurtAndBreak(1, player, hand);
                    world.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
                    world.gameEvent(player, GameEvent.BLOCK_PLACE, pos);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return original;
    }
}
