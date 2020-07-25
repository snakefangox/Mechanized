package net.snakefangox.mechanized;

import java.util.function.ToIntFunction;

import com.google.common.base.Supplier;
import net.snakefangox.mechanized.blocks.AlloyFurnace;
import net.snakefangox.mechanized.blocks.Breaker;
import net.snakefangox.mechanized.blocks.Fan;
import net.snakefangox.mechanized.blocks.FrameRatchet;
import net.snakefangox.mechanized.blocks.Placer;
import net.snakefangox.mechanized.blocks.PressureValve;
import net.snakefangox.mechanized.blocks.Pump;
import net.snakefangox.mechanized.blocks.SteamBoiler;
import net.snakefangox.mechanized.blocks.SteamCharger;
import net.snakefangox.mechanized.blocks.SteamCondenser;
import net.snakefangox.mechanized.blocks.SteamFractionatingTower;
import net.snakefangox.mechanized.blocks.SteamPipe;
import net.snakefangox.mechanized.blocks.SteamPiston;
import net.snakefangox.mechanized.blocks.SteamSource;
import net.snakefangox.mechanized.blocks.SteamTank;
import net.snakefangox.mechanized.blocks.UpgradeTable;
import net.snakefangox.mechanized.blocks.entity.AlloyFurnaceEntity;
import net.snakefangox.mechanized.blocks.entity.BasicBoilerEntity;
import net.snakefangox.mechanized.blocks.entity.BlastBoilerEntity;
import net.snakefangox.mechanized.blocks.entity.BreakerEntity;
import net.snakefangox.mechanized.blocks.entity.FanEntity;
import net.snakefangox.mechanized.blocks.entity.FrameRatchetBE;
import net.snakefangox.mechanized.blocks.entity.PlacerEntity;
import net.snakefangox.mechanized.blocks.entity.PressureValveEntity;
import net.snakefangox.mechanized.blocks.entity.PumpEntity;
import net.snakefangox.mechanized.blocks.entity.SteamChargerEntity;
import net.snakefangox.mechanized.blocks.entity.SteamCondenserEntity;
import net.snakefangox.mechanized.blocks.entity.SteamFractionatingTowerEntity;
import net.snakefangox.mechanized.blocks.entity.SteamPipeEntity;
import net.snakefangox.mechanized.blocks.entity.SteamPistonEntity;
import net.snakefangox.mechanized.blocks.entity.SteamSourceEntity;
import net.snakefangox.mechanized.blocks.entity.SteamTankEntity;
import net.snakefangox.mechanized.effects.ExoEffect;
import net.snakefangox.mechanized.entities.FlyingBlockEntity;
import net.snakefangox.mechanized.gui.AlloyFurnaceContainer;
import net.snakefangox.mechanized.gui.PlacerContainer;
import net.snakefangox.mechanized.gui.PressureValveContainer;
import net.snakefangox.mechanized.gui.SteamBoilerContainer;
import net.snakefangox.mechanized.gui.UpgradeTableContainer;
import net.snakefangox.mechanized.items.PressureGauge;
import net.snakefangox.mechanized.items.SteamCanister;
import net.snakefangox.mechanized.items.SteamDrill;
import net.snakefangox.mechanized.items.SteamExoSuit;
import net.snakefangox.mechanized.items.SteamJet;
import net.snakefangox.mechanized.items.SteamSaw;
import net.snakefangox.mechanized.recipes.AlloyRecipe;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;

public class MRegister {

