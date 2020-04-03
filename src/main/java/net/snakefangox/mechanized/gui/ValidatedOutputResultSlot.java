package net.snakefangox.mechanized.gui;

import io.github.cottonmc.cotton.gui.ValidatedSlot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class ValidatedOutputResultSlot extends ValidatedSlot {

	public ValidatedOutputResultSlot(Inventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}
	
	@Override
	public boolean canInsert(ItemStack stack) {
		return false;
	}

}
