package net.snakefangox.mechanized.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;

public interface Upgradable {

	public CompoundTag getUpgradeTag(ItemStack stack, Item... upgrades);
	public Item[] getItemsFromStack(ItemStack stack);
	public Object[] getUpgradeFromStack(ItemStack stack);
	public Ingredient validUpgrades(Item item);
	public int upgradeSlotCount();
}