	// Blocks
	public static final Block COPPER_ORE = new Block(FabricBlockSettings.of(Material.STONE).hardness(5).resistance(3)
			.breakByTool(FabricToolTags.PICKAXES, 1).requiresTool());
	public static final Block ZINC_ORE = new Block(FabricBlockSettings.of(Material.STONE).hardness(5).resistance(3)
			.breakByTool(FabricToolTags.PICKAXES, 1).requiresTool());
	public static final Block ALLOY_FURNACE = new AlloyFurnace(FabricBlockSettings.of(Material.STONE).hardness(4)
			.resistance(3).breakByTool(FabricToolTags.PICKAXES).nonOpaque().lightLevel(createLightLevelFromBlockState(13)));
	public static final Block BASIC_BOILER = new SteamBoiler(FabricBlockSettings.of(Material.METAL).hardness(4)
			.resistance(3).breakByTool(FabricToolTags.PICKAXES).lightLevel(createLightLevelFromBlockState(13)), BasicBoilerEntity::new);
	public static final Block BLAST_BOILER = new SteamBoiler(FabricBlockSettings.of(Material.METAL).hardness(4)
			.resistance(3).breakByTool(FabricToolTags.PICKAXES).lightLevel(createLightLevelFromBlockState(13)), BlastBoilerEntity::new);
	public static final Block STEAM_PIPE = new SteamPipe(FabricBlockSettings.of(Material.METAL).hardness(3)
			.resistance(3).breakByTool(FabricToolTags.PICKAXES));
	public static final Block STEAM_TANK = new SteamTank(FabricBlockSettings.of(Material.METAL).hardness(4)
			.resistance(3).breakByTool(FabricToolTags.PICKAXES));
	public static final Block PUMP = new Pump(FabricBlockSettings.of(Material.METAL).hardness(4).resistance(3)
			.breakByTool(FabricToolTags.PICKAXES));
	public static final Block BREAKER = new Breaker(FabricBlockSettings.of(Material.METAL).hardness(4).resistance(3)
			.breakByTool(FabricToolTags.PICKAXES));
	public static final Block PLACER = new Placer(FabricBlockSettings.of(Material.METAL).hardness(4).resistance(3)
			.breakByTool(FabricToolTags.PICKAXES));
	public static final Block STEAM_PISTON = new SteamPiston(FabricBlockSettings.of(Material.METAL).hardness(4)
			.resistance(3).breakByTool(FabricToolTags.PICKAXES).nonOpaque());
	public static final Block FAN = new Fan(FabricBlockSettings.of(Material.METAL).hardness(4).resistance(3)
			.breakByTool(FabricToolTags.PICKAXES));
	public static final Block PRESSURE_VALVE = new PressureValve(FabricBlockSettings.of(Material.METAL).hardness(4)
			.resistance(3).breakByTool(FabricToolTags.PICKAXES));
	public static final Block STEAM_CHARGER = new SteamCharger(FabricBlockSettings.of(Material.METAL).hardness(4)
			.resistance(3).breakByTool(FabricToolTags.PICKAXES));
	public static final Block UPGRADE_TABLE = new UpgradeTable(FabricBlockSettings.of(Material.METAL).hardness(4)
			.resistance(3).breakByTool(FabricToolTags.PICKAXES));
	public static final Block STEAM_FRACTIONATING_TOWER = new SteamFractionatingTower(FabricBlockSettings
			.of(Material.METAL).hardness(4).resistance(3).breakByTool(FabricToolTags.PICKAXES).nonOpaque());
	public static final Block BRASS_FRAME = new Block(FabricBlockSettings.of(Material.METAL).hardness(4).resistance(3)
			.breakByTool(FabricToolTags.PICKAXES).nonOpaque());
	public static final Block STEAM_CONDENSER = new SteamCondenser(FabricBlockSettings.of(Material.METAL).hardness(4).resistance(3)
			.breakByTool(FabricToolTags.PICKAXES).nonOpaque());
	public static final Block STEAM_SOURCE = new SteamSource(FabricBlockSettings.of(Material.METAL).hardness(4).resistance(3)
			.breakByTool(FabricToolTags.PICKAXES));
	public static final Block FRAME_RATCHET = new FrameRatchet(FabricBlockSettings.of(Material.METAL).hardness(4).resistance(3)
			.breakByTool(FabricToolTags.PICKAXES).nonOpaque());

	// BlockEntities
	public static BlockEntityType<AlloyFurnaceEntity> ALLOY_FURNACE_ENTITY;
	public static BlockEntityType<BasicBoilerEntity> BASIC_BOILER_ENTITY;
	public static BlockEntityType<BlastBoilerEntity> BLAST_BOILER_ENTITY;
	public static BlockEntityType<SteamPipeEntity> STEAM_PIPE_ENTITY;
	public static BlockEntityType<SteamTankEntity> STEAM_TANK_ENTITY;
	public static BlockEntityType<PumpEntity> PUMP_ENTITY;
	public static BlockEntityType<BreakerEntity> BREAKER_ENTITY;
	public static BlockEntityType<PlacerEntity> PLACER_ENTITY;
	public static BlockEntityType<SteamPistonEntity> STEAM_PISTON_ENTITY;
	public static BlockEntityType<FanEntity> FAN_ENTITY;
	public static BlockEntityType<PressureValveEntity> PRESSURE_VALVE_ENTITY;
	public static BlockEntityType<SteamChargerEntity> STEAM_CHARGER_ENTITY;
	public static BlockEntityType<SteamFractionatingTowerEntity> STEAM_FRACTIONATING_TOWER_ENTITY;
	public static BlockEntityType<SteamSourceEntity> STEAM_SOURCE_ENTITY;
	public static BlockEntityType<SteamCondenserEntity> STEAM_CONDENSER_ENTITY;
	public static BlockEntityType<FrameRatchetBE> FRAME_RATCHET_ENTITY;

