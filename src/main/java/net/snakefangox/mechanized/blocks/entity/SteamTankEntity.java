package net.snakefangox.mechanized.blocks.entity;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.util.math.Direction;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.steam.Steam;

public class SteamTankEntity extends AbstractSteamEntity implements PropertyDelegateHolder {

	private static final int STEAM_CAPACITY = Steam.UNIT * 16;

	public SteamTankEntity() {
		super(MRegister.STEAM_TANK_ENTITY);
	}
	
	@Override
	public int getMaxSteamAmount(Direction dir) {
		return STEAM_CAPACITY;
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
