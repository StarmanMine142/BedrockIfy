package me.juancarloscp52.bedrockify.common.features.animalEatingParticles;

import me.juancarloscp52.bedrockify.common.payloads.EatParticlePayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import org.joml.Vector3f;

public class EatingParticlesUtil {

    public static void spawnItemParticles(Player player, ItemStack stack, Animal entity) {
        if (player.level().isClientSide())
            return;
        int count = 16;
        for (int i = 0; i < count; ++i) {
            final EatParticlePayload particlePayload = new EatParticlePayload();
            Vector3f velocity = new Vector3f((entity.getRandom().nextFloat() - 0.5f) * 0.1f, entity.getRandom().nextFloat() * 0.1f + 0.1f, 0.0f);
            velocity = velocity.rotateX(-entity.getXRot() * ((float)Math.PI / 180));
            velocity = velocity.rotateY(-entity.getYRot() * ((float)Math.PI / 180));
            float f = -entity.getRandom().nextFloat() * 0.6f - 0.3f;
            Vector3f position = new Vector3f((entity.getRandom().nextFloat()- 0.5f) * 0.3f, f, 0.6f);
            position = position.rotateX(-entity.getXRot() * ((float)Math.PI / 180));
            position = position.rotateY(-entity.getYHeadRot() * ((float)Math.PI / 180));
            position = position.add((float) entity.getX(),(float) entity.getEyeY(),(float) entity.getZ());
            particlePayload.setPosition(position);
            particlePayload.setVelocity(velocity);
            particlePayload.setItemStack(stack);
            PlayerLookup.level((ServerLevel) player.level()).forEach(serverPlayerEntity ->
                    ServerPlayNetworking.send(serverPlayerEntity, particlePayload));
        }
    }

}
