package net.snakefangox.mechanized.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.steam.Steam;
import net.snakefangox.mechanized.steam.SteamItem;
import net.snakefangox.mechanized.tools.ExoSuitMat;

import java.util.List;

public class SteamExoSuit extends ArmorItem implements SteamItem, Upgradable {

	public static final int STEAM_CAPACITY = Steam.UNIT;
	public static final int STEAM_USE_PER_SEC = 1;
	public static final double UPGRADE_JUMP_SPEED = 0.3;
	public static final double AREA_SIGHT_RANGE = 8;

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
			if (slot < 4 && player.inventory.armor.get(slot) == stack) {
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
					if (getPressure(stack) > 0) {
						if ((Integer) getUpgradeFromStack(stack)[3] > 0)
							player.addStatusEffect(
									new StatusEffectInstance(StatusEffects.NIGHT_VISION, 3, 1, false, false));
						if ((Integer) getUpgradeFromStack(stack)[4] > 0 && world.getTime() % 20 == 0) {
							List<Entity> entities = world.getEntities(player,
									new Box(player.getX() + AREA_SIGHT_RANGE, player.getY() + AREA_SIGHT_RANGE,
											player.getZ() + AREA_SIGHT_RANGE, player.getX() - AREA_SIGHT_RANGE,
											player.getY() - AREA_SIGHT_RANGE, player.getZ() - AREA_SIGHT_RANGE));
							entities.forEach(ent -> {
								if (ent instanceof LivingEntity)
									((LivingEntity) ent).addStatusEffect(
											new StatusEffectInstance(StatusEffects.GLOWING, 25, 1, false, false));
							});
						}
					}
					break;

				default:
					break;
				}
				if (world.getTime() % 20 == 0)
					removeSteam(stack, STEAM_USE_PER_SEC);
				int armorAmp = 0;
				for (int i = 0; i < player.inventory.armor.size(); i++) {
					if (player.inventory.armor.get(slot).getItem() instanceof SteamExoSuit)
						armorAmp += (Integer) ((Upgradable) MRegister.STEAM_EXOSUIT_CHEST)
								.getUpgradeFromStack(player.inventory.armor.get(i))[0];
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
		int steamExtra = 0;
		int proteccUpgrade = 0;
		int steamJump = 0;
		int nightVision = 0;
		int areaVision = 0;
		for (int i = 0; i < upgrades.length; i++) {
			steamExtra = upgrades[i] == MRegister.STEAM_CANISTER ? SteamCanister.STEAM_CAPACITY : steamExtra;
			proteccUpgrade = upgrades[i] == Items.DIAMOND_CHESTPLATE ? 1 : proteccUpgrade;
			steamJump = upgrades[i] == Item.fromBlock(MRegister.STEAM_PISTON) ? 1 : steamJump;
			nightVision = upgrades[i] == Items.ENDER_EYE ? 1 : nightVision;
			areaVision = upgrades[i] == Items.SPECTRAL_ARROW ? 1 : areaVision;
		}
		tag.putInt("steamExtra", steamExtra);
		tag.putInt("protecc", proteccUpgrade);
		tag.putInt("steamJump", steamJump);
		tag.putInt("nightVision", nightVision);
		tag.putInt("areaVision", areaVision);
		return tag;
	}

	@Override
	public Object[] getUpgradeFromStack(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		return new Object[] { tag.getInt("protecc"), tag.getInt("steamExtra"), tag.getInt("steamJump"),
				tag.getInt("nightVision"), tag.getInt("areaVision") };
	}

	@Override
	public Ingredient validUpgrades(Item item) {
		Item[] items = null;
		if (item == MRegister.STEAM_EXOSUIT_BOOTS) {
			items = new Item[] { MRegister.STEAM_PISTON.asItem(), Items.DIAMOND_CHESTPLATE, MRegister.STEAM_CANISTER };
		} else if (item == MRegister.STEAM_EXOSUIT_HELMET) {
			items = new Item[] { Items.SPECTRAL_ARROW, Items.ENDER_EYE, Items.DIAMOND_CHESTPLATE,
					MRegister.STEAM_CANISTER };
		} else {
			items = new Item[] { Items.DIAMOND_CHESTPLATE, MRegister.STEAM_CANISTER };
		}
		return Ingredient.ofItems(items);
	}

	@Override
	public int upgradeSlotCount(Item item) {
		if (item == MRegister.STEAM_EXOSUIT_HELMET)
			return 2;
		return 1;
	}

	@Override
	public Item[] getItemsFromStack(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		Item[] items = new Item[upgradeSlotCount(stack.getItem())];
		int i = 0;
		if (tag.getInt("protecc") > 0)
			items[i++] = Items.DIAMOND_CHESTPLATE;
		if (tag.getInt("steamExtra") > 0)
			items[i++] = MRegister.STEAM_CANISTER;
		if (tag.getInt("steamJump") > 0)
			items[i++] = Item.fromBlock(MRegister.STEAM_PISTON);
		if (tag.getInt("nightVision") > 0)
			items[i++] = Items.ENDER_EYE;
		if (tag.getInt("areaVision") > 0)
			items[i++] = Items.SPECTRAL_ARROW;
		return items;
	}
}