	// Containers
	public static ScreenHandlerType<AlloyFurnaceContainer> ALLOY_FURNACE_CONTAINER;
	public static ScreenHandlerType<SteamBoilerContainer> STEAM_BOILER_CONTAINER;
	public static ScreenHandlerType<PressureValveContainer> PRESSURE_VALVE_CONTAINER;
	public static ScreenHandlerType<UpgradeTableContainer> UPGRADE_TABLE_CONTAINER;
	public static ScreenHandlerType<PlacerContainer> PLACER_CONTAINER;

	// Items
	public static final Item COPPER_INGOT = new Item(new Item.Settings().group(Mechanized.ITEM_GROUP));
	public static final Item ZINC_INGOT = new Item(new Item.Settings().group(Mechanized.ITEM_GROUP));
	public static final Item BRASS_INGOT = new Item(new Item.Settings().group(Mechanized.ITEM_GROUP));
	public static final Item FAN_BLADE = new Item(new Item.Settings().group(Mechanized.ITEM_GROUP));
	public static final Item BRASS_GEAR = new Item(new Item.Settings().group(Mechanized.ITEM_GROUP));
	public static final Item PRESSURE_GAUGE = new PressureGauge(
			new Item.Settings().group(Mechanized.ITEM_GROUP).maxCount(1));
	public static final Item STEAM_CANISTER = new SteamCanister(
			new Item.Settings().group(Mechanized.ITEM_GROUP).maxCount(1));
	public static final SteamDrill STEAM_DRILL = new SteamDrill(
			new Item.Settings().group(Mechanized.ITEM_GROUP).maxCount(1));
	public static final SteamSaw STEAM_SAW = new SteamSaw(
			new Item.Settings().group(Mechanized.ITEM_GROUP).maxCount(1));
	public static final Item STEAM_EXOSUIT_HELMET = new SteamExoSuit(EquipmentSlot.HEAD,
			new Item.Settings().group(Mechanized.ITEM_GROUP).maxCount(1));
	public static final Item STEAM_EXOSUIT_CHEST = new SteamExoSuit(EquipmentSlot.CHEST,
			new Item.Settings().group(Mechanized.ITEM_GROUP).maxCount(1));
	public static final Item STEAM_EXOSUIT_LEGS = new SteamExoSuit(EquipmentSlot.LEGS,
			new Item.Settings().group(Mechanized.ITEM_GROUP).maxCount(1));
	public static final Item STEAM_EXOSUIT_BOOTS = new SteamExoSuit(EquipmentSlot.FEET,
			new Item.Settings().group(Mechanized.ITEM_GROUP).maxCount(1));
	public static final Item STEAM_JET = new SteamJet(new Item.Settings().group(Mechanized.ITEM_GROUP).maxCount(1));

	// Status Effects
	public static StatusEffect EXOSUIT_STRENGTH;
	public static StatusEffect EXOSUIT_SPEED;
	public static StatusEffect EXOSUIT_PROTECC;

	// Sound Events
	public static SoundEvent STEAM_INJECT;
	public static SoundEvent STEAM_ESCAPES;
	public static SoundEvent STEAM_HIT;
	public static SoundEvent RATCHET_CLUNK;

	// Entities
	public static final EntityType<FlyingBlockEntity> FLYING_BLOCK = Registry.register(Registry.ENTITY_TYPE,
			new Identifier(Mechanized.MODID, "flying_block"),
			FabricEntityTypeBuilder
					.create(SpawnGroup.MISC, (EntityType.EntityFactory<FlyingBlockEntity>) FlyingBlockEntity::new)
					.size(EntityDimensions.fixed(1, 1)).build());

