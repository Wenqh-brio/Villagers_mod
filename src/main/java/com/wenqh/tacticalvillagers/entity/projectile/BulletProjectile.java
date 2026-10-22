package com.wenqh.tacticalvillagers.entity.projectile;

import com.wenqh.tacticalvillagers.registry.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class BulletProjectile extends ThrowableItemProjectile {
    public BulletProjectile(EntityType<? extends BulletProjectile> type, Level level) { super(type, level); }
    public BulletProjectile(Level level, LivingEntity shooter) { super(ModEntities.BULLET.get(), shooter, level); }
    @Override protected Item getDefaultItem() { return Items.IRON_NUGGET; }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (result.getEntity() instanceof LivingEntity target) target.hurt(damageSources().thrown(this, this.getOwner()), 5.0F);
    }
}