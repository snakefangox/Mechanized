package net.snakefangox.mechanized;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.Toml4jConfigSerializer;
import net.snakefangox.mechanized.networking.ToServerHandlers;

public class Mechanized implements ModInitializer {

	public static final String MODID = "mechanized";
	public static final String MOD_NAME = "Mechanized";

	public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(new Identifier(MODID, "general"),
			() -> new ItemStack(MRegister.BASIC_BOILER));
	public static MConfig config;

	public static final String COTTON_R_MODID = "cotton-resources";

	@Override
	public void onInitialize() {
		AutoConfig.register(MConfig.class, Toml4jConfigSerializer::new);
		config = AutoConfig.getConfigHolder(MConfig.class).getConfig();

		MRegister.registerEverything();

		ToServerHandlers.initPacketHandlers();
		
		if (!FabricLoader.getInstance().isModLoaded(COTTON_R_MODID)) {
			Registry.BIOME.forEach(MGeneration::addOreToBiome);
			RegistryEntryAddedCallback.event(Registry.BIOME)
					.register((i, identifier, biome) -> MGeneration.addOreToBiome(biome));
		}

		if (FabricLoader.getInstance().isDevelopmentEnvironment())
			AutoGenJson.autoGenerateJson(MODID, "D:\\Code\\Fabric_mods\\Mechanized");
	}

}
