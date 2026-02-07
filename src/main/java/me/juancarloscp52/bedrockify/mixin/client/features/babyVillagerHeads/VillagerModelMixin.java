package me.juancarloscp52.bedrockify.mixin.client.features.babyVillagerHeads;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerModel.class)
public abstract class VillagerModelMixin {
    @Shadow @Final private ModelPart head;

    @Inject(method = "setupAnim", at = @At("RETURN"))
    private void bedrockify$customBabyHeadScale(VillagerRenderState villagerEntityRenderState, CallbackInfo ci) {
        if (villagerEntityRenderState.isBaby && BedrockifyClient.getInstance().settings.babyVillagerHeads) {
            this.head.offsetScale(new Vector3f(0.5f));
        }
    }
}
