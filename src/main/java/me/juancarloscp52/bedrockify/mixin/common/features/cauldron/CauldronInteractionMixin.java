package me.juancarloscp52.bedrockify.mixin.common.features.cauldron;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.juancarloscp52.bedrockify.Bedrockify;
import me.juancarloscp52.bedrockify.common.block.cauldron.BedrockCauldronBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CauldronInteractions.class)
public abstract class CauldronInteractionMixin {
    /**
     * The lambda of <code>EMPTY.put(Items.POTION, (state, world, pos, player, hand, stack) -> { ... });</code>
     */
    @ModifyReturnValue(method = "lambda$bootStrap$0", at = @At("RETURN"))
    private static InteractionResult bedrockify$addCauldronEmptyBehavior(InteractionResult original, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (!Bedrockify.getInstance().settings.bedrockCauldron) {
            return original;
        }

        if (!(original instanceof InteractionResult.Success)) {
            // Redirect to customized behavior.
            final InteractionResult result = BedrockCauldronBehavior.PLACE_POTION_FLUID.interact(state, world, pos, player, hand, stack);
            return result;
        }

        return original;
    }
}
