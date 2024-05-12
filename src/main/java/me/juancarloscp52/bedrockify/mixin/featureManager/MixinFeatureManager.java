package me.juancarloscp52.bedrockify.mixin.featureManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MixinFeatureManager {

    public static final String FEAT_CAULDRON = "common.features.cauldron";

    public static Map<String, Boolean> features = new HashMap<>();
    static {
        features.put("client.core.clientRenderTimer", true);
        features.put("client.core.bedrockIfyButton", true);
        features.put("client.features.chat", true);
        features.put("client.features.eatingAnimations", true);
        features.put("client.features.fishingBobber", true);
        features.put("client.features.heldItemTooltips",true);
        features.put("client.features.idleHandAnimations", true);
        features.put("client.features.loadingScreens", true);
        features.put("client.features.pickupAnimations", true);
        features.put("client.features.reacharoundPlacement", true);
        features.put("client.features.savingOverlay", true);
        features.put("client.features.screenSafeArea", true);
        features.put("client.features.slotHighlight", true);
        features.put("client.features.sheepColors", true);
        features.put("client.features.worldColorNoise",true);
        features.put("client.features.biggerDraggingItem",true);
        features.put("common.features.recipes", true);
        features.put("client.features.useAnimations", true);
        features.put("client.features.bedrockShading.lightBlock", true);
        features.put("client.features.bedrockShading.sunGlare", true);
        features.put("common.features.fireAspect", true);
        features.put("common.features.fertilizableBlocks", true);
        features.put("common.features.animalEatingParticles", true);
        features.put(FEAT_CAULDRON, true);
        features.put("common.features.fernBonemeal", true);
        features.put("client.features.hudOpacity", true);
        features.put("client.features.editionBranding", true);

    }

    public static boolean isFeatureEnabled(String mixin){
        mixin = mixin.replace("me.juancarloscp52.bedrockify.mixin.","");
        String [] split = mixin.split("\\.");
        mixin = mixin.replace("."+split[split.length-1],"");
        if(mixin.contains("worldGeneration") || mixin.contains("workaround")){
            return true;
        }
        return features.get(mixin);
    }

    public static void loadMixinSettings() {
        File file = new File("./config/bedrockify/bedrockifyMixins.json");
        Gson gson = new Gson();
        if (file.exists()) {
            try {
                FileReader fileReader = new FileReader(file);
                Type mapType = new TypeToken<Map<String,Boolean>>() {}.getType();
                Map<String,Boolean> newFeatures = gson.fromJson(fileReader, mapType);
                features.replaceAll((key,value) -> {
                   if(newFeatures.get(key) !=null){
                       return newFeatures.get(key);
                   }else{
                      return value;
                   }
                });
                fileReader.close();
            } catch (Exception e) {
                LogManager.getLogger().warn("Could not load bedrockIfy Mixin settings, creating new config. ERROR: " + e.getLocalizedMessage());
                saveMixinSettings();
            }
        } else {
            LogManager.getLogger().warn("BedrockIfy Mixin Config not found, creating new config.");

            saveMixinSettings();
        }
    }

    public static void saveMixinSettings() {
        Gson gson = new Gson();
        File file = new File("./config/bedrockify/bedrockifyMixins.json");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(gson.toJson(features));
            fileWriter.close();
        } catch (IOException e) {
            LogManager.getLogger().warn("Could not save bedrockIfy Mixin settings: " + e.getLocalizedMessage());
        }
    }
    

}
