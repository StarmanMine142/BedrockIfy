package me.juancarloscp52.bedrockify.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import org.jetbrains.annotations.Nullable;

public class BedrockIfyDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(BedrockIfyWorldGenProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.CONFIGURED_FEATURE, BedrockIfyWorldGenBootstrap::configureFeatures);
        registryBuilder.add(Registries.PLACED_FEATURE, BedrockIfyWorldGenBootstrap::placedFeatures);
    }

    @Override
    public @Nullable String getEffectiveModId() {
        return "bedrockify";
    }
}
