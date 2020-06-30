package net.snakefangox.mechanized.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.items.SteamExoSuit;
import net.snakefangox.mechanized.items.Upgradable;
import net.snakefangox.mechanized.steam.SteamItem;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntity {

	protected PlayerMixin(EntityType<? extends LivingEntity> type, World world) {
		super(type, world);
	}

	@Inject(method = "jump", at = @At("RETURN"))
	public void onJump(CallbackInfo info) {
		ItemStack stack = ((PlayerEntity) (Object) this).getEquippedStack(EquipmentSlot.FEET);
		if (!((PlayerEntity) (Object) this).world.isClient && stack.getItem() == MRegister.STEAM_EXOSUIT_BOOTS
				&& ((SteamItem) MRegister.STEAM_EXOSUIT_BOOTS).getPressure(stack) > 0.01
				&& (Integer) ((Upgradable) MRegister.STEAM_EXOSUIT_BOOTS).getUpgradeFromStack(stack)[2] > 0) {
			((PlayerEntity) (Object) this).addVelocity(0, SteamExoSuit.UPGRADE_JUMP_SPEED, 0);
			((PlayerEntity) (Object) this).velocityModified = true;
			((SteamItem) MRegister.STEAM_EXOSUIT_BOOTS).removeSteam(stack, SteamExoSuit.STEAM_USE_PER_SEC * 2);
			((ServerWorld) ((PlayerEntity) (Object) this).world).playSound(null,
					((PlayerEntity) (Object) this).getX(), ((PlayerEntity) (Object) this).getY(),
					((PlayerEntity) (Object) this).getZ(), MRegister.STEAM_HIT, SoundCategory.PLAYERS, 0.25f, 1);
		}
	}

	@Inject(method = "handleFallDamage", at = @At("HEAD"))
	public void onFall(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Boolean> info) {
		ItemStack stack = ((PlayerEntity) (Object) this).getEquippedStack(EquipmentSlot.FEET);
		if (!((PlayerEntity) (Object) this).world.isClient && stack.getItem() == MRegister.STEAM_EXOSUIT_BOOTS
				&& ((SteamItem) MRegister.STEAM_EXOSUIT_BOOTS).getPressure(stack) > 0.01) {
			((PlayerEntity) (Object) this)
					.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 3, 1, false, false));
		}
	}

}
