package com.wenqh.tacticalvillagers.entity;

import com.wenqh.tacticalvillagers.entity.TacticalVillager;
import com.wenqh.tacticalvillagers.entity.projectile.BulletProjectile;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class BanditEntity extends Monster {

    private int attackCooldown = 0;
    private int reloadTimer = 0;

    public BanditEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setCanPickUpLoot(true);
    }

    // 必须注册属性，否则刷怪即崩溃
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TacticalVillager.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;

        // 确保手里有枪，没有就随机发一把
        if (!isSuperbWarfareItem(this.getMainHandItem())) {
            equipDefaultWeapon();
        }

        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            executeCombatAlgorithm(target);
        }
    }

    protected void executeCombatAlgorithm(LivingEntity target) {
        double distSqr = this.distanceToSqr(target);
        this.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (reloadTimer > 0) {
            reloadTimer--;
            this.getNavigation().stop();
            if (reloadTimer == 0) {
                this.playSound(SoundEvents.CROSSBOW_LOADING_END, 1.0F, 1.0F);
            }
            return;
        }

        // 走位逻辑
        if (distSqr < 36.0D) { // 距离小于 6 格，太近了，后退
            // 计算远离目标的方向坐标
            double dx = this.getX() - target.getX();
            double dz = this.getZ() - target.getZ();
            // 归一化并设定后退距离（比如退后 8 格）
            double length = Math.sqrt(dx * dx + dz * dz);
            if (length > 0) {
                double targetX = this.getX() + (dx / length) * 8.0D;
                double targetZ = this.getZ() + (dz / length) * 8.0D;
                this.getNavigation().moveTo(targetX, this.getY(), targetZ, 1.2D);
            }
        } else if (distSqr > 256.0D) { 
            this.getNavigation().moveTo(target, 1.2D); // 太远了，靠近
        } else { 
            this.getNavigation().stop(); // 中距离，站定射击
            tryAttackWithGun(target);
        }
    }

    protected void tryAttackWithGun(LivingEntity target) {
        if (attackCooldown > 0) {
            attackCooldown--;
            return;
        }

        ItemStack gun = this.getMainHandItem();
        if (isSuperbWarfareItem(gun)) {
            BulletProjectile bullet = new BulletProjectile(this.level(), this);
            bullet.shoot(target.getX() - this.getX(), target.getEyeY() - this.getEyeY(), target.getZ() - this.getZ(), 2.5F, 1.0F);
            this.level().addFreshEntity(bullet);
            this.swing(InteractionHand.MAIN_HAND, true);
            
            this.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F);
            this.reloadTimer = 20;
            this.playSound(SoundEvents.CROSSBOW_LOADING_START, 1.0F, 1.0F);
            
            attackCooldown = 10; 
        } else {
            this.doHurtTarget(target);
            attackCooldown = 20;
        }
    }

    protected boolean isSuperbWarfareItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id.getNamespace().equals("superbwarfare") || id.getPath().contains("grenade");
    }

    // 随机获取卓越前线武器
    protected void equipDefaultWeapon() {
        List<Item> availableGuns = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            if (id.getNamespace().equals("superbwarfare")) {
                String path = id.getPath();
                if (!path.contains("ammo") && !path.contains("mag") && !path.contains("bullet") && !path.contains("attachment") && !path.contains("knife") && !path.contains("grenade")) {
                    availableGuns.add(item);
                }
            }
        }

        if (!availableGuns.isEmpty()) {
            Item randomGun = availableGuns.get(this.getRandom().nextInt(availableGuns.size()));
            this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(randomGun));
        } else {
            this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
        }
    }
}