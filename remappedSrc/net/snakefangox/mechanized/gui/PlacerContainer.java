package net.snakefangox.mechanized.gui;

import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;

public class PlacerContainer extends CottonCraftingController {

	public PlacerContainer(int syncID, PlayerInventory playerInventory, BlockContext context) {
		super(RecipeType.SMELTING, syncID, playerInventory, getBlockInventory(context),
				getBlockPropertyDelegate(context));
		WGridPanel root = new WGridPanel();
		setRootPanel(root);

		WItemSlot slot = WItemSlot.outputOf(blockInventory, 0);
		root.add(slot, 4, 1);
		
		root.add(createPlayerInventoryPanel(), 0, 4);

		root.validate(this);
	}

	public static class PlacerScreen extends CottonInventoryScreen<PlacerContainer> {
		public PlacerScreen(PlacerContainer container, PlayerEntity player) {
			super(container, player);
		}
	}
}
