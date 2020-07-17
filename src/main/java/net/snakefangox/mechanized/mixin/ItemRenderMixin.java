package net.snakefangox.mechanized.mixin;

import net.snakefangox.mechanized.steam.SteamItem;
import net.snakefangox.mechanized.tools.RenderHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Mixin(ItemRenderer.class)
@Environment(EnvType.CLIENT)
public class ItemRenderMixin {

	//Adapted (with permission) from https://github.com/ejektaflex/Runik

	@Inject(
			method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At(value = "TAIL"))
	public void onGuiRender(TextRenderer fontRenderer, ItemStack stack, int x, int y, String amountText, CallbackInfo ci) {
		if (stack.getItem() instanceof SteamItem) {
			SteamItem steamItem = (SteamItem) stack.getItem();
			RenderHelper.drawQuad(x + 2, y + 13, 12, 2, 0x000000);
			RenderHelper.drawQuad(x + 2, y + 13, (int) Math.floor(steamItem.getPressure(stack) * 12), 1, 0xffffff);
		}
	}
}
