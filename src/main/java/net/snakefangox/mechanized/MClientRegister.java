package net.snakefangox.mechanized;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.container.BlockContext;
import net.snakefangox.mechanized.gui.AlloyFurnaceContainer;
import net.snakefangox.mechanized.gui.AlloyFurnaceContainer.AlloyFurnaceScreen;
import net.snakefangox.mechanized.gui.SteamBoilerContainer;
import net.snakefangox.mechanized.gui.SteamBoilerContainer.SteamBoilerScreen;
import net.snakefangox.mechanized.gui.SteamGaugeContainer;
import net.snakefangox.mechanized.gui.SteamGaugeContainer.SteamGaugeScreen;

public class MClientRegister {

	public static void registerClient() {
		ScreenProviderRegistry.INSTANCE.registerFactory(MRegister.ALLOY_FURNACE_CONTAINER,
				(syncId, identifier, player, buf) -> new AlloyFurnaceScreen(new AlloyFurnaceContainer(syncId,
						player.inventory, BlockContext.create(player.world, buf.readBlockPos())), player));
		ScreenProviderRegistry.INSTANCE.registerFactory(MRegister.STEAM_BOILER_CONTAINER,
				(syncId, identifier, player, buf) -> new SteamBoilerScreen(new SteamBoilerContainer(syncId,
						player.inventory, BlockContext.create(player.world, buf.readBlockPos())), player));
		ScreenProviderRegistry.INSTANCE.registerFactory(MRegister.STEAM_GAUGE_CONTAINER,
				(syncId, identifier, player, buf) -> new SteamGaugeScreen(new SteamGaugeContainer(syncId,
						player.inventory, BlockContext.create(player.world, buf.readBlockPos())), player));
	}
	
}
