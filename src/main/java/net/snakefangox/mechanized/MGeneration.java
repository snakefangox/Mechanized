package net.snakefangox.mechanized;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class MGeneration {

	public static void addOreToBiome(Biome biome) {
		if (Mechanized.config.enableOres && biome.getCategory() != Biome.Category.NETHER && biome.getCategory() != Biome.Category.THEEND) {
			biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES,
					Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE,
							MRegister.COPPER_ORE.getDefaultState(), 14)).createDecoratedFeature(
									Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(20, 0, 12, 64))));
			biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES,
					Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE,
							MRegister.ZINC_ORE.getDefaultState(), 7)).createDecoratedFeature(
									Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(20, 0, 12, 64))));
		}
	}
}
