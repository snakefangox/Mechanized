package net.snakefangox.mechanized.blocks.entity;

import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.parts.StandardInventory;
import net.snakefangox.mechanized.steam.Steam;
import net.snakefangox.mechanized.steam.SteamUtil;

public class SteamChargerEntity extends BlockEntity implements Steam, Tickable, StandardInventory {

	DefaultedList<ItemStack> inv = DefaultedList.ofSize(1, ItemStack.EMPTY);
	
	public SteamChargerEntity() {
		super(MRegister.STEAM_CHARGER_ENTITY);
	}

	@Override
	public void tick() {
		if (world.isClient)
			return;
		if (world.getTime() % 5 == 0) {
			SteamUtil.equalizeSteam(world, this, pos, null);
		}
	}

	@Override
	public int getSteamAmount(Direction dir) {
		return 0;
	}

	@Override
	public int getMaxSteamAmount(Direction dir) {
		return 0;
	}

	@Override
	public void setSteamAmount(Direction dir, int amount) {
		
	}
	
	@Override
	public boolean canPipeConnect(Direction dir) {
		return getCachedState().get(HorizontalFacingBlock.FACING) == dir;
	}

	@Override
	public int getInvMaxStackAmount() {
		return 1;
	}
	
	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		Inventories.fromTag(tag, inv);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		Inventories.toTag(tag, inv);
		return super.toTag(tag);
	}

	@Override
	public DefaultedList<ItemStack> getItems() {
		return inv;
	}
}
