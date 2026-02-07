package me.juancarloscp52.bedrockify.common.payloads;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public abstract class AbstractVelocityParticlePayload implements CustomPacketPayload {
    protected Vec3f position;
    protected Vector3f velocity;

    public void setPosition(Vec3 position) {
        this.position = position;
    }

    public void setVelocity(Vec3 velocity) {
        this.velocity = velocity;
    }

    public static final class OptionalCodec {
        public static void encode(RegistryFriendlyByteBuf buf, AbstractVelocityParticlePayload target) {
            buf.writeVec3(target.position);
            buf.writeVector3f(target.velocity);
        }

        public static void decode(RegistryFriendlyByteBuf buf, AbstractVelocityParticlePayload target) {
            target.position = buf.readVec3();
            target.velocity = buf.readVector3f();
        }
    }
}
