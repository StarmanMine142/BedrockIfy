package me.juancarloscp52.bedrockify.mixin.common.features.cauldron;

import me.juancarloscp52.bedrockify.Bedrockify;
import me.juancarloscp52.bedrockify.common.block.cauldron.BedrockCauldronBehavior;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CauldronInteraction.class)
public interface CauldronInteractionMixin {
    /**
     * The lambda of <code>EMPTY_CAULDRON_BEHAVIOR.put(Items.POTION, (state, world, pos, player, hand, stack) -> { ... });</code>
     */
    @Inject(method = "method_32222", at = @At("RETURN"), cancellable = true)
    private static void bedrockify$addCauldronEmptyBehavior(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, CallbackInfoReturnable<InteractionResult> cir) {
        var components = stack.get(DataComponents.POTION_CONTENTS);
        if (!Bedrockify.getInstance().settings.bedrockCauldron || components != null && components.is(Potions.WATER)) {
            return;
        }

        // Redirect to customized behavior.
        final InteractionResult result = BedrockCauldronBehavior.PLACE_POTION_FLUID.interact(state, world, pos, player, hand, stack);
        cir.setReturnValue(result);
    }
}
