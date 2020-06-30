package net.snakefangox.mechanized;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.render.entity.FallingBlockEntityRenderer;
import net.snakefangox.mechanized.blocks.entity.FanEntityRenderer;
import net.snakefangox.mechanized.blocks.entity.SteamChargerEntityRenderer;
import net.snakefangox.mechanized.gui.AlloyFurnaceContainer.AlloyFurnaceScreen;
import net.snakefangox.mechanized.gui.PlacerContainer.PlacerScreen;
import net.snakefangox.mechanized.gui.PressureValveContainer.PressureValveScreen;
import net.snakefangox.mechanized.gui.SteamBoilerContainer.SteamBoilerScreen;
import net.snakefangox.mechanized.gui.UpgradeTableContainer.UpgradeTableScreen;
import net.snakefangox.mechanized.networking.ToClientHandlers;

@Environment(EnvType.CLIENT)
public class MClientRegister {

	public static void registerClient() {

		ToClientHandlers.initPacketHandlers();
		MRegister.setRenderLayers();

		BlockEntityRendererRegistry.INSTANCE.register(MRegister.FAN_ENTITY, FanEntityRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(MRegister.STEAM_CHARGER_ENTITY, SteamChargerEntityRenderer::new);

		ScreenRegistry.register(MRegister.ALLOY_FURNACE_CONTAINER, AlloyFurnaceScreen::new);
		ScreenRegistry.register(MRegister.STEAM_BOILER_CONTAINER, SteamBoilerScreen::new);
		ScreenRegistry.register(MRegister.PRESSURE_VALVE_CONTAINER, PressureValveScreen::new);
		ScreenRegistry.register(MRegister.UPGRADE_TABLE_CONTAINER, UpgradeTableScreen::new);
		ScreenRegistry.register(MRegister.PLACER_CONTAINER, PlacerScreen::new);
		
		EntityRendererRegistry.INSTANCE.register(MRegister.FLYING_BLOCK, (entityRenderDispatcher, context) -> new FallingBlockEntityRenderer(entityRenderDispatcher));
	}

}
