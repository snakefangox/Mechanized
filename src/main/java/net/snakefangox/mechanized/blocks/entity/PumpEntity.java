package net.snakefangox.mechanized.blocks.entity;

import alexiil.mc.lib.attributes.CombinableAttribute;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.world.FluidWorldUtil;
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.steam.Steam;
import net.snakefangox.mechanized.steam.SteamUtil;

public class PumpEntity extends BlockEntity implements Steam, Tickable, PropertyDelegateHolder {

	private static final int STEAM_CAPACITY = Steam.UNIT;
	private static final int PUMP_COST = (int) (Steam.UNIT * 0.01);
	int steamAmount = 0;

	private static final FluidAmount TANK_CAPACITY = FluidAmount.ofWhole(1);
	public final SimpleFixedFluidInv tank;

	public PumpEntity() {
		super(MRegister.PUMP_ENTITY);
		tank = new SimpleFixedFluidInv(1, TANK_CAPACITY);
	}

	@Override
	public void tick() {
		if (world.isClient)
			return;
		if (world.getTime() % 5 == 0) {
			SteamUtil.equalizeSteam(world, this, pos, null);
		}
		if (world.getTime() % 10 == 0) {
			if (getPressure(null) >= 0.1
					&& tank.attemptInsertion(FluidWorldUtil.drain(world, pos.offset(Direction.DOWN), Simulation.SIMULATE), Simulation.SIMULATE)
							.isEmpty()) {
				tank.attemptInsertion(FluidWorldUtil.drain(world, pos.offset(Direction.DOWN), Simulation.ACTION), Simulation.ACTION);
				removeSteam(null, PUMP_COST);
			}
			if (!tank.getTank(0).get().isEmpty()) {
				FluidInsertable outTank = FluidAttributes.INSERTABLE.getAllFromNeighbour(this, getCachedState().get(HorizontalFacingBlock.FACING)).getFirstOrNull();
				tank.attemptInsertion(outTank.attemptInsertion(tank.attemptAnyExtraction(TANK_CAPACITY, Simulation.ACTION), Simulation.ACTION), Simulation.ACTION);
			}
		}
	}

	public <T> T getWorldAttribute(CombinableAttribute<T> attr, Direction dir) {
		return attr.get(getWorld(), getPos().offset(dir), SearchOptions.inDirection(dir));
	}

	@Override
	public int getSteamAmount(Direction dir) {
		return steamAmount;
	}

	@Override
	public int getMaxSteamAmount(Direction dir) {
		return STEAM_CAPACITY;
	}

	@Override
	public void setSteamAmount(Direction dir, int amount) {
		steamAmount = amount;
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		steamAmount = tag.getInt("steamAmount");
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putInt("steamAmount", steamAmount);
		return super.toTag(tag);
	}

	PropertyDelegate propdel = new PropertyDelegate() {

		@Override
		public int size() {
			return 2;
		}

		@Override
		public void set(int index, int value) {
			switch (index) {
			case 0:
				steamAmount = value;
				break;
			}
		}

		@Override
		public int get(int index) {
			switch (index) {
			case 0:
				return steamAmount;
			case 1:
				return STEAM_CAPACITY;
			}
			return 0;
		}
	};

	@Override
	public PropertyDelegate getPropertyDelegate() {
		return propdel;
	}
}
