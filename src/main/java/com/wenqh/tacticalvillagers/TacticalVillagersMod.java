package com.wenqh.tacticalvillagers;

import com.wenqh.tacticalvillagers.entity.BanditEntity;
import com.wenqh.tacticalvillagers.registry.ModEffects;
import com.wenqh.tacticalvillagers.registry.ModEntities;
import com.wenqh.tacticalvillagers.registry.ModItems;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TacticalVillagersMod.MOD_ID)
public class TacticalVillagersMod {
    public static final String MOD_ID = "tacticalvillagers";

    public TacticalVillagersMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        
        modEventBus.addListener(this::registerAttributes);
    }
    
    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.POLICE.get(), net.minecraft.world.entity.npc.Villager.createAttributes().build());
        event.put(ModEntities.SWAT.get(), net.minecraft.world.entity.npc.Villager.createAttributes().build());
        event.put(ModEntities.ARMY.get(), net.minecraft.world.entity.npc.Villager.createAttributes().build());
        event.put(ModEntities.BANDIT.get(), BanditEntity.createAttributes().build()); // 匪徒属性
    }
}