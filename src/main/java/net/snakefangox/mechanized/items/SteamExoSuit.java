package net.snakefangox.mechanized.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.steam.Steam;
import net.snakefangox.mechanized.steam.SteamItem;
import net.snakefangox.mechanized.tools.ExoSuitMat;

public class SteamExoSuit extends ArmorItem implements SteamItem {

	public static final int STEAM_CAPACITY = Steam.UNIT;

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
			if (slot < 4 && player.inventory.getArmorStack(slot).getItem() instanceof SteamExoSuit) {
				switch (slot) {
				case 0:
					break;
				case 1:
					if(((SteamExoSuit)MRegister.STEAM_EXOSUIT_BOOTS).getPressure(stack) > 0)
						player.addStatusEffect(new StatusEffectInstance(MRegister.EXOSUIT_SPEED, 3, 1, false, false));
					break;
				case 2:
					if(((SteamExoSuit)MRegister.STEAM_EXOSUIT_BOOTS).getPressure(stack) > 0)
						player.addStatusEffect(new StatusEffectInstance(MRegister.EXOSUIT_STRENGTH, 3, 1, false, false));
					break;
					
				case 3:
					break;
					
				default:
					break;
				}
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
		stack.setDamage(STEAM_CAPACITY - amount);
	}

	@Override
	public int getMaxSteamAmount(ItemStack stack) {
		return STEAM_CAPACITY;
	}
}
