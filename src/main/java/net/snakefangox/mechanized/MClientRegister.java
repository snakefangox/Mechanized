package net.snakefangox.mechanized;

import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.container.BlockContext;
import net.snakefangox.mechanized.blocks.entity.FanEntityRenderer;
import net.snakefangox.mechanized.blocks.entity.SteamChargerEntityRenderer;
import net.snakefangox.mechanized.gui.AlloyFurnaceContainer;
import net.snakefangox.mechanized.gui.AlloyFurnaceContainer.AlloyFurnaceScreen;
import net.snakefangox.mechanized.gui.PressureValveContainer;
import net.snakefangox.mechanized.gui.PressureValveContainer.PressureValveScreen;
import net.snakefangox.mechanized.gui.SteamBoilerContainer;
import net.snakefangox.mechanized.gui.SteamBoilerContainer.SteamBoilerScreen;
import net.snakefangox.mechanized.gui.UpgradeTableContainer;
import net.snakefangox.mechanized.gui.UpgradeTableContainer.UpgradeTableScreen;
import net.snakefangox.mechanized.networking.ToClientHandlers;

public class MClientRegister {

	public static void registerClient() {

		ToClientHandlers.initPacketHandlers();

		BlockEntityRendererRegistry.INSTANCE.register(MRegister.FAN_ENTITY, FanEntityRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(MRegister.STEAM_CHARGER_ENTITY, SteamChargerEntityRenderer::new);

		ScreenProviderRegistry.INSTANCE.registerFactory(MRegister.ALLOY_FURNACE_CONTAINER,
				(syncId, identifier, player, buf) -> new AlloyFurnaceScreen(new AlloyFurnaceContainer(syncId,
						player.inventory, BlockContext.create(player.world, buf.readBlockPos())), player));
		ScreenProviderRegistry.INSTANCE.registerFactory(MRegister.STEAM_BOILER_CONTAINER,
				(syncId, identifier, player, buf) -> new SteamBoilerScreen(new SteamBoilerContainer(syncId,
						player.inventory, BlockContext.create(player.world, buf.readBlockPos())), player));
		ScreenProviderRegistry.INSTANCE.registerFactory(MRegister.PRESSURE_VALVE_CONTAINER,
				(syncId, identifier, player, buf) -> new PressureValveScreen(new PressureValveContainer(syncId,
						player.inventory, BlockContext.create(player.world, buf.readBlockPos())), player));
		ScreenProviderRegistry.INSTANCE.registerFactory(MRegister.UPGRADE_TABLE_CONTAINER, (syncId, identifier, player,
				buf) -> new UpgradeTableScreen(new UpgradeTableContainer(syncId, player.inventory), player));
	}

}
