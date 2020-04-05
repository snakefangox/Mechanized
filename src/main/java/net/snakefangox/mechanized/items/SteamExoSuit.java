package net.snakefangox.mechanized.items;

import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.steam.Steam;
import net.snakefangox.mechanized.steam.SteamItem;
import net.snakefangox.mechanized.tools.ExoSuitMat;

public class SteamExoSuit extends ArmorItem implements SteamItem, Upgradable {

	public static final int STEAM_CAPACITY = Steam.UNIT;
	public static final int STEAM_USE_PER_TICK = 1;

	public SteamExoSuit(EquipmentSlot slot, Settings settings) {
		super(ExoSuitMat.MAT, slot, settings);
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return false;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			if (slot < 4 && player.inventory.getArmorStack(slot) == stack) {
				switch (slot) {
				case 0:
					break;
				case 1:
					if (getPressure(stack) > 0)
						player.addStatusEffect(new StatusEffectInstance(MRegister.EXOSUIT_SPEED, 3, 1, false, false));
					break;
				case 2:
					if (getPressure(stack) > 0)
						player.addStatusEffect(
								new StatusEffectInstance(MRegister.EXOSUIT_STRENGTH, 3, 1, false, false));
					break;

				case 3:
					break;

				default:
					break;
				}
				if(world.getTime() % 20 == 0)
					removeSteam(stack, STEAM_USE_PER_TICK);
				int armorAmp = 0;
				for (int i = 0; i < player.inventory.armor.size(); i++) {
					if (player.inventory.getArmorStack(i).getItem() instanceof SteamExoSuit)
						armorAmp += (Integer) ((Upgradable) MRegister.STEAM_EXOSUIT_CHEST)
								.getUpgradeFromStack(player.inventory.getArmorStack(i))[0];
				}
				player.addStatusEffect(new StatusEffectInstance(MRegister.EXOSUIT_PROTECC, 3, armorAmp, false, false));
			}
		}
	}

	@Override
	public boolean isDamageable() {
		return true;
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
		stack.setDamage((int) (stack.getMaxDamage()
				- (stack.getMaxDamage() * (float) ((float) amount / (float) getMaxSteamAmount(stack)))));
	}

	@Override
	public int getMaxSteamAmount(ItemStack stack) {
		return STEAM_CAPACITY + (Integer) getUpgradeFromStack(stack)[1];
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
		int proteccUpgrade = upgrades[0] == Items.DIAMOND_CHESTPLATE ? 1 : 0;
		tag.putInt("steamExtra", steamExtra);
		tag.putInt("protecc", proteccUpgrade);
		return tag;
	}

	@Override
	public Object[] getUpgradeFromStack(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		return new Object[] { tag.getInt("protecc"), tag.getInt("steamExtra") };
	}

	@Override
	public Ingredient validUpgrades(Item item) {
		return Ingredient.ofItems(Items.DIAMOND_CHESTPLATE, MRegister.STEAM_CANISTER);
	}

	@Override
	public int upgradeSlotCount() {
		return 1;
	}

	@Override
	public Item[] getItemsFromStack(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		return new Item[] { tag.getInt("protecc") == 1 ? Items.DIAMOND_CHESTPLATE
				: tag.getInt("steamExtra") > 0 ? MRegister.STEAM_CANISTER : null };
	}
}
