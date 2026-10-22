package com.yourname.tacticalvillagers.entity.vehicle;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class TankEntity extends Boat {
    public TankEntity(EntityType<? extends Boat> type, Level level) { super(type, level); }
    @Override public Item getDropItem() { return Items.IRON_BLOCK; }
}