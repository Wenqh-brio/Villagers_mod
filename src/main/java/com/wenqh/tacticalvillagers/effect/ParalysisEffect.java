package com.wenqh.tacticalvillagers.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class ParalysisEffect extends MobEffect {
    public ParalysisEffect() { super(MobEffectCategory.HARMFUL, 0xCC0000); }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        entity.setDeltaMovement(0, entity.getDeltaMovement().y, 0); 
    }
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) { return true; }
}