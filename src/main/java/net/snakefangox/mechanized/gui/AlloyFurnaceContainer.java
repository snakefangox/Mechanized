package net.snakefangox.mechanized.gui;

import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import io.github.cottonmc.cotton.gui.widget.WBar;
import io.github.cottonmc.cotton.gui.widget.WBar.Direction;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;

public class AlloyFurnaceContainer extends CottonCraftingController {

	WBar fire;
	WBar progress;

	public AlloyFurnaceContainer(int syncID, PlayerInventory playerInventory, BlockContext context) {
		super(RecipeType.SMELTING, syncID, playerInventory, getBlockInventory(context),
				getBlockPropertyDelegate(context));
		WGridPanel root = new WGridPanel();
		setRootPanel(root);

		WItemSlot input1 = WItemSlot.of(blockInventory, 0);
		WItemSlot input2 = WItemSlot.of(blockInventory, 1);
		WItemSlot input3 = WItemSlot.of(blockInventory, 2);
		WItemSlot fuel_slot = WItemSlot.of(blockInventory, 3);
		WOutputSlot output_tile = WOutputSlot.outputOf(blockInventory, 4);

		root.add(input1, 2, 0);
		root.add(input2, 3, 0);
		root.add(input3, 4, 0);
		root.add(fuel_slot, 3, 2);
		root.add(output_tile, 7, 1);

		fire = new WBar(Components.FURNACE_FLAME_BG, Components.FURNACE_FLAME, 1, 2, Direction.UP);
		root.add(fire, 3, 1);
		progress = new WBar(Components.PROGRESS_BG, Components.PROGRESS, 0, 3, Direction.RIGHT);
		root.add(progress, 5, 1);

		root.add(createPlayerInventoryPanel(), 0, 4);

		root.validate(this);
	}

	public static class AlloyFurnaceScreen extends CottonInventoryScreen<AlloyFurnaceContainer> {
		public AlloyFurnaceScreen(AlloyFurnaceContainer container, PlayerEntity player) {
			super(container, player);
		}
	}
}
