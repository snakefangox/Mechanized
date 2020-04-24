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
	private static final int PUMP_COST = (int) (Steam.UNIT * 0.005);
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
			Direction dir = getCachedState().get(HorizontalFacingBlock.FACING);
			SteamUtil.directionalEqualizeSteam(world, this, pos, dir, dir);
			SteamUtil.directionalEqualizeSteam(world, this, pos, Direction.UP, Direction.UP);
		}
		if (world.getTime() % 10 == 0) {
			if (getPressure(Direction.UP) >= 0.1 && tank
					.attemptInsertion(FluidWorldUtil.drain(world, pos.offset(Direction.DOWN), Simulation.SIMULATE),
							Simulation.SIMULATE)
					.isEmpty()) {
				tank.attemptInsertion(FluidWorldUtil.drain(world, pos.offset(Direction.DOWN), Simulation.ACTION),
						Simulation.ACTION);
				removeSteam(Direction.UP, PUMP_COST);
			}
			if (!tank.getTank(0).get().isEmpty()) {
				FluidInsertable outTank = FluidAttributes.INSERTABLE
						.getAllFromNeighbour(this, getCachedState().get(HorizontalFacingBlock.FACING)).getFirstOrNull();
				if (outTank != null)
					tank.attemptInsertion(
							outTank.attemptInsertion(tank.attemptAnyExtraction(TANK_CAPACITY, Simulation.ACTION),
									Simulation.ACTION),
							Simulation.ACTION);
			}
		}
	}

	@Override
	public boolean canPipeConnect(Direction dir) {
		return dir == Direction.UP || dir == getCachedState().get(HorizontalFacingBlock.FACING);
	}

	public <T> T getWorldAttribute(CombinableAttribute<T> attr, Direction dir) {
		return attr.get(getWorld(), getPos().offset(dir), SearchOptions.inDirection(dir));
	}

	@Override
	public int getSteamAmount(Direction dir) {
		if (dir == Direction.UP || dir == getCachedState().get(HorizontalFacingBlock.FACING))
			return steamAmount;
		return 0;
	}

	@Override
	public int getMaxSteamAmount(Direction dir) {
		if (dir == Direction.UP || dir == getCachedState().get(HorizontalFacingBlock.FACING))
			return STEAM_CAPACITY;
		return 0;
	}

	@Override
	public void setSteamAmount(Direction dir, int amount) {
		if (dir == Direction.UP || dir == getCachedState().get(HorizontalFacingBlock.FACING))
			steamAmount = amount;
	}

	@Override
	public int getPressurePSBForReadout(Direction dir) {
		return getPressurePSB(Direction.UP);
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		steamAmount = tag.getInt("steamAmount");
		tank.fromTag(tag.getCompound("tank"));
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putInt("steamAmount", steamAmount);
		tag.put("tank", tank.toTag());
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
