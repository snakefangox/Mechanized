package net.snakefangox.mechanized.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class ExoEffect extends StatusEffect implements HiddenEffect {

	public ExoEffect() {
		super(StatusEffectType.BENEFICIAL, 0xd0ad34);
	}
}
