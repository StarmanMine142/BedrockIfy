package me.juancarloscp52.bedrockify.common.payloads;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public abstract class AbstractVelocityParticlePayload implements CustomPacketPayload {
    protected Vector3f position;
    protected Vector3f velocity;

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public static final class OptionalCodec {
        public static void encode(RegistryFriendlyByteBuf buf, AbstractVelocityParticlePayload target) {
            buf.writeVector3f(target.position);
            buf.writeVector3f(target.velocity);
        }

        public static void decode(RegistryFriendlyByteBuf buf, AbstractVelocityParticlePayload target) {
            target.position = buf.readVector3f();
            target.velocity = buf.readVector3f();
        }
    }
}