	public static void registerEverything() {
		if (Mechanized.config.enableOres) {
			registerBlock(COPPER_ORE, new Identifier(Mechanized.MODID, "copper_ore"));
			registerBlock(ZINC_ORE, new Identifier(Mechanized.MODID, "zinc_ore"));
		}
		ALLOY_FURNACE_ENTITY = registerBlock(ALLOY_FURNACE, new Identifier(Mechanized.MODID, "alloy_furnace"),
				AlloyFurnaceEntity::new);
		BASIC_BOILER_ENTITY = registerBlock(BASIC_BOILER, new Identifier(Mechanized.MODID, "steam_boiler"),
				BasicBoilerEntity::new);
		BLAST_BOILER_ENTITY = registerBlock(BLAST_BOILER, new Identifier(Mechanized.MODID, "blast_boiler"),
				BlastBoilerEntity::new);
		STEAM_PIPE_ENTITY = registerBlock(STEAM_PIPE, new Identifier(Mechanized.MODID, "steam_pipe"),
				SteamPipeEntity::new);
		STEAM_TANK_ENTITY = registerBlock(STEAM_TANK, new Identifier(Mechanized.MODID, "steam_tank"),
				SteamTankEntity::new);
		PUMP_ENTITY = registerBlock(PUMP, new Identifier(Mechanized.MODID, "pump"), PumpEntity::new);
		BREAKER_ENTITY = registerBlock(BREAKER, new Identifier(Mechanized.MODID, "breaker"), BreakerEntity::new);
		PLACER_ENTITY = registerBlock(PLACER, new Identifier(Mechanized.MODID, "placer"), PlacerEntity::new);
		STEAM_PISTON_ENTITY = registerBlock(STEAM_PISTON, new Identifier(Mechanized.MODID, "steam_piston"),
				SteamPistonEntity::new);
		FAN_ENTITY = registerBlock(FAN, new Identifier(Mechanized.MODID, "fan"), FanEntity::new);
		PRESSURE_VALVE_ENTITY = registerBlock(PRESSURE_VALVE, new Identifier(Mechanized.MODID, "pressure_valve"),
				PressureValveEntity::new);
		STEAM_CHARGER_ENTITY = registerBlock(STEAM_CHARGER, new Identifier(Mechanized.MODID, "steam_charger"),
				SteamChargerEntity::new);
		registerBlock(UPGRADE_TABLE, new Identifier(Mechanized.MODID, "upgrade_table"));
		STEAM_FRACTIONATING_TOWER_ENTITY = registerBlock(STEAM_FRACTIONATING_TOWER,
				new Identifier(Mechanized.MODID, "steam_fractionating_tower"), SteamFractionatingTowerEntity::new);
		registerBlock(BRASS_FRAME, new Identifier(Mechanized.MODID, "brass_frame"));
		STEAM_SOURCE_ENTITY = registerBlock(STEAM_SOURCE, new Identifier(Mechanized.MODID, "steam_source"),
				SteamSourceEntity::new);
		STEAM_CONDENSER_ENTITY = registerBlock(STEAM_CONDENSER, new Identifier(Mechanized.MODID, "steam_condenser"),
				SteamCondenserEntity::new);
		FRAME_RATCHET_ENTITY = registerBlock(FRAME_RATCHET, new Identifier(Mechanized.MODID, "frame_ratchet"),
				FrameRatchetBE::new);

		if (Mechanized.config.enableIngots) {
			registerItem(COPPER_INGOT, new Identifier(Mechanized.MODID, "copper_ingot"));
			registerItem(ZINC_INGOT, new Identifier(Mechanized.MODID, "zinc_ingot"));
		}
		registerItem(BRASS_INGOT, new Identifier(Mechanized.MODID, "brass_ingot"));
		registerItem(FAN_BLADE, new Identifier(Mechanized.MODID, "fan_blade"));
		registerItem(BRASS_GEAR, new Identifier(Mechanized.MODID, "brass_gear"));
		registerItem(PRESSURE_GAUGE, new Identifier(Mechanized.MODID, "pressure_gauge"));
		registerItem(STEAM_CANISTER, new Identifier(Mechanized.MODID, "steam_canister"));
		registerItem(STEAM_DRILL, new Identifier(Mechanized.MODID, "steam_drill"));
		registerItem(STEAM_SAW, new Identifier(Mechanized.MODID, "steam_saw"));
		registerItem(STEAM_EXOSUIT_HELMET, new Identifier(Mechanized.MODID, "steam_exosuit_helm"));
		registerItem(STEAM_EXOSUIT_CHEST, new Identifier(Mechanized.MODID, "steam_exosuit_chest"));
		registerItem(STEAM_EXOSUIT_LEGS, new Identifier(Mechanized.MODID, "steam_exosuit_legs"));
		registerItem(STEAM_EXOSUIT_BOOTS, new Identifier(Mechanized.MODID, "steam_exosuit_boots"));
		registerItem(STEAM_JET, new Identifier(Mechanized.MODID, "steam_jet"));

		EXOSUIT_STRENGTH = Registry.register(Registry.STATUS_EFFECT,
				new Identifier(Mechanized.MODID, "exosuit_strength"),
				new ExoEffect().addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE,
						"6551312b-037b-4152-8c44-e81e9065bae6", 2, EntityAttributeModifier.Operation.ADDITION));
		EXOSUIT_SPEED = Registry.register(Registry.STATUS_EFFECT, new Identifier(Mechanized.MODID, "exosuit_speed"),
				new ExoEffect().addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
						"d859def7-792c-40c2-881b-7d2703fc5760", 0.15D,
						EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
		EXOSUIT_PROTECC = Registry.register(Registry.STATUS_EFFECT, new Identifier(Mechanized.MODID, "exosuit_protecc"),
				new ExoEffect().addAttributeModifier(EntityAttributes.GENERIC_ARMOR, "1b7e6f75-78d3-4a4d-85af-330e09d2470e", 2,
						EntityAttributeModifier.Operation.ADDITION));

		ALLOY_FURNACE_CONTAINER = ScreenHandlerRegistry.registerExtended(
				new Identifier(Mechanized.MODID, "alloy_furnace"), (syncID, player, buf) ->
						new AlloyFurnaceContainer(syncID, player, ScreenHandlerContext.create(player.player.world, buf.readBlockPos())));
		STEAM_BOILER_CONTAINER = ScreenHandlerRegistry.registerExtended(
				new Identifier(Mechanized.MODID, "steam_boiler"), (syncID, player, buf) ->
						new SteamBoilerContainer(syncID, player, ScreenHandlerContext.create(player.player.world, buf.readBlockPos())));
		PRESSURE_VALVE_CONTAINER = ScreenHandlerRegistry.registerExtended(
				new Identifier(Mechanized.MODID, "pressure_valve"), (syncID, player, buf) ->
						new PressureValveContainer(syncID, player, ScreenHandlerContext.create(player.player.world, buf.readBlockPos())));
		UPGRADE_TABLE_CONTAINER = ScreenHandlerRegistry.registerSimple(
				new Identifier(Mechanized.MODID, "upgrade_table"), (syncID, player) ->
						new UpgradeTableContainer(syncID, player));
		PLACER_CONTAINER = ScreenHandlerRegistry.registerExtended(
				new Identifier(Mechanized.MODID, "placer"), (syncID, player, buf) ->
						new PlacerContainer(syncID, player, ScreenHandlerContext.create(player.player.world, buf.readBlockPos())));

		STEAM_INJECT = registerSoundEvent(new Identifier(Mechanized.MODID, "steam_inject"));
		STEAM_HIT = registerSoundEvent(new Identifier(Mechanized.MODID, "steam_hit"));
		STEAM_ESCAPES = registerSoundEvent(new Identifier(Mechanized.MODID, "steam_escapes"));
		RATCHET_CLUNK = registerSoundEvent(new Identifier(Mechanized.MODID, "ratchet_clunk"));

		Registry.register(Registry.RECIPE_SERIALIZER, AlloyRecipe.ID, AlloyRecipe.AlloyRecipeSerializer.INSTANCE);
		Registry.register(Registry.RECIPE_TYPE, AlloyRecipe.ID, AlloyRecipe.AlloyRecipeType.INSTANCE);

	}

