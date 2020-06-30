package net.snakefangox.mechanized.blocks.entity;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.fabricmc.fabric.impl.content.registry.FuelRegistryImpl;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.blocks.AlloyFurnace;
import net.snakefangox.mechanized.blocks.SteamBoiler;
import net.snakefangox.mechanized.gui.AlloyFurnaceContainer;
import net.snakefangox.mechanized.parts.StandardInventory;
import net.snakefangox.mechanized.recipes.AlloyRecipe;
import net.snakefangox.mechanized.tools.InventoryTools;

import java.util.Optional;

public class AlloyFurnaceEntity extends BlockEntity
		implements StandardInventory, SidedInventory, InventoryProvider, PropertyDelegateHolder, Tickable {

	DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);
	static final int[] INPUT_SLOTS = new int[] { 0, 1 };
	static final int[] FUEL_SLOT = new int[] { 2 };
	static final int[] OUTPUT_SLOT = new int[] { 3 };

	static final int CRAFT_AMOUNT = 3;
	public static final int SMELT_TIME = 400;

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
			smelt();
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
		} else if (fuel == 0 && oldFuel != fuel) {
			updateBlockState();
		}
	}

	private void updateBlockState() {
		if (world.getBlockState(pos).get(AlloyFurnace.LIT) != fuel > 0)
			world.setBlockState(pos, getCachedState().with(SteamBoiler.LIT, fuel > 0));
	}

	private void smelt() {
		Optional<AlloyRecipe> match = world.getRecipeManager().getFirstMatch(AlloyRecipe.AlloyRecipeType.INSTANCE, this,
				world);
		if (match.isPresent()) {
			AlloyRecipe recipe = match.get();
			InventoryTools.insertItemstack(this, OUTPUT_SLOT[0], recipe.craft(this));
		}
	}

	private boolean isRecipeValid() {
		Optional<AlloyRecipe> match = world.getRecipeManager().getFirstMatch(AlloyRecipe.AlloyRecipeType.INSTANCE, this,
				world);
		return match.isPresent() && InventoryTools.insertItemstack(this, OUTPUT_SLOT[0], match.get().getOutput(), true);
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
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		Inventories.fromTag(tag, inventory);
		fuel = tag.getInt("fuel");
		maxFuel = tag.getInt("maxFuel");
		progress = tag.getInt("progress");
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		if (side == Direction.DOWN) {
			return OUTPUT_SLOT;
		} else {
			return side == Direction.UP ? INPUT_SLOTS : FUEL_SLOT;
		}
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		if (slot == OUTPUT_SLOT[0])
			return false;
		if(slot == FUEL_SLOT[0])
			return FuelRegistryImpl.INSTANCE.get(stack.getItem()) != null;
		return true;
	}

	@Override
	public DefaultedList<ItemStack> getItems() {
		return inventory;
	}

	@Override
	public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
		return this;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		if (slot == OUTPUT_SLOT[0])
			return false;
		if(slot == FUEL_SLOT[0])
			return FuelRegistryImpl.INSTANCE.get(stack.getItem()) > 0;
		return true;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return slot == OUTPUT_SLOT[0];
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
