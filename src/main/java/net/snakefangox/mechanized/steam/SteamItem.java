package net.snakefangox.mechanized.steam;

import net.minecraft.item.ItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.snakefangox.mechanized.Mechanized;

public interface SteamItem {

	/**
	 * Good place to sort out steam mechanics. 1 unit of steam = 1600
	 */
	public static final int UNIT = Steam.UNIT;

	/**
	 * The key to store steam amount
	 */
	public static final String TAG_KEY = Mechanized.MODID + "steam";

	public static final ItemPropertyGetter DIS_STEAM_PROPERTY_GETTER = (stack, world, entity) -> {
		return ((SteamItem)stack.getItem()).getPressure(stack) == 1 ? 1.0F : 0.0F;
	};

	public static final ItemPropertyGetter STEAM_PROPERTY_GETTER = (stack, world, entity) -> {
		return MathHelper.clamp((float) ((SteamItem)stack.getItem()).getPressure(stack), 0.0F, 1.0F);
	};

	/**
	 * Returns the amount of steam in the tank linked to the given itemstack
	 * 
	 * @param stack
	 * @return
	 */
	public int getSteamAmount(ItemStack stack);

	/**
	 * Sets the amount of steam in the tank linked to the given itemstack
	 * 
	 * @param stack
	 */
	public void setSteamAmount(ItemStack stack, int amount);

	/**
	 * Returns the maximum amount of steam in the tank linked to the given itemstack
	 * 
	 * @param stack
	 * @return
	 */
	public int getMaxSteamAmount(ItemStack stack);

	/**
	 * If a pipe can visually connect to a given side
	 * 
	 * @param stack
	 * @return
	 */
	default public boolean canPipeConnect(ItemStack stack) {
		return true;
	}

	/**
	 * Gets the pressure for the steam tank linked to the given side
	 * 
	 * @param stack
	 * @return the pressure as a float from 0 to 1
	 */
	default public float getPressure(ItemStack stack) {
		return getSteamAmount(stack) / (float) getMaxSteamAmount(stack);
	}

	/**
	 * Gets the pressure for the steam tank linked to the given side in PSB
	 * 
	 * @param stack
	 * @return the pressure as an int from 0 to 100
	 */
	default public int getPressurePSB(ItemStack stack) {
		return (int) (getPressure(stack) * 100.0f);
	}

	/**
	 * Calls add or remove as needed
	 * 
	 * @param stack
	 * @param amount
	 * @return the amount of steam that was accepted or removed
	 */
	default public int addOrRemoveSteam(ItemStack stack, int amount) {
		if (amount > 0) {
			return addSteam(stack, amount);
		} else {
			return removeSteam(stack, amount * -1) * -1;
		}
	}

	/**
	 * Adds steam to the tank linked to the given itemstack
	 * 
	 * @param stack
	 * @param amount
	 * @return the amount of steam that was accepted
	 */
	default public int addSteam(ItemStack stack, int amount) {
		if (amount + getSteamAmount(stack) <= getMaxSteamAmount(stack)) {
			setSteamAmount(stack, amount + getSteamAmount(stack));
			return amount;
		} else {
			int acceptedAmount = getMaxSteamAmount(stack) - getSteamAmount(stack);
			setSteamAmount(stack, getMaxSteamAmount(stack));
			return acceptedAmount;
		}
	}

	/**
	 * Removes steam from the tank linked to the given itemstack
	 * 
	 * @param stack
	 * @param amount
	 * @return the amount of steam that was removed
	 */
	default public int removeSteam(ItemStack stack, int amount) {
		if (getSteamAmount(stack) - amount < 0) {
			int removedAmount = getSteamAmount(stack);
			setSteamAmount(stack, 0);
			return removedAmount;
		} else {
			setSteamAmount(stack, getSteamAmount(stack) - amount);
			return amount;
		}
	}
}
