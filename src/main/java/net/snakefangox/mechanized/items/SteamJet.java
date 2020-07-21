package net.snakefangox.mechanized.items;

import java.util.List;

import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.steam.Steam;
import net.snakefangox.mechanized.steam.SteamItem;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class SteamJet extends Item implements SteamItem, Upgradable {

	private static final Integer STEAM_CAPACITY = Steam.UNIT;

	public SteamJet(Settings settings) {
		super(settings);
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		if (!world.isClient && getPressure(stack) > 0) {
			Vec3d rot = user.getRotationVector().multiply((double) getUpgradeFromStack(stack)[1] + 0.08);
			user.addVelocity(rot.x, rot.y, rot.z);
			user.velocityModified = true;
			removeSteam(stack, 2);
			if (remainingUseTicks % 20 == 0) {
				world.playSound(null, user.getBlockPos(), MRegister.STEAM_ESCAPES, SoundCategory.PLAYERS, 0.1F, 1);
			}
		}
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.SPEAR;
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return getMaxSteamAmount(stack);
	}

	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		if (getPressure(itemStack) > 0) {
			user.setCurrentHand(hand);
			return TypedActionResult.consume(itemStack);
		}
		return TypedActionResult.fail(itemStack);
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
		double speedIncrease = upgrades[0] == MRegister.PRESSURE_VALVE.asItem() ? 0.002 : 0;
		tag.putInt("steamExtra", steamExtra);
		tag.putDouble("speedIncrease", speedIncrease);
		return tag;
	}

	@Override
	public Object[] getUpgradeFromStack(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		return new Object[] {tag.getInt("steamExtra"), tag.getDouble("speedIncrease")};
	}

	@Override
	public Ingredient validUpgrades(Item item) {
		return Ingredient.ofItems(MRegister.STEAM_CANISTER, MRegister.PRESSURE_VALVE);
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
		if (tag.getDouble("speedIncrease") > 0) {
			items[i++] = MRegister.PRESSURE_VALVE.asItem();
		}
		return items;
	}
}
