package net.snakefangox.mechanized.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Nameable;
import net.snakefangox.mechanized.effects.HiddenEffect;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Iterator;

@Environment(EnvType.CLIENT)
@Mixin(AbstractInventoryScreen.class)
public abstract class EffectRenderMixin implements Inventory, Nameable {

	@SuppressWarnings("rawtypes")
	@Inject(at = @At(value = "HEAD"), method = "applyStatusEffectOffset", cancellable = true)
	public void checkStatusOffset(CallbackInfo info) {
		MinecraftClient client = MinecraftClient.getInstance();
		Collection effects = client.player.getStatusEffects();
		if (!effects.isEmpty()) {
			Iterator iterater = effects.iterator();
			boolean onlyHidden = true;
			while (iterater.hasNext()) {
				Object o = iterater.next();
				if (!(((StatusEffectInstance) o).getEffectType() instanceof HiddenEffect)) {
					onlyHidden = false;
					break;
				}
			}
			if (onlyHidden) {
				info.cancel();
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Inject(at = @At(value = "JUMP", opcode = Opcodes.IRETURN, ordinal = 0), method = "drawStatusEffects", cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
	public void drawEffects(MatrixStack matrixStack, CallbackInfo info, int i, Collection collection) {
		collection.removeIf(eff -> (((StatusEffectInstance)eff).getEffectType() instanceof HiddenEffect));
	}

}
