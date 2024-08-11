package me.juancarloscp52.bedrockify.mixin;

import me.juancarloscp52.bedrockify.mixin.featureManager.MixinFeatureManager;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class BedrockIfyMixinPlugin  implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
        MixinFeatureManager.loadMixinSettings();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if(mixinClassName.equals("me.juancarloscp52.bedrockify.mixin.common.features.fertilizableBlocks.SugarCaneBlockMixin") && FabricLoader.getInstance().isModLoaded("carpet-extra")){
            LogManager.getLogger().warn("BedrockIfy compatibility with \"Carpet Extra\" enabled.");
            LogManager.getLogger().warn("\t\\_ If you want to bonemeal sugar cane use carpet option /carpet betterBonemeal true");
            return false;
        }
        if(mixinClassName.equals("me.juancarloscp52.bedrockify.mixin.client.features.heldItemTooltips.ItemTooltipsMixin") && FabricLoader.getInstance().isModLoaded("held-item-info")){
            LogManager.getLogger().info("The mod \"Held Item Info\" has been detected. This mod is not totally compatible with BedrockIfy. BedrockIfy Held Item Tooltips has been disabled.");
            return false;
        }
        if(mixinClassName.contains("detailArmorBar") && !FabricLoader.getInstance().isModLoaded("detailab")){
            return false;
        }
        if(mixinClassName.contains("appleskin") && !FabricLoader.getInstance().isModLoaded("appleskin")){
            return false;
        }
        if(mixinClassName.contains("me.juancarloscp52.bedrockify.mixin.client.features.bedrockShading") && FabricLoader.getInstance().isModLoaded("optifabric")){
            LogManager.getLogger().info("The mod \"OptiFabric\" has been detected. This mod is not totally compatible with BedrockIfy. BedrockIfy Bedrock Shading is now disabled.");
            return false;
        }
        if (mixinClassName.equals("me.juancarloscp52.bedrockify.mixin.client.features.sheepColors.SheepWoolFeatureRendererMixin") && FabricLoader.getInstance().isModLoaded("optifabric")) {
            LogManager.getLogger().info("The mod \"OptiFabric\" has been detected. This mod is not totally compatible with BedrockIfy. BedrockIfy Sheep Colors is now disabled.");
            return false;
        }
        if (mixinClassName.contains("me.juancarloscp52.bedrockify.mixin.client.features.eatingAnimations") && FabricLoader.getInstance().isModLoaded("notenoughanimations")) {
            LogManager.getLogger().info("The mod \"Not Enough Animations\" has been detected. This mod is not totally compatible with BedrockIfy. BedrockIfy Eating Animations is now disabled.");
            return false;
        }

        return MixinFeatureManager.isFeatureEnabled(mixinClassName);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
