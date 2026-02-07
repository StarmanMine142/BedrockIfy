package me.juancarloscp52.bedrockify.client.features.fishingBobber;

import me.juancarloscp52.bedrockify.Bedrockify;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.state.FishingHookRenderState;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class FishingBobber3DModel<T extends FishingHookRenderState> extends EntityModel<T> {
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Bedrockify.MOD_ID, "fishing_hook"), "main");
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Bedrockify.MOD_ID, "textures/entity/fishing_hook.png");
    public static final RenderType RENDER_LAYER = RenderTypes.entityTranslucent(TEXTURE);

    private static final String NAME_HEAD_X = "head_axis_x";
    private static final String NAME_HEAD_Z = "head_axis_z";
    private static final String NAME_BOBBER = "bobber";
    private static final String NAME_HOOK = "hook";
    private static final float ANGLE_180_DEGREES = (float) (1f * Math.PI);

    public FishingBobber3DModel(@NotNull ModelPart root) {
        super(root);
    }

    @NotNull
    public static LayerDefinition generateModel() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();

        modelPartData.addOrReplaceChild(NAME_HEAD_X, CubeListBuilder.create().addBox(-0.5f, 3f, 0f, 1f, 1f, 0f), PartPose.ZERO);
        modelPartData.addOrReplaceChild(NAME_HEAD_Z, CubeListBuilder.create().addBox(0f, 3f, -0.5f, 0f, 1f, 1f), PartPose.ZERO);
        modelPartData.addOrReplaceChild(NAME_BOBBER, CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -1.5f, 3f, 3f, 3f), PartPose.offsetAndRotation(0f, 3f, 0f, ANGLE_180_DEGREES, 0f, 0f));
        modelPartData.addOrReplaceChild(NAME_HOOK, CubeListBuilder.create().texOffs(0, 6).addBox(-0.5f, -3f, 0f, 3f, 3f, 0f), PartPose.offsetAndRotation(0f, -3f, 0f, ANGLE_180_DEGREES, 0f, 0f));

        return LayerDefinition.create(modelData, 12, 9);
    }

    @Override
    public void setupAnim(T state) {
    }
}
