package com.yourname.tacticalvillagers.entity.projectile;

import com.yourname.tacticalvillagers.registry.ModEntities;
import com.yourname.tacticalvillagers.registry.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class GrenadeProjectile extends ThrowableItemProjectile {
    public GrenadeProjectile(EntityType<? extends GrenadeProjectile> type, Level level) { super(type, level); }
    public GrenadeProjectile(Level level, LivingEntity shooter) { super(ModEntities.GRENADE.get(), shooter, level); }

    @Override protected Item getDefaultItem() { return ModItems.GRENADE.get(); }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            // 简单的爆炸逻辑
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 2.0F, Level.ExplosionInteraction.NONE);
            this.discard();
        }
    }
}