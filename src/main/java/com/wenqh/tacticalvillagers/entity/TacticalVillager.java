package com.wenqh.tacticalvillagers.entity;

import com.wenqh.tacticalvillagers.entity.projectile.BulletProjectile;
import com.wenqh.tacticalvillagers.entity.projectile.TaserProjectile;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class TacticalVillager extends Villager {
    private int aiTimer = 0;
    private int chatCooldown = 0;
    private int reloadTimer = 0;

    // 卓越前线和各附属包的命名空间
    private static final String[] VEHICLE_NAMESPACES = {
            "superbwarfare", "ashvehicle", "vvp", "mscp", "dr_reforge", "fcp"
    };

    public TacticalVillager(EntityType<? extends Villager> type, Level level) {
        super(type, level);
        this.setCanPickUpLoot(true);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (!this.level().isClientSide()) {
            if (--aiTimer <= 0) { aiTimer = 20; evaluateSimpleAI(); }
            if (chatCooldown > 0) chatCooldown--;
        }
    }

    private boolean isSuperbWarfareItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id.getNamespace().equals("superbwarfare") || id.getPath().contains("grenade");
    }

    private boolean isSuperbVehicle(Entity entity) {
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        for (String ns : VEHICLE_NAMESPACES) {
            if (id.getNamespace().equals(ns)) return true;
        }
        return false;
    }

    private void evaluateSimpleAI() {
        if (!this.isPassenger() && tryRideVehicle()) return;

        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            ItemStack mainHand = this.getMainHandItem();
            if (isSuperbWarfareItem(mainHand)) {
                if (this.distanceToSqr(target) > 49.0D && mainHand.getItem().toString().contains("grenade")) {
                    throwGrenade(target);
                } else {
                    handleCombat(target);
                }
            }
            return;
        }

        if (chatCooldown <= 0 && this.getRandom().nextFloat() < 0.05F) {
            Player nearestPlayer = this.level().getNearestPlayer(this, 10.0D);
            if (nearestPlayer != null) broadcastChatMessage(nearestPlayer);
        }
    }

    private boolean tryRideVehicle() {
        List<Entity> vehicles = this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(15.0D),
                e -> isSuperbVehicle(e) && e.getPassengers().isEmpty());
        if (!vehicles.isEmpty()) {
            this.startRiding(vehicles.get(0));
            this.playSound(SoundEvents.VILLAGER_YES, 1.0F, 1.0F);
            return true;
        }
        return false;
    }

    private void handleCombat(LivingEntity target) {
        this.getLookControl().setLookAt(target, 30.0F, 30.0F);
        if (this.reloadTimer > 0) {
            this.reloadTimer--;
            if (this.reloadTimer == 0) this.playSound(SoundEvents.CROSSBOW_LOADING_END, 1.0F, 1.0F); 
            return; 
        }

        if (this.tickCount % 20 == 0) {
            if (this instanceof PoliceVillager) {
                TaserProjectile taser = new TaserProjectile(this.level(), this);
                taser.shoot(target.getX() - this.getX(), target.getEyeY() - this.getEyeY(), target.getZ() - this.getZ(), 2.0F, 1.0F);
                this.level().addFreshEntity(taser);
            } else {
                BulletProjectile bullet = new BulletProjectile(this.level(), this);
                bullet.shoot(target.getX() - this.getX(), target.getEyeY() - this.getEyeY(), target.getZ() - this.getZ(), 3.0F, 0.5F);
                this.level().addFreshEntity(bullet);
            }
            this.swing(InteractionHand.MAIN_HAND, true);
            this.reloadTimer = 40; 
            this.playSound(SoundEvents.CROSSBOW_LOADING_START, 1.0F, 1.0F);
        }
    }

    private void throwGrenade(LivingEntity target) {
        this.swing(InteractionHand.MAIN_HAND, true);
        net.minecraft.world.entity.projectile.Snowball grenade = new net.minecraft.world.entity.projectile.Snowball(this.level(), this);
        grenade.shoot(target.getX() - this.getX(), target.getEyeY() - this.getEyeY(), target.getZ() - this.getZ(), 1.2F, 1.0F);
        this.level().addFreshEntity(grenade);
        this.level().explode(this, target.getX(), target.getY(), target.getZ(), 2.0F, Level.ExplosionInteraction.MOB);
        this.playSound(SoundEvents.SNOWBALL_THROW, 1.0F, 1.0F);
    }

    private void broadcastChatMessage(Player player) {
        String[] messages = {"各单位注意，保持警戒。", "有什么需要协助的吗？", "今天也是个巡逻的好天气。"};
        String msg = messages[this.getRandom().nextInt(messages.length)];
        player.sendSystemMessage(Component.literal("村民: " + msg));
        chatCooldown = 600; 
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (isSuperbWarfareItem(itemstack)) {
            ItemStack copy = itemstack.copy();
            copy.setCount(1);
            this.setItemInHand(InteractionHand.MAIN_HAND, copy);
            if (!player.isCreative()) itemstack.shrink(1);
            this.playSound(SoundEvents.VILLAGER_YES, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }
}