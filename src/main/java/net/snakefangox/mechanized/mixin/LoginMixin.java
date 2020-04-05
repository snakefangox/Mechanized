package net.snakefangox.mechanized.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.server.PlayerManager;

@Mixin(PlayerManager.class)
public abstract class LoginMixin implements ServerLoginPacketListener {

	@Inject(method = "createPlayer", at = @At("RETURN"), cancellable = true, locals = LocalCapture.PRINT)
	public void onPlayerInstance(CallbackInfo info) {
		
	}
}
