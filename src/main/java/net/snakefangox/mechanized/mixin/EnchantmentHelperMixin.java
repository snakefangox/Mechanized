package net.snakefangox.mechanized.mixin;

import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.steam.Steam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

	@Shadow
	static int getEquipmentLevel(Enchantment enchantment, LivingEntity entity) {
		return 0;
	}

	@Inject(method = "getKnockback(Lnet/minecraft/entity/LivingEntity;)I", at = @At("HEAD"), cancellable = true)
	private static void getKnockback(LivingEntity entity, CallbackInfoReturnable<Integer> info) {
		if (entity instanceof PlayerEntity && entity.getEquippedStack(EquipmentSlot.CHEST).getItem() == MRegister.STEAM_EXOSUIT_CHEST) {
			ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);
			if ((int) MRegister.STEAM_EXOSUIT_CHEST.getUpgradeFromStack(chest)[5] > 0 && MRegister.STEAM_EXOSUIT_CHEST.getPressure(chest) > 0.1) {
				entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
								SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, entity.getSoundCategory(), 2.0F, 1.0F);
				entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
								MRegister.STEAM_HIT, entity.getSoundCategory(), 1.0F, 1.0F);
				MRegister.STEAM_EXOSUIT_CHEST.removeSteam(chest, Steam.UNIT / 40);
				info.setReturnValue(getEquipmentLevel(Enchantments.KNOCKBACK, entity) + 5);
				info.cancel();
			}
		}
	}
}
