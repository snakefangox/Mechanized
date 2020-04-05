package net.snakefangox.mechanized.items;

import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.steam.SteamItem;
import net.snakefangox.mechanized.tools.SteamToolMaterial;

public class SteamDrill extends PickaxeItem implements SteamItem, Upgradable {

	public static final int STEAM_CAPACITY = SteamItem.UNIT;
	public static final int STEAM_PER_BLOCK = 1;

	public SteamDrill(Settings settings) {
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
	public float getMiningSpeed(ItemStack stack, BlockState state) {
		return getPressure(stack) * getMaterial().getMiningSpeed();
	}

	@Override
	public boolean isDamageable() {
		return true;
	}

	public boolean isEffectiveOn(BlockState state, ItemStack stack) {
		Block block = state.getBlock();
		int i = this.getMaterial().getMiningLevel() + (int) getUpgradeFromStack(stack)[0];
		if (block == Blocks.OBSIDIAN) {
			return i == 3;
		} else if (block != Blocks.DIAMOND_BLOCK && block != Blocks.DIAMOND_ORE && block != Blocks.EMERALD_ORE
				&& block != Blocks.EMERALD_BLOCK && block != Blocks.GOLD_BLOCK && block != Blocks.GOLD_ORE
				&& block != Blocks.REDSTONE_ORE) {
			if (block != Blocks.IRON_BLOCK && block != Blocks.IRON_ORE && block != Blocks.LAPIS_BLOCK
					&& block != Blocks.LAPIS_ORE) {
				Material material = state.getMaterial();
				return material == Material.STONE || material == Material.METAL || material == Material.ANVIL;
			} else {
				return i >= 1;
			}
		} else {
			return i >= 2;
		}
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
		stack.setDamage((int) (stack.getMaxDamage() - (stack.getMaxDamage() * (float)((float)amount / (float) getMaxSteamAmount(stack)))));
	}

	@Override
	public int getMaxSteamAmount(ItemStack stack) {
		return STEAM_CAPACITY + (Integer) getUpgradeFromStack(stack)[1];
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
		int miningUpgrade = upgrades[0] == Items.DIAMOND ? 1 : 0;
		int steamExtra = upgrades[0] == MRegister.STEAM_CANISTER ? SteamCanister.STEAM_CAPACITY : 0;
		tag.putInt("miningUpgrade", miningUpgrade);
		tag.putInt("steamExtra", steamExtra);
		return tag;
	}

	@Override
	public Object[] getUpgradeFromStack(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		return new Object[] { tag.getInt("miningUpgrade"), tag.getInt("steamExtra") };
	}

	@Override
	public Ingredient validUpgrades(Item item) {
		return Ingredient.ofItems(Items.DIAMOND, MRegister.STEAM_CANISTER);
	}

	@Override
	public int upgradeSlotCount() {
		return 1;
	}

	@Override
	public Item[] getItemsFromStack(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.getInt("miningUpgrade") == 1) {
			return new Item[] { Items.DIAMOND };
		} else if (tag.getInt("steamExtra") > 0) {
			return new Item[] { MRegister.STEAM_CANISTER };
		} else {
			return new Item[] { null };
		}
	}
}
