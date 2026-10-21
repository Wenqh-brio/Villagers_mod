package com.yourname.tacticalvillagers.registry;

import com.yourname.tacticalvillagers.TacticalVillagersMod;
import com.yourname.tacticalvillagers.entity.*;
import com.yourname.tacticalvillagers.entity.projectile.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TacticalVillagersMod.MOD_ID);

    public static final RegistryObject<EntityType<PoliceVillager>> POLICE = ENTITY_TYPES.register("police", () -> EntityType.Builder.of(PoliceVillager::new, MobCategory.CREATURE).sized(0.6F, 1.95F).build("police"));
    public static final RegistryObject<EntityType<SwatVillager>> SWAT = ENTITY_TYPES.register("swat", () -> EntityType.Builder.of(SwatVillager::new, MobCategory.CREATURE).sized(0.6F, 1.95F).build("swat"));
    public static final RegistryObject<EntityType<ArmyVillager>> ARMY = ENTITY_TYPES.register("army", () -> EntityType.Builder.of(ArmyVillager::new, MobCategory.CREATURE).sized(0.6F, 1.95F).build("army"));

    public static final RegistryObject<EntityType<BulletProjectile>> BULLET = ENTITY_TYPES.register("bullet", () -> EntityType.Builder.<BulletProjectile>of(BulletProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("bullet"));
    public static final RegistryObject<EntityType<TaserProjectile>> TASER = ENTITY_TYPES.register("taser", () -> EntityType.Builder.<TaserProjectile>of(TaserProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("taser"));
}