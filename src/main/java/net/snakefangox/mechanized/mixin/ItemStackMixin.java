package net.snakefangox.mechanized.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.items.SteamDrill;

@Mixin(value = ItemStack.class, priority = 999)
public abstract class ItemStackMixin {

	@Shadow
	public abstract Item getItem();

	// Sorry it had to come to this FabricAPI but the way you do the break check
	// doesn't work for me
	@Inject(at = @At("HEAD"), method = "getMiningSpeed", cancellable = true)
	public void getItemBreakSpeed(BlockState state, CallbackInfoReturnable<Float> info) {
		if (getItem() instanceof SteamDrill) {
			info.setReturnValue(MRegister.STEAM_DRILL.getMiningSpeed((ItemStack) (Object) this, state));
			info.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "isEffectiveOn", cancellable = true)
	public void canMineWithUpgrade(BlockState state, CallbackInfoReturnable<Boolean> info) {
		if(getItem() instanceof SteamDrill) {
			info.setReturnValue(((SteamDrill)MRegister.STEAM_DRILL).isEffectiveOn(state, (ItemStack) (Object)this));
			info.cancel();
		}
	}
}
