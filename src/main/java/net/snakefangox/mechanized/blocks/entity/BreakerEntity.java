package net.snakefangox.mechanized.blocks.entity;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.blocks.Breaker;
import net.snakefangox.mechanized.steam.Steam;
import net.snakefangox.mechanized.steam.SteamUtil;

public class BreakerEntity extends BlockEntity implements Steam, PropertyDelegateHolder, Tickable {

	private static final int STEAM_CAPACITY = Steam.UNIT;
	private static final int COST_PER_OP = (int) (Steam.UNIT * 0.05);

	int steamAmount = 0;
	boolean extended = false;

	public BreakerEntity() {
		super(MRegister.BREAKER_ENTITY);
	}

	@Override
	public void tick() {
		if (world.isClient)
			return;
		if (world.getTime() % 5 == 0) {
			SteamUtil.equalizeSteam(world, this, pos, null);
			extendOrRetract(false);
		}
		if (world.getTime() % 20 == 0) {
			BlockPos bp = pos.offset(getCachedState().get(Properties.FACING));
			BlockState bs = world.getBlockState(bp);
			float hardness = bs.getHardness(world, bp);
			if (!bs.isAir() && (hardness >= 1 ? hardness : 1) <= getPressurePSB(null)) {
				world.breakBlock(bp, true);
				removeSteam(null, COST_PER_OP);
				extendOrRetract(true);
			}
		}
	}

	public void extendOrRetract(boolean extend) {
		if (extended != extend) {
			world.setBlockState(pos, getCachedState().with(Breaker.EXTENDED, extend));
			extended = extend;
		}
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
		extended = tag.getBoolean("extended");
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putInt("steamAmount", steamAmount);
		tag.putBoolean("extended", extended);
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
