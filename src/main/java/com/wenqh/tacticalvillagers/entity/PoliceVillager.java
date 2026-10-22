package com.wenqh.tacticalvillagers.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;

public class PoliceVillager extends TacticalVillager {
    public PoliceVillager(EntityType<? extends Villager> type, Level level) { super(type, level); }
}