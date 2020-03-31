package net.snakefangox.mechanized;

import com.google.common.base.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.tools.FabricToolTags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.container.BlockContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.snakefangox.mechanized.blocks.AlloyFurnace;
import net.snakefangox.mechanized.blocks.SteamBoiler;
import net.snakefangox.mechanized.blocks.SteamPipe;
import net.snakefangox.mechanized.blocks.SteamTank;
import net.snakefangox.mechanized.blocks.entity.AlloyFurnaceEntity;
import net.snakefangox.mechanized.blocks.entity.SteamBoilerEntity;
import net.snakefangox.mechanized.blocks.entity.SteamPipeEntity;
import net.snakefangox.mechanized.blocks.entity.SteamTankEntity;
import net.snakefangox.mechanized.gui.AlloyFurnaceContainer;
import net.snakefangox.mechanized.gui.AlloyFurnaceContainer.AlloyFurnaceScreen;
import net.snakefangox.mechanized.gui.SteamBoilerContainer;
import net.snakefangox.mechanized.gui.SteamBoilerContainer.SteamBoilerScreen;
import net.snakefangox.mechanized.gui.SteamTankContainer;
import net.snakefangox.mechanized.gui.SteamTankContainer.SteamTankScreen;
import net.snakefangox.mechanized.steam.SteamPipeNetworkStorage;

public class MRegister {

	// Blocks
	public static final Block COPPER_ORE = new Block(FabricBlockSettings.of(Material.STONE).hardness(5).resistance(3)
			.breakByTool(FabricToolTags.PICKAXES, 1).build());
	public static final Block ZINC_ORE = new Block(FabricBlockSettings.of(Material.STONE).hardness(5).resistance(3)
			.breakByTool(FabricToolTags.PICKAXES, 1).build());
	public static final Block ALLOY_FURNACE = new AlloyFurnace(FabricBlockSettings.of(Material.STONE).hardness(4)
			.resistance(3).breakByTool(FabricToolTags.PICKAXES).build());
	public static final Block STEAM_BOILER = new SteamBoiler(FabricBlockSettings.of(Material.METAL).hardness(4)
			.resistance(3).breakByTool(FabricToolTags.PICKAXES).build());
	public static final Block STEAM_PIPE = new SteamPipe(FabricBlockSettings.of(Material.METAL).hardness(3)
			.resistance(3).breakByTool(FabricToolTags.PICKAXES).build());
	public static final Block STEAM_TANK = new SteamTank(FabricBlockSettings.of(Material.METAL).hardness(4)
			.resistance(3).breakByTool(FabricToolTags.PICKAXES).build());

	// BlockEntities
	public static BlockEntityType<AlloyFurnaceEntity> ALLOY_FURNACE_ENTITY;
	public static BlockEntityType<SteamBoilerEntity> STEAM_BOILER_ENTITY;
	public static BlockEntityType<SteamPipeEntity> STEAM_PIPE_ENTITY;
	public static BlockEntityType<SteamTankEntity> STEAM_TANK_ENTITY;

	// Containers
	public static final Identifier ALLOY_FURNACE_CONTAINER = new Identifier(Mechanized.MODID, "alloy_furnace");
	public static final Identifier STEAM_BOILER_CONTAINER = new Identifier(Mechanized.MODID, "steam_boiler");
	public static final Identifier STEAM_TANK_CONTAINER = new Identifier(Mechanized.MODID, "steam_tank");

	// Items
	public static final Item COPPER_INGOT = new Item(new Item.Settings().group(Mechanized.ITEM_GROUP));
	public static final Item ZINC_INGOT = new Item(new Item.Settings().group(Mechanized.ITEM_GROUP));
	public static final Item BRASS_INGOT = new Item(new Item.Settings().group(Mechanized.ITEM_GROUP));
	public static final Item DEBUG_TOOL = new Item(new Item.Settings().group(Mechanized.ITEM_GROUP)) {
		public net.minecraft.util.ActionResult useOnBlock(net.minecraft.item.ItemUsageContext context) {
			if (context.getWorld().isClient)
				return ActionResult.SUCCESS;
			if (context.getWorld().getBlockEntity(context.getBlockPos()) != null)
				context.getPlayer().addChatMessage(new LiteralText(
						context.getWorld().getBlockEntity(context.getBlockPos()).toTag(new CompoundTag()).asString()),
						true);
			System.out.println(SteamPipeNetworkStorage.getInstance((ServerWorld) context.getWorld()));
			return ActionResult.SUCCESS;
		};
	};

