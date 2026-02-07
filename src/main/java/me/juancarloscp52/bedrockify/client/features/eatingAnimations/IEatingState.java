package me.juancarloscp52.bedrockify.client.features.eatingAnimations;

import net.minecraft.world.InteractionHand;

import java.util.Optional;

public interface IEatingState {
    default void setEatingHand(InteractionHand hand) {
    }

    default Optional<InteractionHand> getEatingHand() {
        return Optional.empty();
    }
}
