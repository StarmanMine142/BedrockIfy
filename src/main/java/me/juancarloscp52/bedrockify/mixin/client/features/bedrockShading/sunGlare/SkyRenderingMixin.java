package me.juancarloscp52.bedrockify.mixin.client.features.bedrockShading.sunGlare;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.bedrockShading.BedrockSunGlareShading;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.render.state.SkyRenderState;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;

@Mixin(SkyRendering.class)
public abstract class SkyRenderingMixin {
    @Unique
    private final BedrockSunGlareShading sunGlareShading = BedrockifyClient.getInstance().bedrockSunGlareShading;
    @Unique
    private GpuBuffer sunVertexBuffer;
    @Unique
    private int sunIndexCount;

    @Accessor("SUN_TEXTURE")
    public static Identifier getSunTextureId() {
        throw new AssertionError();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void bedrockify$SkyRenderingCtor(CallbackInfo ci) {
        // Allocate buffer.
        try (BufferAllocator bufferAllocator = BufferAllocator.fixedSized(4 * VertexFormats.POSITION_TEXTURE_COLOR.getVertexSize())) {
            BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

            Matrix4f matrix4f = new Matrix4f();
            bufferBuilder.vertex(matrix4f, -30.0F, 100.0F, -30.0F).texture(0.0F, 0.0F).color(-1);
            bufferBuilder.vertex(matrix4f, 30.0F, 100.0F, -30.0F).texture(1.0F, 0.0F).color(-1);
            bufferBuilder.vertex(matrix4f, 30.0F, 100.0F, 30.0F).texture(1.0F, 1.0F).color(-1);
            bufferBuilder.vertex(matrix4f, -30.0F, 100.0F, 30.0F).texture(0.0F, 1.0F).color(-1);

            try (BuiltBuffer builtBuffer = bufferBuilder.end()) {
                this.sunIndexCount = builtBuffer.getDrawParameters().indexCount();
                this.sunVertexBuffer = RenderSystem.getDevice().createBuffer(() -> "Bedrockify Sun vertex buffer", 8, builtBuffer.getBuffer());
            }
        }
    }

    @WrapOperation(method = "renderCelestialBodies", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/SkyRendering;renderSun(FLnet/minecraft/client/util/math/MatrixStack;)V"))
    private void bedrockify$modifySunIntensity(SkyRendering instance, float alpha, MatrixStack matrices, Operation<Void> original) {
        final float intensity = MathHelper.lerp(sunGlareShading.getSunBrightnessDelta(), 1.5f + sunGlareShading.getSunIntensityDelta() * 0.5f, 1.0f);
        final float scale = MathHelper.lerp(sunGlareShading.getSunBrightnessDelta(), 1.3f, 1f);

        // Get model view matrix.
        Matrix4fStack modelView = RenderSystem.getModelViewStack();
        modelView.pushMatrix();

        // Get position matrix.
        Matrix4f position = matrices.peek().getPositionMatrix();
        // Apply scaling.
        modelView.mul(position).scale(scale, 1f, scale);

        // Set up texture renderer.
        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
        AbstractTexture abstractTexture = textureManager.getTexture(getSunTextureId());
        abstractTexture.setUseMipmaps(false);
        RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
        GpuBuffer gpuBuffer = shapeIndexBuffer.getIndexBuffer(this.sunIndexCount);
        GpuTextureView colorView = MinecraftClient.getInstance().getFramebuffer().getColorAttachmentView();
        GpuTextureView depthView = MinecraftClient.getInstance().getFramebuffer().getDepthAttachmentView();
        GpuBufferSlice uniform = RenderSystem.getDynamicUniforms().write(modelView, new Vector4f(intensity, intensity, intensity, alpha), new Vector3f(), new Matrix4f(), 0.0F);

        // Draw.
        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Bedrockify Sun", colorView, OptionalInt.empty(), depthView, OptionalDouble.empty())) {
            renderPass.setPipeline(RenderPipelines.POSITION_TEX_COLOR_CELESTIAL);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", uniform);
            renderPass.bindSampler("Sampler0", abstractTexture.getGlTextureView());
            renderPass.setVertexBuffer(0, Objects.requireNonNull(this.sunVertexBuffer));
            renderPass.setIndexBuffer(gpuBuffer, shapeIndexBuffer.getIndexType());
            renderPass.drawIndexed(0, 0, this.sunIndexCount, 1);
        }

        modelView.popMatrix();
    }

    @Inject(method = "updateRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getSkyColor(Lnet/minecraft/util/math/Vec3d;F)I"))
    private static void bedrockify$updateSunAngleDiff(ClientWorld world, float f, Vec3d pos, SkyRenderState state, CallbackInfo ci) {
        final BedrockSunGlareShading sunGlareShading = BedrockifyClient.getInstance().bedrockSunGlareShading;
        sunGlareShading.updateSunBrightnessDelta(f);
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void bedrockify$closeBuffer(CallbackInfo ci) {
        if (this.sunVertexBuffer != null) {
            this.sunVertexBuffer.close();
        }
    }
}
