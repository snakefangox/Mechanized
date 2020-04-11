package net.snakefangox.mechanized.blocks.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.steam.Steam;
import net.snakefangox.mechanized.steam.SteamUtil;

public class SteamSourceEntity extends BlockEntity implements Steam, Tickable {

	private static final int STEAM_CAPACITY = Steam.UNIT * 16;

	public SteamSourceEntity() {
		super(MRegister.STEAM_SOURCE_ENTITY);
	}

	@Override
	public void tick() {
		if (world.isClient)
			return;
		if (world.getTime() % 5 == 0) {
			SteamUtil.equalizeSteam(world, this, pos, null);
		}
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
