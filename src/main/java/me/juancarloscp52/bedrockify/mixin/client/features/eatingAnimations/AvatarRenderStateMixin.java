package me.juancarloscp52.bedrockify.mixin.client.features.eatingAnimations;

import me.juancarloscp52.bedrockify.client.features.eatingAnimations.IEatingState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(AvatarRenderState.class)
public abstract class AvatarRenderStateMixin implements IEatingState {
    @Unique
    private InteractionHand eatingHand = null;

    @Override
    public void setEatingHand(InteractionHand hand) {
        this.eatingHand = hand;
    }

    @Override
    public Optional<InteractionHand> getEatingHand() {
        return Optional.ofNullable(this.eatingHand);
    }
}
