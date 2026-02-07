package me.juancarloscp52.bedrockify.common.features.animalEatingParticles;

import me.juancarloscp52.bedrockify.common.payloads.EatParticlePayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class EatingParticlesUtil {

    public static void spawnItemParticles(Player player, ItemStack stack, Animal entity) {
        if (player.level().isClientSide())
            return;
        int count = 16;
        for (int i = 0; i < count; ++i) {
            final EatParticlePayload particlePayload = new EatParticlePayload();
            Vec3 vec3d = new Vec3(((double)entity.getRandom().nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec3d = vec3d.xRot(-entity.getXRot() * ((float)Math.PI / 180));
            vec3d = vec3d.yRot(-entity.getYRot() * ((float)Math.PI / 180));
            double d = (double)(-entity.getRandom().nextFloat()) * 0.6 - 0.3;
            Vec3 vec3d2 = new Vec3(((double)entity.getRandom().nextFloat()- 0.5) * 0.3, d, 0.6);
            vec3d2 = vec3d2.xRot(-entity.getXRot() * ((float)Math.PI / 180));
            vec3d2 = vec3d2.yRot(-entity.getYHeadRot() * ((float)Math.PI / 180));
            vec3d2 = vec3d2.add(entity.getX(), entity.getEyeY(), entity.getZ());
            particlePayload.setPosition(vec3d2);
            particlePayload.setVelocity(vec3d);
            particlePayload.setItemStack(stack);
            PlayerLookup.world((ServerLevel) player.level()).forEach(serverPlayerEntity ->
                    ServerPlayNetworking.send(serverPlayerEntity, particlePayload));
        }
    }

}
