package net.snakefangox.mechanized.blocks.entity;

import java.util.List;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.mixininterfaces.FurnaceInterface;
import net.snakefangox.mechanized.steam.Steam;

public class SteamCondenserEntity extends AbstractSteamEntity {

	public static final int STEAM_MAX = Steam.UNIT;
	private static final int CONDENSE_PER_TICK = 10;
	private static final FluidAmount TANK_CAPACITY = FluidAmount.ofWhole(1);
	public final SimpleFixedFluidInv tank;

	public SteamCondenserEntity() {
		super(MRegister.STEAM_CONDENSER_ENTITY);
		tank = new SimpleFixedFluidInv(1, TANK_CAPACITY);
	}

	@Override
	public int getMaxSteamAmount(Direction dir) {
		return STEAM_MAX;
	}

	@Override
	public void tick() {
		super.tick();
		if(getSteamAmount(null) > 0) {
			int amountCondensed = removeSteam(null, CONDENSE_PER_TICK);
			if (amountCondensed > 0) {
				BlockEntity be = world.getBlockEntity(pos.offset(Direction.UP));
				if (be instanceof AbstractFurnaceBlockEntity) {
					((FurnaceInterface)be).addFuelTimeMech(amountCondensed);
				}else {
					if (world.getBlockState(pos.offset(Direction.UP)).isAir()) {
						List<Entity> entities = world.getEntities(null, new Box(pos.up()));
						entities.forEach(e -> {
							if (e instanceof LivingEntity) {
								((LivingEntity)e).damage(DamageSource.IN_FIRE, 1);
							}
						});
					}
				}
				tank.attemptInsertion(new FluidVolume(FluidKeys.WATER, FluidAmount.BUCKET) {}, Simulation.ACTION);
			}
		}
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		tank.fromTag(tag.getCompound("tank"));
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.put("tank", tank.toTag());
		return super.toTag(tag);
	}

}
