package net.snakefangox.mechanized.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.snakefangox.mechanized.steam.SteamItem;

public class SteamCanister extends Item implements SteamItem {
	
	public static final int STEAM_CAPACITY = SteamItem.UNIT;

	public SteamCanister(Settings settings) {
		super(settings);
	}

	@Override
	public int getSteamAmount(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		if(tag.contains(SteamItem.TAG_KEY)) {
			return tag.getInt(TAG_KEY);
		}
		return 0;
	}

	@Override
	public void setSteamAmount(ItemStack stack, int amount) {
		CompoundTag tag = stack.getOrCreateTag();
		if(tag.contains(SteamItem.TAG_KEY)) {
			tag.putInt(TAG_KEY, amount);
		}
	}

	@Override
	public int getMaxSteamAmount(ItemStack stack) {
		return 0;
	}

}
