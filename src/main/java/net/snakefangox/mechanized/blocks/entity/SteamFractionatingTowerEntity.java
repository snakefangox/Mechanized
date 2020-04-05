package net.snakefangox.mechanized.blocks.entity;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.blocks.SteamFractionatingTower;
import net.snakefangox.mechanized.steam.Steam;
import net.snakefangox.mechanized.steam.SteamUtil;

public class SteamFractionatingTowerEntity extends BlockEntity implements Steam, Tickable {

	public static final int STEAM_CAPACITY = Steam.UNIT;
	public static final int STEAM_USE_PER_OP = Steam.UNIT / 10;
	public static final int RESOURCES_DIAMETER = 6;
	public static final int RESOURCE_AMOUNT_MAX = 3;
	public static final float RESOURCE_CHANCE = 0.1f;
	private static final Random RAND = new Random();
	private static final Item[] POSSIBLE_ITEMS = new Item[] { Item.fromBlock(Blocks.IRON_ORE) };

	private int steamAmount = 0;
	private int level = -1;

	public SteamFractionatingTowerEntity() {
		super(MRegister.STEAM_FRACTIONATING_TOWER_ENTITY);
	}

	@Override
	public void tick() {
		if (world.isClient)
			return;
		if (world.getTime() % 5 == 0 && level == 2) {
			SteamUtil.directionalEqualizeSteam(world, this, pos, Direction.UP, Direction.UP);
		}
		if (level == -1) {
			level = getCachedState().get(SteamFractionatingTower.LEVEL);
		}
		if (world.getTime() % 20 == 0 && level == 0) {
			BlockEntity be = world.getBlockEntity(pos.offset(Direction.UP, 2));
			if (be instanceof SteamFractionatingTowerEntity && ((SteamFractionatingTowerEntity) be).getPressure(Direction.DOWN) == 1) {
				System.out.println("Go");
				if (checkIsPositionValid()) {
					removeSteam(Direction.UP, STEAM_USE_PER_OP);
					doGenerate();
				}
			}
		}
	}

	private void doGenerate() {
		if (true/* RAND.nextFloat() > 0.9 */) {
			System.out.println("Spawn");
			int amount = RAND.nextInt(RESOURCE_AMOUNT_MAX);
			for (int i = 0; i < amount; i++) {
				ItemEntity spawn = new ItemEntity(world, pos.getX() + (RESOURCES_DIAMETER * (0.5 - RAND.nextFloat())),
						pos.getY(), pos.getZ(), new ItemStack(POSSIBLE_ITEMS[RAND.nextInt(POSSIBLE_ITEMS.length)]));
				world.spawnEntity(spawn);
			}
		}
	}

	private boolean checkIsPositionValid() {
		boolean foundBedrock = false;
		BlockPos.Mutable searchPos = new BlockPos.Mutable(pos);
		for (int i = pos.getY(); i >= 0; i--) {
			searchPos.set(searchPos.getX(), searchPos.getY() - 1, searchPos.getZ());
			Block atPos = world.getBlockState(searchPos).getBlock();
			System.out.println(atPos);
			if (atPos == Blocks.BEDROCK) {
				foundBedrock = true;
				break;
				// I know that isAir is the better check, I don't want to break on cave air and
				// I never get to void air
			} else if (atPos == Blocks.AIR) {
				break;
			}
		}
		return foundBedrock;
	}

	@Override
	public int getSteamAmount(Direction dir) {
		if (dir == Direction.UP && level == 2)
			return steamAmount;
		return 0;
	}

	@Override
	public int getMaxSteamAmount(Direction dir) {
		if (dir == Direction.UP && level == 2)
			return STEAM_CAPACITY;
		return 0;
	}

	@Override
	public void setSteamAmount(Direction dir, int amount) {
		if (dir == Direction.UP && level == 2)
			steamAmount = amount;
	}

	@Override
	public boolean canPipeConnect(Direction dir) {
		return level == 2 && dir == Direction.UP;
	}

	@Override
	public int getPressurePSBForReadout(Direction dir) {
		if (level == 2) {
			return getPressurePSBForReadout(Direction.UP);
		} else {
			BlockEntity be = world.getBlockEntity(pos.offset(Direction.UP, 2 - level));
			if (be instanceof SteamFractionatingTowerEntity)
				return (2 - level + 1) * ((SteamFractionatingTowerEntity) be).getPressurePSBForReadout(dir);
		}
		return 0;
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		steamAmount = tag.getInt("steamAmount");
		level = tag.getInt("level");
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putInt("steamAmount", steamAmount);
		tag.putInt("level", level);
		return super.toTag(tag);
	}

}
