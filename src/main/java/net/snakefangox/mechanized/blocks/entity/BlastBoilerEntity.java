package net.snakefangox.mechanized.blocks.entity;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.steam.Steam;

public class BlastBoilerEntity extends AbstractSteamBoilerEntity {

	public static final int STEAM_PER_OP = 4;
	public static final FluidAmount FLUID_PER_OP = FluidAmount.of(STEAM_PER_OP * 2, Steam.UNIT);
	public static final int FUEL_PER_OP = (STEAM_PER_OP * 2) - 1;
	
	public BlastBoilerEntity() {
		super(MRegister.BLAST_BOILER_ENTITY);
	}

	@Override
	protected FluidAmount fluidPerOp() {
		return FLUID_PER_OP;
	}

	@Override
	protected int steamPerOp() {
		return STEAM_PER_OP;
	}

	@Override
	protected void extractTick() {
		fuel = Math.max(0, fuel - FUEL_PER_OP);
	}
}
