package net.snakefangox.mechanized.blocks.entity;

import net.minecraft.util.math.Direction;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.steam.Steam;

public class SteamSourceEntity extends AbstractSteamEntity {

	private static final int STEAM_CAPACITY = Steam.UNIT * 16;

	public SteamSourceEntity() {
		super(MRegister.STEAM_SOURCE_ENTITY);
	}

	@Override
	public int getSteamAmount(Direction dir) {
		return STEAM_CAPACITY;
	}

	@Override
	public int getMaxSteamAmount(Direction dir) {
		return STEAM_CAPACITY;
	}

	@Override
	public void setSteamAmount(Direction dir, int amount) {
	}
}
