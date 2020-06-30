package net.snakefangox.mechanized.tools;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.steam.Steam;

public class SteamToolMaterial implements ToolMaterial {
	
	public static final SteamToolMaterial INSTANCE = new SteamToolMaterial();

	@Override
	public int getDurability() {
		return Steam.UNIT;
	}

	@Override
	public float getMiningSpeedMultiplier() { return 1.0f; }

	@Override
	public float getAttackDamage() {
		return 2.5f;
	}

	@Override
	public int getMiningLevel() {
		return 2;
	}

	@Override
	public int getEnchantability() {
		return 14;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.ofItems(MRegister.BRASS_INGOT);
	}

}
