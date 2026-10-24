package com.wenqh.tacticalvillagers.registry;

import com.wenqh.tacticalvillagers.TacticalVillagersMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TacticalVillagersMod.MOD_ID);

    public static final RegistryObject<Item> POLICE_SPAWN_EGG = ITEMS.register("police_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.POLICE, 0x0000FF, 0xFFFFFF, new Item.Properties()));
    public static final RegistryObject<Item> SWAT_SPAWN_EGG = ITEMS.register("swat_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.SWAT, 0x333333, 0xFFFFFF, new Item.Properties()));
    public static final RegistryObject<Item> ARMY_SPAWN_EGG = ITEMS.register("army_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.ARMY, 0x556B2F, 0xFFFFFF, new Item.Properties()));
    
    // 匪徒刷怪蛋
    public static final RegistryObject<Item> BANDIT_SPAWN_EGG = ITEMS.register("bandit_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.BANDIT, 0x8B0000, 0x000000, new Item.Properties()));
}