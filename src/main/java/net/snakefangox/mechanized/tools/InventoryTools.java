package net.snakefangox.mechanized.tools;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.collection.DefaultedList;

public class InventoryTools {

	/**
	 * Tries to insert an itemstack and returns true if it's possible
	 * 
	 * @param inv
	 * @param slot
	 * @param stack
	 * @param simulate If true then just simulate the action
	 * @return if the action can go ahead
	 */
	public static boolean insertItemstack(Inventory inv, int slot, ItemStack stack, boolean simulate) {
		ItemStack inSlot = inv.getStack(slot);
		if (inSlot.isEmpty()) {
			if (!simulate) {
				inv.setStack(slot, stack);
			}
			return true;
		} else if (inSlot.getItem() == stack.getItem()
				&& stack.getCount() + inSlot.getCount() <= inSlot.getMaxCount()) {
			if (!simulate) {
				inSlot.increment(stack.getCount());
			}
			return true;
		} else {
			return false;
		}
	}

	public static boolean insertItemstack(Inventory inv, int slot, ItemStack stack) {
		return insertItemstack(inv, slot, stack, false);
	}

	public static void extractSetFromInv(Inventory inv, Item item, int amountIn) {
		int amount = amountIn;
		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getStack(i);
			if (stack.getItem() == item) {
				if (stack.getCount() > amount) {
					stack.decrement(amount);
					return;
				} else if (stack.getCount() == amount) {
					inv.setStack(i, ItemStack.EMPTY);
					return;
				} else {
					int count = stack.getCount();
					inv.setStack(i, ItemStack.EMPTY);
					amount -= count;
				}
			}
		}
	}

	public static void decrementFuel(Inventory inv, int slot) {
		ItemStack stack = inv.getStack(slot);
		if (stack.getItem() == Items.LAVA_BUCKET) {
			inv.setStack(slot, new ItemStack(Items.BUCKET));
		} else {
			stack.decrement(1);
		}
	}
	
	public static CompoundTag toTagIncEmpty(CompoundTag tag, DefaultedList<ItemStack> stacks, boolean setIfEmpty) {
		ListTag listTag = new ListTag();

		for (int i = 0; i < stacks.size(); ++i) {
			ItemStack itemStack = (ItemStack) stacks.get(i);
			CompoundTag compoundTag = new CompoundTag();
			compoundTag.putByte("Slot", (byte) i);
			itemStack.toTag(compoundTag);
			listTag.add(compoundTag);
		}

		if (!listTag.isEmpty() || setIfEmpty) {
			tag.put("Items", listTag);
		}

		return tag;
	}
}
