package net.snakefangox.mechanized.blocks.entity;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.blocks.Placer;
import net.snakefangox.mechanized.parts.StandardInventory;
import net.snakefangox.mechanized.steam.Steam;
import net.snakefangox.mechanized.steam.SteamUtil;

public class PlacerEntity extends BlockEntity implements Steam, Tickable, StandardInventory {

	private static final int STEAM_CAPACITY = Steam.UNIT;
	private static final int COST_PER_OP = (int) (Steam.UNIT * 0.05);

	int steamAmount = 0;
	boolean extended = false;
	DefaultedList<ItemStack> block = DefaultedList.ofSize(1, ItemStack.EMPTY);

	public PlacerEntity() {
		super(MRegister.PLACER_ENTITY);
	}

	@Override
	public void tick() {
		if (world.isClient)
			return;
		if (world.getTime() % 5 == 0) {
			SteamUtil.equalizeSteam(world, this, pos, null);
			extendOrRetract(false);
		}
		if (world.getTime() % 20 == 0 && world.getReceivedRedstonePower(pos) == 0) {
			BlockPos placepos = pos.offset(getCachedState().get(Properties.FACING));
			ItemStack stack = block.get(0);
			if (world.isAir(placepos) && !stack.isEmpty() && stack.getItem() instanceof BlockItem) {
				Block bl = ((BlockItem) stack.getItem()).getBlock();
				if (bl.getHardness(bl.getDefaultState(), world, placepos) < 0)
					return;
				((BlockItem) stack.getItem()).place(
						new AutomaticItemPlacementContext(world, placepos, getCachedState().get(Properties.FACING),
								stack, getCachedState().get(Properties.FACING).getOpposite()));
				extendOrRetract(true);
				removeSteam(null, COST_PER_OP);
			}
		}
	}

	public void extendOrRetract(boolean extend) {
		if (extended != extend) {
			world.setBlockState(pos, getCachedState().with(Placer.EXTENDED, extend));
			extended = extend;
		}
	}

	@Override
	public int getSteamAmount(Direction dir) {
		return steamAmount;
	}

	@Override
	public int getMaxSteamAmount(Direction dir) {
		return STEAM_CAPACITY;
	}

	@Override
	public void setSteamAmount(Direction dir, int amount) {
		steamAmount = amount;
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		steamAmount = tag.getInt("steamAmount");
		Inventories.toTag(tag, getItems());
		extended = tag.getBoolean("extended");
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putInt("steamAmount", steamAmount);
		Inventories.fromTag(tag, getItems());
		tag.putBoolean("extended", extended);
		return super.toTag(tag);
	}

	@Override
	public DefaultedList<ItemStack> getItems() {
		return block;
	}

	@Override
	public boolean isValidInvStack(int slot, ItemStack stack) {
		return stack.getItem() instanceof BlockItem;
	}
}
