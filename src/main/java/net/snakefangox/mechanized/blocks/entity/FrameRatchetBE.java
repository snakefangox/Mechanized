package net.snakefangox.mechanized.blocks.entity;

import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.steam.Steam;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.Direction;

public class FrameRatchetBE extends AbstractSteamEntity {

	public FrameRatchetBE() {
		super(MRegister.FRAME_RATCHET_ENTITY);
	}

	@Override
	public int getMaxSteamAmount(Direction dir) {
		return Steam.UNIT;
	}
}
