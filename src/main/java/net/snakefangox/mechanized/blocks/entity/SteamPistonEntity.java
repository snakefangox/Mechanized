package net.snakefangox.mechanized.blocks.entity;

import java.util.List;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.blocks.Breaker;
import net.snakefangox.mechanized.entities.FlyingBlockEntity;
import net.snakefangox.mechanized.steam.Steam;
import net.snakefangox.mechanized.steam.SteamUtil;
import net.snakefangox.mechanized.tools.InventoryTools;

public class SteamPistonEntity extends BlockEntity implements Steam, Tickable, PropertyDelegateHolder {

	private static final int STEAM_CAPACITY = Steam.UNIT;
	private static final int COST_PER_OP = (int) (Steam.UNIT * 0.1);
	private static final float VELOCITY_PER_PSB = 0.05f;

	int steamAmount = 0;
	int retractTimer = 0;
	boolean extended = false;
	boolean powered = false;

	public SteamPistonEntity() {
		super(MRegister.STEAM_PISTON_ENTITY);
	}

	@Override
	public void tick() {
		if (world.isClient)
			return;
		if (world.getTime() % 5 == 0) {
			SteamUtil.equalizeSteam(world, this, pos, null);
		}
		if (retractTimer > 0) {
			--retractTimer;
			if (retractTimer == 0)
				extendOrRetract(false);
		}
	}

	public void updateSignal(int power) {
		if (power > 0 && !powered) {
			powered = true;
			onSignal();
		} else if (power == 0 && powered) {
			powered = false;
		}
	}

	public void onSignal() {
		if (retractTimer > 0)
			return;
		Direction dir = world.getBlockState(pos).get(Properties.FACING);
		BlockPos off = pos.offset(dir);
		if (!world.isAir(off)) {
			BlockState state = world.getBlockState(off);
			boolean isAnvil = state.getBlock() instanceof AnvilBlock;
			BlockEntity be = world.getBlockEntity(off);
			FlyingBlockEntity fallingBlock = new FlyingBlockEntity(world, off.getX() + 0.5, off.getY() + 0.5,
					off.getZ() + 0.5, state);

			if (be != null) {
				CompoundTag tag = new CompoundTag();
				be.toTag(tag);
				fallingBlock.blockEntityData = tag.copy();
				if (tag.contains("Items") && tag.get("Items") instanceof ListTag)
					InventoryTools.toTagIncEmpty(tag,
							DefaultedList.ofSize(tag.getList("Items", 10).size(), ItemStack.EMPTY), true);
				be.fromTag(tag);
			}
			if (isAnvil)
				fallingBlock.setHurtEntities(true);
			((ServerWorld) world).spawnEntity(fallingBlock);
		}
		List<Entity> entities = world.getEntities(null, new Box(off));
		entities.forEach(ent -> {
			ent.addVelocity(dir.getOffsetX() * (getPressurePSB(null) * VELOCITY_PER_PSB),
					dir.getOffsetY() * (getPressurePSB(null) * VELOCITY_PER_PSB),
					dir.getOffsetZ() * (getPressurePSB(null) * VELOCITY_PER_PSB));
			ent.velocityModified = true;
		});
		removeSteam(null, COST_PER_OP);
		extendOrRetract(true);
		retractTimer = 15;
		((ServerWorld) world).playSound(null, pos, MRegister.STEAM_HIT, SoundCategory.BLOCKS, 1, 0.5f);
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
		retractTimer = tag.getInt("retractTimer");
		extended = tag.getBoolean("extended");
		powered = tag.getBoolean("powered");
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putInt("steamAmount", steamAmount);
		tag.putInt("retractTimer", retractTimer);
		tag.putBoolean("extended", extended);
		tag.putBoolean("powered", powered);
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
