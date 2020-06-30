package net.snakefangox.mechanized.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.piston.PistonHandler;
import net.snakefangox.mechanized.MRegister;

@Mixin(PistonHandler.class)
public class PistonHandlerMixin {

	@Inject(at = @At("HEAD"), method = "isBlockSticky", cancellable = true)
	private static void stickyCheck(Block block, CallbackInfoReturnable<Boolean> info) {
		if (block == MRegister.BRASS_FRAME) {
			info.setReturnValue(true);
			info.cancel();
		}
	}
	
}
