package net.snakefangox.mechanized.blocks.entity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.fabricmc.fabric.impl.content.registry.FuelRegistryImpl;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.Direction;
import net.snakefangox.mechanized.blocks.SteamBoiler;
import net.snakefangox.mechanized.parts.StandardInventory;
import net.snakefangox.mechanized.steam.Steam;
import net.snakefangox.mechanized.steam.SteamUtil;
import net.snakefangox.mechanized.tools.InventoryTools;

public abstract class AbstractSteamBoilerEntity extends AbstractSteamEntity implements StandardInventory, PropertyDelegateHolder {

	private static final FluidAmount TANK_CAPACITY = FluidAmount.ofWhole(3);
	private static final int STEAM_TANK_CAPACITY = 3 * Steam.UNIT;
	public final SimpleFixedFluidInv waterTank;

	DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
	int fuel = 0;
	int maxFuel = 0;

	public AbstractSteamBoilerEntity(BlockEntityType<?> type) {
		super(type);
		waterTank = new SimpleFixedFluidInv(1, TANK_CAPACITY) {
			@Override
			public boolean isFluidValidForTank(int tank, FluidKey fluid) {
				return fluid.equals(FluidKeys.WATER);
			}
		};
	}
	
	protected abstract FluidAmount fluidPerOp();
	protected abstract int steamPerOp();
	protected abstract void extractTick();

	@Override
	public void tick() {
		if (world.isClient)
			return;
		if (waterTank.getTank(0).get().getAmount_F().isGreaterThanOrEqual(fluidPerOp()) && fuel > 0) {
			waterTank.getTank(0).attemptAnyExtraction(fluidPerOp(), Simulation.ACTION);
			steamAmount = Math.min(STEAM_TANK_CAPACITY, steamAmount + steamPerOp());
			extractTick();
		}
		int oldFuel = fuel;
		if (fuel > 0) {
			--fuel;
		}
		if (fuel == 0 && !waterTank.getTank(0).get().isEmpty()) {
			Integer fuelAmount = FuelRegistryImpl.INSTANCE.get(inventory.get(0).getItem());
			if (fuelAmount != null) {
				InventoryTools.decrementFuel(this, 0);
				fuel = fuelAmount;
				maxFuel = fuelAmount;
				updateBlockState();
			} else if (oldFuel != fuel) {
				updateBlockState();
			}
		} else if (oldFuel != fuel && fuel == 0) {
			updateBlockState();
		}
		if (world.getTime() % 5 == 0) {
			SteamUtil.equalizeSteam(world, this, pos, null);
		}
	}

	private void updateBlockState() {
		if (world.getBlockState(pos).get(SteamBoiler.LIT) != fuel > 0)
			world.setBlockState(pos, getCachedState().with(SteamBoiler.LIT, fuel > 0));
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		Inventories.toTag(tag, inventory);
		tag.putInt("fuel", fuel);
		tag.putInt("maxFuel", maxFuel);
		tag.put("tank", waterTank.toTag());
		return super.toTag(tag);
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		Inventories.fromTag(tag, inventory);
		fuel = tag.getInt("fuel");
		maxFuel = tag.getInt("maxFuel");
		waterTank.fromTag(tag.getCompound("tank"));
	}

	@Override
	public DefaultedList<ItemStack> getItems() {
		return inventory;
	}

	PropertyDelegate propdel = new PropertyDelegate() {

		@Override
		public int size() {
			return 6;
		}

		@Override
		public void set(int index, int value) {
			switch (index) {
			case 0:
				fuel = value;
				break;
			case 1:
				maxFuel = value;
				break;
			case 2:
				waterTank.setInvFluid(0, new FluidVolume(FluidKeys.WATER, FluidAmount.of1620(value)) {
				}, Simulation.ACTION);
				break;
			case 3:
				break;
			case 4:
				steamAmount = value;
				break;
			case 5:
				break;
			}
		}

		@Override
		public int get(int index) {
			switch (index) {
			case 0:
				return fuel;
			case 1:
				return maxFuel;
			case 2:
				return waterTank.getInvFluid(0).getAmount_F().as1620();
			case 3:
				return TANK_CAPACITY.as1620();
			case 4:
				return steamAmount;
			case 5:
				return STEAM_TANK_CAPACITY;
			}
			return 0;
		}
	};

	@Override
	public PropertyDelegate getPropertyDelegate() {
		return propdel;
	}

	@Override
	public int getSteamAmount(net.minecraft.util.math.Direction dir) {
		return steamAmount;
	}

	@Override
	public int getMaxSteamAmount(net.minecraft.util.math.Direction dir) {
		return STEAM_TANK_CAPACITY;
	}

	@Override
	public void setSteamAmount(Direction dir, int amount) {
		steamAmount = amount;
	}

}
