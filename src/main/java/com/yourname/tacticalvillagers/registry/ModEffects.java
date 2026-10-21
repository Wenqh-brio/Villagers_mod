package com.yourname.tacticalvillagers.registry;

import com.yourname.tacticalvillagers.TacticalVillagersMod;
import com.yourname.tacticalvillagers.effect.ParalysisEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, TacticalVillagersMod.MOD_ID);
    public static final RegistryObject<MobEffect> PARALYSIS = EFFECTS.register("paralysis", ParalysisEffect::new);
}