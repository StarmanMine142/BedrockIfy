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
/** The lambda of <code>tagFile.entries().forEach((entryx) -> { ... } );</code> */
    @Inject(method = "lambda$load$1(Ljava/util/List;Ljava/lang/String;Lnet/minecraft/tags/TagEntry;)V", at = @At("HEAD"), cancellable = true)
    private static void bedrockify$removeCauldronFromTag(List<?> instance, String packName, TagEntry entryx, CallbackInfo ci) {
        if (packName.equals(Bedrockify.MOD_ID) && !MixinFeatureManager.features.get(MixinFeatureManager.FEAT_CAULDRON) && entryx.toString().contains("cauldron")) {
            ci.cancel();
        }
    }
}
