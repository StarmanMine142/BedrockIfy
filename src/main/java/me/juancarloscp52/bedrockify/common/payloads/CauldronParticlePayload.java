package me.juancarloscp52.bedrockify.common.payloads;

import me.juancarloscp52.bedrockify.Bedrockify;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

import java.util.Objects;

public final class CauldronParticlePayload extends AbstractVelocityParticlePayload {
    private Identifier particleType;

    public static final StreamCodec<RegistryFriendlyByteBuf, CauldronParticlePayload> CODEC = new StreamCodec<>() {
        @Override
        public CauldronParticlePayload decode(RegistryFriendlyByteBuf buf) {
            CauldronParticlePayload result = new CauldronParticlePayload();
            result.particleType = buf.readIdentifier();
            OptionalCodec.decode(buf, result);
            return result;
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, CauldronParticlePayload value) {
            buf.writeIdentifier(value.particleType);
            OptionalCodec.encode(buf, value);
        }
    };

    @Override
    public Type<CauldronParticlePayload> type() {
        return new Type<>(Identifier.fromNamespaceAndPath(Bedrockify.MOD_ID, "cauldron_particles"));
    }

    public void setParticleType(Identifier particleType) {
        this.particleType = particleType;
    }

    public static final class CauldronParticleHandler implements ClientPlayNetworking.PlayPayloadHandler<CauldronParticlePayload> {
        @Override
        public void receive(CauldronParticlePayload payload, ClientPlayNetworking.Context context) {
            if (payload == null || context == null) {
                return;
            }
            final Minecraft client = context.client();
            try {
                var particle = Objects.requireNonNull(BuiltInRegistries.PARTICLE_TYPE.getValue(payload.particleType));
                double x = payload.position.x;
                double y = payload.position.y;
                double z = payload.position.z;
                float vx = (float) payload.velocity.x;
                float vy = (float) payload.velocity.y;
                float vz = (float) payload.velocity.z;

                if (particle instanceof ParticleOptions generic) {
                    client.execute(() -> {
                        if (null != client.level) {
                            client.level.addParticle(generic, x, y, z, vx, vy, vz);
                        }
                    });
                } else if (particle.equals(ParticleTypes.ENTITY_EFFECT)) {
                    client.execute(() -> {
                        if (null != client.level) {
                            client.level.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, vx, vy, vz), x, y, z, vx, vy, vz);
                        }
                    });
                }
            } catch (Exception ignored) {
            }
        }
    }
}
