package net.snakefangox.mechanized.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.util.Tickable;
import net.snakefangox.mechanized.mixininterfaces.FurnaceInterface;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class FurnaceMixin extends LockableContainerBlockEntity implements SidedInventory, RecipeUnlocker, RecipeInputProvider, Tickable, FurnaceInterface {

	@Shadow
	private int burnTime;
	
	@Shadow
	private int fuelTime;
	
	protected FurnaceMixin(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}
	
	public void addFuelTimeMech(int time) {
		burnTime += time;
		fuelTime = burnTime;
	}
	
}
