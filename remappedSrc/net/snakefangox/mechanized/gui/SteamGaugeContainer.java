package net.snakefangox.mechanized.gui;

import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import io.github.cottonmc.cotton.gui.widget.WBar;
import io.github.cottonmc.cotton.gui.widget.WBar.Direction;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;

public class SteamGaugeContainer extends CottonCraftingController {

	public SteamGaugeContainer(int syncID, PlayerInventory playerInventory, BlockContext context) {
		super(RecipeType.SMELTING, syncID, playerInventory, getBlockInventory(context),
				getBlockPropertyDelegate(context));
		WGridPanel root = new WGridPanel();
		setRootPanel(root);

		WBar steam = new WBar(Components.TANK_BG, Components.STEAM_TANK, 0, 1, Direction.UP);
		root.add(steam, 4, 0, 1, 3);

		root.add(createPlayerInventoryPanel(), 0, 4);

		root.validate(this);

	}

	public static class SteamGaugeScreen extends CottonInventoryScreen<SteamGaugeContainer> {
		public SteamGaugeScreen(SteamGaugeContainer container, PlayerEntity player) {
			super(container, player);
		}
	}
}
