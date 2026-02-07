package me.juancarloscp52.bedrockify.common.payloads;

import me.juancarloscp52.bedrockify.Bedrockify;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;

import java.util.Objects;

public final class EatParticlePayload extends AbstractVelocityParticlePayload {
    private ItemStack itemStack;

    public static final StreamCodec<RegistryFriendlyByteBuf, EatParticlePayload> CODEC = new StreamCodec<>() {
        @Override
        public EatParticlePayload decode(RegistryFriendlyByteBuf buf) {
            EatParticlePayload result = new EatParticlePayload();
            result.itemStack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
            OptionalCodec.decode(buf, result);
            return result;
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, EatParticlePayload value) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, value.itemStack);
            OptionalCodec.encode(buf, value);
        }
    };

    @Override
    public Type<EatParticlePayload> type() {
        return new Type<>(Identifier.fromNamespaceAndPath(Bedrockify.MOD_ID, "eat-particles"));
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack.copy();
    }

    public static final class EatParticleHandler implements ClientPlayNetworking.PlayPayloadHandler<EatParticlePayload> {
        @Override
        public void receive(EatParticlePayload payload, ClientPlayNetworking.Context context) {
            if (payload == null || context == null) {
                return;
            }
            final Minecraft client = context.client();
            try {
                ItemStack stack = Objects.requireNonNull(payload.itemStack);
                double x = payload.position.x;
                double y = payload.position.y;
                double z = payload.position.z;
                double velx = payload.velocity.x;
                double vely = payload.velocity.y;
                double velz = payload.velocity.z;

                client.execute(() -> {
                    if (null != client.level)
                        client.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), x, y, z, velx, vely, velz);
                });
            } catch (Exception ignored) {
            }
        }
    }
}