	@SuppressWarnings("unchecked")
	public static void registerEverything() {
		registerBlock(COPPER_ORE, new Identifier(Mechanized.MODID, "copper_ore"), RenderLayerEnum.CUTOUT);
		registerBlock(ZINC_ORE, new Identifier(Mechanized.MODID, "zinc_ore"), RenderLayerEnum.CUTOUT);
		ALLOY_FURNACE_ENTITY = (BlockEntityType<AlloyFurnaceEntity>) registerBlock(ALLOY_FURNACE,
				new Identifier(Mechanized.MODID, "alloy_furnace"), AlloyFurnaceEntity::new);
		STEAM_BOILER_ENTITY = (BlockEntityType<SteamBoilerEntity>) registerBlock(STEAM_BOILER,
				new Identifier(Mechanized.MODID, "steam_boiler"), SteamBoilerEntity::new);
		STEAM_PIPE_ENTITY = (BlockEntityType<SteamPipeEntity>) registerBlock(STEAM_PIPE,
				new Identifier(Mechanized.MODID, "steam_pipe"), SteamPipeEntity::new);
		STEAM_TANK_ENTITY = (BlockEntityType<SteamTankEntity>) registerBlock(STEAM_TANK,
				new Identifier(Mechanized.MODID, "steam_tank"), SteamTankEntity::new);

		ContainerProviderRegistry.INSTANCE.registerFactory(ALLOY_FURNACE_CONTAINER,
				(syncId, id, player, buf) -> new AlloyFurnaceContainer(syncId, player.inventory,
						BlockContext.create(player.world, buf.readBlockPos())));
		ContainerProviderRegistry.INSTANCE.registerFactory(STEAM_BOILER_CONTAINER,
				(syncId, id, player, buf) -> new SteamBoilerContainer(syncId, player.inventory,
						BlockContext.create(player.world, buf.readBlockPos())));
		ContainerProviderRegistry.INSTANCE.registerFactory(STEAM_TANK_CONTAINER,
				(syncId, id, player, buf) -> new SteamTankContainer(syncId, player.inventory,
						BlockContext.create(player.world, buf.readBlockPos())));

		registerItem(COPPER_INGOT, new Identifier(Mechanized.MODID, "copper_ingot"));
		registerItem(ZINC_INGOT, new Identifier(Mechanized.MODID, "zinc_ingot"));
		registerItem(BRASS_INGOT, new Identifier(Mechanized.MODID, "brass_ingot"));
		registerItem(DEBUG_TOOL, new Identifier(Mechanized.MODID, "debug_tool"));
	}

	public static void registerClient() {
		ScreenProviderRegistry.INSTANCE.registerFactory(ALLOY_FURNACE_CONTAINER,
				(syncId, identifier, player, buf) -> new AlloyFurnaceScreen(new AlloyFurnaceContainer(syncId,
						player.inventory, BlockContext.create(player.world, buf.readBlockPos())), player));
		ScreenProviderRegistry.INSTANCE.registerFactory(STEAM_BOILER_CONTAINER,
				(syncId, identifier, player, buf) -> new SteamBoilerScreen(new SteamBoilerContainer(syncId,
						player.inventory, BlockContext.create(player.world, buf.readBlockPos())), player));
		ScreenProviderRegistry.INSTANCE.registerFactory(STEAM_TANK_CONTAINER,
				(syncId, identifier, player, buf) -> new SteamTankScreen(new SteamTankContainer(syncId,
						player.inventory, BlockContext.create(player.world, buf.readBlockPos())), player));
	}

	private static void registerBlock(Block block, Identifier id, RenderLayerEnum layer) {
		registerBlock(block, id);
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
			setRenderLayer(block, layer);
	}

	private static void registerBlock(Block block, Identifier id) {
		Registry.register(Registry.BLOCK, id, block);
		Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(Mechanized.ITEM_GROUP)));

	}

	private static BlockEntityType<? extends BlockEntity> registerBlock(Block block, Identifier id,
			Supplier<BlockEntity> be) {
		registerBlock(block, id);
		return Registry.register(Registry.BLOCK_ENTITY_TYPE, id, BlockEntityType.Builder.create(be, block).build(null));
	}

	/*
	 * private static BlockEntityType<? extends BlockEntity> registerBlock(Block
	 * block, Identifier id, Supplier<BlockEntity> be, RenderLayerEnum layer) {
	 * registerBlock(block, id, layer); return
	 * Registry.register(Registry.BLOCK_ENTITY_TYPE, id,
	 * BlockEntityType.Builder.create(be, block).build(null)); }
	 */

	@Environment(EnvType.CLIENT)
	private static void setRenderLayer(Block block, RenderLayerEnum layer) {
		switch (layer) {
		case CUTOUT:
			BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
			break;
		default:
			break;
		}
	}

	private static void registerItem(Item item, Identifier id) {
		Registry.register(Registry.ITEM, id, item);
	}

	public static enum RenderLayerEnum {
		CUTOUT;
	}
}
