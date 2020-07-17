package net.snakefangox.mechanized.tools;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.steam.Steam;

public enum ExoSuitMat implements ArmorMaterial {
	MAT;

	@Override
	public int getDurability(EquipmentSlot slot) {
		return 0;
	}

	@Override
	public int getProtectionAmount(EquipmentSlot slot) {
		switch(slot) {
		case CHEST:
			return 4;
		case FEET:
			return 2;
		case HEAD:
			return 1;
		case LEGS:
			return 3;
		default:
			return 0;
		}
	}

	@Override
	public int getEnchantability() {
		return 12;
	}

	@Override
	public SoundEvent getEquipSound() {
		return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.ofItems(MRegister.BRASS_INGOT);
	}

	@Override
	public String getName() {
		return "steam_exosuit";
	}

	@Override
	public float getToughness() {
		return 1;
	}

	@Override
	public float getKnockbackResistance() {
		return 0;
	}

}
