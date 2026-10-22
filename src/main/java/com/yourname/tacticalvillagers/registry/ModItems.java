package com.yourname.tacticalvillagers.registry;

import com.yourname.tacticalvillagers.TacticalVillagersMod;
import com.yourname.tacticalvillagers.item.GrenadeItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TacticalVillagersMod.MOD_ID);

    public static final RegistryObject<Item> TACTICAL_GUN = ITEMS.register("tactical_gun", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> TASER_GUN = ITEMS.register("taser_gun", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GRENADE = ITEMS.register("grenade", () -> new GrenadeItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> POLICE_SPAWN_EGG = ITEMS.register("police_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.POLICE, 0x0000FF, 0xFFFFFF, new Item.Properties()));
    public static final RegistryObject<Item> SWAT_SPAWN_EGG = ITEMS.register("swat_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.SWAT, 0x333333, 0xFFFFFF, new Item.Properties()));
    public static final RegistryObject<Item> ARMY_SPAWN_EGG = ITEMS.register("army_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.ARMY, 0x556B2F, 0xFFFFFF, new Item.Properties()));
}