	@Environment(EnvType.CLIENT)
	public static void setRenderLayers() {
		setRenderLayer(PUMP, RenderLayerEnum.CUTOUT);
		if (Mechanized.config.enableOres) {
			setRenderLayer(COPPER_ORE, RenderLayerEnum.CUTOUT);
			setRenderLayer(ZINC_ORE, RenderLayerEnum.CUTOUT);
		}
	}

	private static void registerBlock(Block block, Identifier id) {
		Registry.register(Registry.BLOCK, id, block);
		Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(Mechanized.ITEM_GROUP)));

	}

	private static SoundEvent registerSoundEvent(Identifier id) {
		return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
	}

	@SuppressWarnings("unchecked")
	private static <T extends BlockEntity> BlockEntityType<T> registerBlock(Block block, Identifier id,
																			Supplier<BlockEntity> be) {
		registerBlock(block, id);
		return (BlockEntityType<T>) Registry.register(Registry.BLOCK_ENTITY_TYPE, id,
				BlockEntityType.Builder.create(be, block).build(null));
	}

	private static ToIntFunction<BlockState> createLightLevelFromBlockState(int litLevel) {
		return (blockState) -> {
			return (Boolean) blockState.get(Properties.LIT) ? litLevel : 0;
		};
	}

	@Environment(EnvType.CLIENT)
	private static void setRenderLayer(Block block, RenderLayerEnum layer) {
		switch (layer) {
		case CUTOUT:
			BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
			break;
		case TRANSLUCENT:
			BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
			break;
		default:
			break;
		}
	}

	private static void registerItem(Item item, Identifier id) {
		Registry.register(Registry.ITEM, id, item);
	}

	public enum RenderLayerEnum {
		CUTOUT, TRANSLUCENT;
	}
}
