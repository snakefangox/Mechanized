package net.snakefangox.mechanized.steam;

import net.minecraft.util.math.Direction;

public interface Steam {

	/**
	 * Good place to sort out steam mechanics. 1 unit of steam = 1600
	 */
	public static final int UNIT = 1600;

	/**
	 * Returns the amount of steam in the tank linked to the given facing
	 * 
	 * @param dir
	 * @return
	 */
	public int getSteamAmount(Direction dir);

	/**
	 * Sets the amount of steam in the tank linked to the given facing
	 * 
	 * @param dir
	 */
	public void setSteamAmount(Direction dir, int amount);

	/**
	 * Returns the maximum amount of steam in the tank linked to the given facing
	 * 
	 * @param dir
	 * @return
	 */
	public int getMaxSteamAmount(Direction dir);

	/**
	 * Adds steam to the tank linked to the given facing
	 * 
	 * @param dir
	 * @param amount
	 * @return the amount of steam that was accepted
	 */
	default public int addSteam(Direction dir, int amount) {
		if (amount + getSteamAmount(dir) <= getMaxSteamAmount(dir)) {
			setSteamAmount(dir, amount + getSteamAmount(dir));
			return amount;
		} else {
			int acceptedAmount = getMaxSteamAmount(dir) - getSteamAmount(dir);
			setSteamAmount(dir, getMaxSteamAmount(dir));
			return acceptedAmount;
		}
	}

	/**
	 * Removes steam from the tank linked to the given facing
	 * 
	 * @param dir
	 * @param amount
	 * @return the amount of steam that was removed
	 */
	default public int removeSteam(Direction dir, int amount) {
		if (getSteamAmount(dir) - amount < 0) {
			int removedAmount = getSteamAmount(dir);
			setSteamAmount(dir, 0);
			return removedAmount;
		} else {
			setSteamAmount(dir, getSteamAmount(dir) - amount);
			return amount;
		}
	}
}
