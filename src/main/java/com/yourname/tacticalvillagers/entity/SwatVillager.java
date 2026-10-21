package com.yourname.tacticalvillagers.entity;
import net.minecraft.world.entity.EntityType; 
import net.minecraft.world.entity.npc.Villager; 
import net.minecraft.world.level.Level;
public class SwatVillager extends TacticalVillager { 
    public SwatVillager(EntityType<? extends Villager> type, Level level) { super(type, level); } 
}