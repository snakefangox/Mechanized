package net.snakefangox.mechanized.blocks.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.blocks.SteamFractionatingTower;
import net.snakefangox.mechanized.steam.Steam;
import net.snakefangox.mechanized.steam.SteamUtil;

import java.util.List;
import java.util.Random;

public class SteamFractionatingTowerEntity extends AbstractSteamEntity {

	public static final int STEAM_CAPACITY = Steam.UNIT;
	public static final int STEAM_USE_PER_OP = Steam.UNIT / 4;
	public static final int MINE_DIAMETER = 16;
	public static final int MINE_AMOUNT_MAX = 18;
	public static final float BREAK_CHANCE = 0.1F;
	private static final Random RAND = new Random();

	private int level = -1;
	private int stoneLevel = 0;

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
			if (be instanceof SteamFractionatingTowerEntity
					&& ((SteamFractionatingTowerEntity) be).getPressure(Direction.UP) >= 1) {
				if (checkIsPositionValid()) {
					((SteamFractionatingTowerEntity) be).removeSteam(Direction.UP, STEAM_USE_PER_OP);
					((ServerWorld) world).playSound(null, pos, MRegister.STEAM_INJECT, SoundCategory.BLOCKS, 4, 1);
					for (int i = 0; i < MINE_AMOUNT_MAX; i++) {
						if (RAND.nextBoolean()) {
							doMine();
						}
					}
				}
			}
		}
	}

	private void doMine() {
		BlockPos minePos = new BlockPos(pos.getX() + ((MINE_DIAMETER / 2) - RAND.nextInt(MINE_DIAMETER)),
				pos.getY() - (RAND.nextInt(pos.getY()) + stoneLevel),
				pos.getZ() + ((MINE_DIAMETER / 2) - RAND.nextInt(MINE_DIAMETER)));
		BlockState blockState = world.getBlockState(minePos);
		float hardness = blockState.getHardness(getWorld(), minePos);
		if (!world.isAir(minePos)
				&& (blockState.getMaterial() == Material.STONE || blockState.getMaterial() == Material.AGGREGATE)
				&& hardness < 100 && hardness >= 0) {
			BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? world.getBlockEntity(minePos) : null;
			Block.dropStacks(blockState, world, minePos, blockEntity, null, ItemStack.EMPTY);
			if (RAND.nextFloat() > BREAK_CHANCE && pos.getX() != minePos.getX() && pos.getZ() != minePos.getZ())
				world.breakBlock(minePos, false);
			List<ItemEntity> items = world.getEntities(ItemEntity.class, new Box(minePos), null);
			items.forEach(ie -> {
				double x = ie.getX();
				double z = ie.getZ();
				ie.refreshPositionAndAngles(x, pos.getY(), z, ie.yaw, ie.pitch);
				ie.addVelocity(0, 0.2, 0);
				ie.velocityModified = true;
			});
		}
	}

	private boolean checkIsPositionValid() {
		boolean foundBedrock = false;
		BlockPos.Mutable searchPos = new BlockPos.Mutable();
		searchPos.add(pos);
		for (int i = pos.getY(); i >= 0; i--) {
			searchPos.set(searchPos.getX(), searchPos.getY() - 1, searchPos.getZ());
			BlockState state = world.getBlockState(searchPos);
			Block atPos = state.getBlock();
			if (state.getMaterial() == Material.STONE)
				stoneLevel = i;
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
		return world.getBlockState(getPos()).get(SteamFractionatingTower.LEVEL) == 2 && dir == Direction.UP;
	}

	@Override
	public int getPressurePSBForReadout(Direction dir) {
		if (level == 2) {
			return getPressurePSB(Direction.UP);
		} else {
			BlockEntity be = world.getBlockEntity(pos.offset(Direction.UP, 2 - level));
			if (be instanceof SteamFractionatingTowerEntity)
				return (2 - level + 1) * ((SteamFractionatingTowerEntity) be).getPressurePSBForReadout(dir);
		}
		return 0;
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		level = tag.getInt("level");
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putInt("level", level);
		return super.toTag(tag);
	}

}
