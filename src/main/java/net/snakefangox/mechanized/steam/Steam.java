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
	 * If a pipe can visually connect to a given side
	 * @param dir
	 * @return
	 */
	default public boolean canPipeConnect(Direction dir) {
		return true;
	}

	/**
	 * Gets the pressure for the steam tank linked to the given side
	 * 
	 * @param dir
	 * @return the pressure as a float from 0 to 1
	 */
	default public float getPressure(Direction dir) {
		return getSteamAmount(dir) / (float) getMaxSteamAmount(dir);
	}
	
	/**
	 * Gets the pressure for the steam tank linked to the given side in PSB
	 * 
	 * @param dir
	 * @return the pressure as an int from 0 to 100
	 */
	default public int getPressurePSB(Direction dir) {
		return (int) (getPressure(dir) * 100.0f);
	}
	
	/**
	 * Calls add or remove as needed
	 * 
	 * @param dir
	 * @param amount
	 * @return the amount of steam that was accepted or removed
	 */
	default public int addOrRemoveSteam(Direction dir, int amount) {
		if(amount > 0) {
			return addSteam(dir, amount);
		}else {
			return removeSteam(dir, amount * -1) * -1;
		}
	}
	
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
