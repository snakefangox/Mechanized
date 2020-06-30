package net.snakefangox.mechanized.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import io.github.cottonmc.cotton.gui.widget.WBar;
import io.github.cottonmc.cotton.gui.widget.WBar.Direction;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.snakefangox.mechanized.MRegister;

public class SteamGaugeContainer extends SyncedGuiDescription {

	public SteamGaugeContainer(int syncID, PlayerInventory playerInventory, ScreenHandlerContext context) {
		// Set this V properly if you ever use it
		super(MRegister.STEAM_BOILER_CONTAINER, syncID, playerInventory, getBlockInventory(context),
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
