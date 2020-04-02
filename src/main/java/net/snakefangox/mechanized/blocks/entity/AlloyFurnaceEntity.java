package net.snakefangox.mechanized.blocks.entity;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.fabricmc.fabric.impl.content.registry.FuelRegistryImpl;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.blocks.AlloyFurnace;
import net.snakefangox.mechanized.blocks.SteamBoiler;
import net.snakefangox.mechanized.parts.StandardInventory;
import net.snakefangox.mechanized.tools.InventoryTools;

public class AlloyFurnaceEntity extends BlockEntity
		implements StandardInventory, SidedInventory, InventoryProvider, PropertyDelegateHolder, Tickable {

	DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
	static final int[] INPUT_SLOTS = new int[] { 0, 1, 2 };
	static final int[] FUEL_SLOT = new int[] { 3 };
	static final int[] OUTPUT_SLOT = new int[] { 4 };

	static final int CRAFT_AMOUNT = 3;
	static final int SMELT_TIME = 400;

	int progress = 0;
	int fuel = 0;
	int maxFuel = 0;

	public AlloyFurnaceEntity() {
		super(MRegister.ALLOY_FURNACE_ENTITY);
	}

	@Override
	public void tick() {
		if (world.isClient)
			return;
		if (progress == SMELT_TIME) {
			progress = 0;
			InventoryTools.insertItemstack(this, OUTPUT_SLOT[0], new ItemStack(MRegister.BRASS_INGOT, 3));
			//FIXME More filthy hacks
			InventoryTools.extractNamedFromInv(this, 2, "copper", "ingot");
			InventoryTools.extractNamedFromInv(this, 1, "zinc", "ingot");
		} else if (isRecipeValid() && fuel > 0) {
			++progress;
		} else {
			progress = 0;
		}
		int oldFuel = fuel;
		if (fuel > 0) {
			--fuel;
		}
		if (fuel == 0 && isRecipeValid()) {
			Integer fuelAmount = FuelRegistryImpl.INSTANCE.get(inventory.get(FUEL_SLOT[0]).getItem());
			if (fuelAmount != null) {
				InventoryTools.decrementFuel(this, FUEL_SLOT[0]);
				fuel = fuelAmount;
				maxFuel = fuelAmount;
				updateBlockState();
			} else if (oldFuel != fuel) {
				updateBlockState();
			}
		}
	}

	private void updateBlockState() {
		if (world.getBlockState(pos).get(AlloyFurnace.LIT) != fuel > 0)
			world.setBlockState(pos, getCachedState().with(SteamBoiler.LIT, fuel > 0));
	}

	// FIXME Filthy hack for modfest compat
	private boolean isRecipeValid() {
		int copperCount = 0;
		int zincCount = 0;
		for (int i = 0; i < INPUT_SLOTS.length; i++) {
			if (inventory.get(i).getItem() == MRegister.COPPER_INGOT
					|| (inventory.get(i).getItem().getName().toString().contains("copper")
							&& inventory.get(i).getItem().getName().toString().contains("ingot")))
				copperCount += inventory.get(i).getCount();
			if (inventory.get(i).getItem() == MRegister.ZINC_INGOT
					|| (inventory.get(i).getItem().getName().toString().contains("zinc")
							&& inventory.get(i).getItem().getName().toString().contains("ingot")))
				zincCount += inventory.get(i).getCount();
		}
		return copperCount >= 2 && zincCount >= 1
				&& InventoryTools.insertItemstack(this, OUTPUT_SLOT[0], new ItemStack(MRegister.BRASS_INGOT, 3), true);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		Inventories.toTag(tag, inventory);
		tag.putInt("fuel", fuel);
		tag.putInt("maxFuel", maxFuel);
		tag.putInt("progress", progress);
		return super.toTag(tag);
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		Inventories.fromTag(tag, inventory);
		fuel = tag.getInt("fuel");
		maxFuel = tag.getInt("maxFuel");
		progress = tag.getInt("progress");
	}

	@Override
	public int[] getInvAvailableSlots(Direction side) {
		if (side == Direction.DOWN) {
			return OUTPUT_SLOT;
		} else {
			return side == Direction.UP ? INPUT_SLOTS : FUEL_SLOT;
		}
	}

	@Override
	public boolean canInsertInvStack(int slot, ItemStack stack, Direction dir) {
		return slot != 4;
	}

	@Override
	public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) {
		return slot == 4;
	}

	@Override
	public DefaultedList<ItemStack> getItems() {
		return inventory;
	}

	@Override
	public SidedInventory getInventory(BlockState state, IWorld world, BlockPos pos) {
		return this;
	}

	PropertyDelegate propdel = new PropertyDelegate() {

		@Override
		public int size() {
			return 4;
		}

		@Override
		public void set(int index, int value) {
			switch (index) {
			case 0:
				progress = value;
				break;
			case 1:
				fuel = value;
				break;
			case 2:
				maxFuel = value;
				break;
			}
		}

		@Override
		public int get(int index) {
			switch (index) {
			case 0:
				return progress;
			case 1:
				return fuel;
			case 2:
				return maxFuel;
			case 3:
				return SMELT_TIME;
			}
			return 0;
		}
	};

	@Override
	public PropertyDelegate getPropertyDelegate() {
		return propdel;
	}
}
