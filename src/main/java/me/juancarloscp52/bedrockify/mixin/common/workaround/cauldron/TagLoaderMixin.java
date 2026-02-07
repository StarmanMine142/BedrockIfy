package me.juancarloscp52.bedrockify.mixin.common.workaround.cauldron;

import me.juancarloscp52.bedrockify.Bedrockify;
import me.juancarloscp52.bedrockify.mixin.featureManager.MixinFeatureManager;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TagLoader.class)
public abstract class TagLoaderMixin {
    // TODO(Ravel): target method method_43954 with the signature not found
// TODO(Ravel): target method method_43954 with the signature not found
/** The lambda of <code>tagFile.entries().forEach((entryx) -> { ... } );</code> */
    @Inject(method = "method_43954(Ljava/util/List;Ljava/lang/String;Lnet/minecraft/registry/tag/TagEntry;)V", at = @At("HEAD"), cancellable = true)
    private static void bedrockify$removeCauldronFromTag(List<TagLoader.EntryWithSource> instance, String packName, TagEntry entryx, CallbackInfo ci) {
        if (packName.equals(Bedrockify.MOD_ID) && !MixinFeatureManager.features.get(MixinFeatureManager.FEAT_CAULDRON) && entryx.toString().contains("cauldron")) {
            ci.cancel();
        }
    }
}
