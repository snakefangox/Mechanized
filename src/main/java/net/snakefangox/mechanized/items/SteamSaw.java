package net.snakefangox.mechanized.items;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.steam.SteamItem;
import net.snakefangox.mechanized.tools.SteamToolMaterial;

import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;

public class SteamSaw extends AxeItem implements SteamItem, Upgradable, DynamicAttributeTool {

	public static final int STEAM_CAPACITY = SteamItem.UNIT;
	public static final int STEAM_PER_BLOCK = 1;
	public static final UUID DAMAGE_UUID = UUID.fromString("25eba1b7-9f2c-4607-b078-bc683b40cb0c");

	public SteamSaw(Settings settings) {
		super(SteamToolMaterial.INSTANCE, 1, -2.8F, settings);
	}

	@Override
	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
		removeSteam(stack, STEAM_PER_BLOCK);
		return true;
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		removeSteam(stack, STEAM_PER_BLOCK);
		return true;
	}

	@Override
	public float getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
		return getPressure(stack) * 10;
	}

	@Override
	public int getMiningLevel(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
		return getMaterial().getMiningLevel();
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getDynamicModifiers(EquipmentSlot slot, ItemStack stack, LivingEntity user) {
		Multimap<EntityAttribute, EntityAttributeModifier> map = HashMultimap.create(1, 1);
		map.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(DAMAGE_UUID, "saw_damage",
				(Integer) getUpgradeFromStack(stack)[1], EntityAttributeModifier.Operation.ADDITION));
		return map;
	}

	@Override
	public int getSteamAmount(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.contains(SteamItem.TAG_KEY)) {
			return tag.getInt(TAG_KEY);
		} else {
			tag.putInt(TAG_KEY, 0);
			return 0;
		}
	}

	@Override
	public void setSteamAmount(ItemStack stack, int amount) {
		CompoundTag tag = stack.getOrCreateTag();
		tag.putInt(TAG_KEY, amount);
	}

	@Override
	public int getMaxSteamAmount(ItemStack stack) {
		return STEAM_CAPACITY + (Integer) getUpgradeFromStack(stack)[0];
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return false;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		Item[] upgrades = getItemsFromStack(stack);
		if (upgrades.length > 0) {
			tooltip.add(1, new LiteralText("Upgrades:"));
			for (int i = 0; i < upgrades.length; i++) {
				if (upgrades[i] != null) {
					tooltip.add(2 + i, new net.minecraft.text.TranslatableText(upgrades[i].getTranslationKey(stack)));
				} else {
					tooltip.add(2 + i, new LiteralText("Empty"));
				}
			}
		}
	}

	@Override
	public CompoundTag getUpgradeTag(ItemStack stack, Item... upgrades) {
		CompoundTag tag = stack.getOrCreateTag();
		int steamExtra = upgrades[0] == MRegister.STEAM_CANISTER ? SteamCanister.STEAM_CAPACITY : 0;
		tag.putInt("steamExtra", steamExtra);
		int damageExtra = upgrades[0] == Items.IRON_SWORD ? 6 : 0;
		tag.putInt("damageExtra", damageExtra);
		return tag;
	}

	@Override
	public Object[] getUpgradeFromStack(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		return new Object[] {tag.getInt("steamExtra"), tag.getInt("damageExtra")};
	}

	@Override
	public Ingredient validUpgrades(Item item) {
		return Ingredient.ofItems(MRegister.STEAM_CANISTER, Items.IRON_SWORD);
	}

	@Override
	public int upgradeSlotCount(Item item) {
		return 1;
	}

	@Override
	public Item[] getItemsFromStack(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		Item[] items = new Item[upgradeSlotCount(stack.getItem())];
		int i = 0;
		if (tag.getInt("steamExtra") > 0) {
			items[i++] = MRegister.STEAM_CANISTER;
		}
		if (tag.getInt("damageExtra") >= 6) {
			items[i++] = Items.IRON_SWORD;
		}
		return items;
	}
}
