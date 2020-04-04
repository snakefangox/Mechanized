package net.snakefangox.mechanized.items;

import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;

public interface Upgradable {

	public CompoundTag getUpgradeTag(CompoundTag tag, Item... upgrades);
	public Item[] getItemsFromTag(CompoundTag tag);
	public Object[] getUpgradeFromTag(CompoundTag tag);
	public Ingredient validUpgrades();
	public int upgradeSlotCount();
}
