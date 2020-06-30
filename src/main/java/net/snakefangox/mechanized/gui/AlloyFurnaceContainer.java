package net.snakefangox.mechanized.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import io.github.cottonmc.cotton.gui.widget.WBar;
import io.github.cottonmc.cotton.gui.widget.WBar.Direction;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.snakefangox.mechanized.MRegister;

public class AlloyFurnaceContainer extends SyncedGuiDescription {

	WBar fire;
	WBar progress;

	public AlloyFurnaceContainer(int syncID, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(MRegister.ALLOY_FURNACE_CONTAINER, syncID, playerInventory, getBlockInventory(context),
				getBlockPropertyDelegate(context));
		WGridPanel root = new WGridPanel();
		setRootPanel(root);

		WItemSlot input1 = WItemSlot.of(blockInventory, 0);
		WItemSlot input2 = WItemSlot.of(blockInventory, 1);
		WItemSlot fuel_slot = WItemSlot.of(blockInventory, 2);
		WItemSlot output_tile = WItemSlot.outputOf(blockInventory, 3);
		
		root.add(input1, 2, 0);
		input1.setLocation(input1.getX() + 9, input1.getY());
		root.add(input2, 3, 0);
		input2.setLocation(input2.getX() + 9, input2.getY());
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
		public AlloyFurnaceScreen(AlloyFurnaceContainer container, PlayerInventory player, Text text) {
			super(container, player.player);
		}
	}
}
