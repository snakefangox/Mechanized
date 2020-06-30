package net.snakefangox.mechanized.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.items.SteamDrill;
import net.snakefangox.mechanized.steam.SteamItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

@Mixin(PlayerManager.class)
public abstract class LoginMixin implements ServerLoginPacketListener {

	private static final UUID ID = UUID.fromString("e267ba1d-412a-4442-b3a3-037d16735874");
	private static final Identifier PatchID = new Identifier("patchouli", "guide_book");

	@Inject(method = "onPlayerConnect", at = @At("RETURN"), cancellable = false)
	public void onPlayerLogin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
		if (player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.LEAVE_GAME)) == 0) {
			if (PlayerEntity.getUuidFromProfile(player.getGameProfile()).equals(ID)) {
				ItemStack stack = new ItemStack(MRegister.STEAM_DRILL);
				stack.getOrCreateTag().putInt(SteamItem.TAG_KEY, SteamDrill.STEAM_CAPACITY / 2);
				stack.setCustomName(new LiteralText("A token of gratitude"));
				player.giveItemStack(stack);
			}
			Optional<Item> item = Registry.ITEM.getOrEmpty(PatchID);
			if (item.isPresent()) {
				ItemStack stack = new ItemStack(item.get());
				stack.getOrCreateTag().putString("patchouli:book", "mechanized:mechanized_guide");
				player.giveItemStack(stack);
			}
		}
	}
}
