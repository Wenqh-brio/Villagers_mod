package com.yourname.tacticalvillagers.entity;

import com.yourname.tacticalvillagers.entity.projectile.BulletProjectile;
import com.yourname.tacticalvillagers.entity.projectile.GrenadeProjectile;
import com.yourname.tacticalvillagers.entity.projectile.TaserProjectile;
import com.yourname.tacticalvillagers.entity.vehicle.PlaneEntity;
import com.yourname.tacticalvillagers.entity.vehicle.TankEntity;
import com.yourname.tacticalvillagers.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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

    private void evaluateSimpleAI() {
        // 1. 尝试寻找并乘坐载具 (优先开坦克/飞机)
        if (!this.isPassenger() && tryRideVehicle()) {
            return;
        }

        // 2. 战斗逻辑
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            double distSqr = this.distanceToSqr(target);
            // 距离较远且手里有手榴弹，扔手榴弹
            if (distSqr > 36.0D && this.getRandom().nextInt(100) < 10) {
                throwGrenade(target);
            } else {
                handleCombat(target);
            }
            return;
        }

        // 3. 聊天逻辑
        if (chatCooldown <= 0 && this.getRandom().nextFloat() < 0.05F) {
            Player nearestPlayer = this.level().getNearestPlayer(this, 10.0D);
            if (nearestPlayer != null) broadcastChatMessage(nearestPlayer);
        }
    }

    private boolean tryRideVehicle() {
        // 优先寻找坦克
        List<TankEntity> tanks = this.level().getEntitiesOfClass(TankEntity.class, this.getBoundingBox().inflate(10.0D));
        if (!tanks.isEmpty() && tanks.get(0).getPassengers().isEmpty()) {
            this.startRiding(tanks.get(0));
            return true;
        }
        // 寻找飞机
        List<PlaneEntity> planes = this.level().getEntitiesOfClass(PlaneEntity.class, this.getBoundingBox().inflate(10.0D));
        if (!planes.isEmpty() && planes.get(0).getPassengers().isEmpty()) {
            this.startRiding(planes.get(0));
            return true;
        }
        return false;
    }

    private void handleCombat(LivingEntity target) {
        this.getLookControl().setLookAt(target, 30.0F, 30.0F);
        
        // 换弹逻辑（模拟卓越前线动作）
        if (this.reloadTimer > 0) {
            this.reloadTimer--;
            if (this.reloadTimer == 0) {
                this.playSound(SoundEvents.CROSSBOW_LOADING_END, 1.0F, 1.0F); 
            }
            return; 
        }

        ItemStack gun = new ItemStack(this instanceof PoliceVillager ? ModItems.TASER_GUN.get() : ModItems.TACTICAL_GUN.get());
        if (!ItemStack.isSameItem(this.getMainHandItem(), gun)) {
            this.setItemInHand(InteractionHand.MAIN_HAND, gun);
        }

        if (this.tickCount % 20 == 0) { // 每秒开一枪
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
            
            // 触发换弹动作
            this.reloadTimer = 40; // 2秒换弹时间
            this.playSound(SoundEvents.CROSSBOW_LOADING_START, 1.0F, 1.0F);
        }
    }

    private void throwGrenade(LivingEntity target) {
        this.swing(InteractionHand.MAIN_HAND, true);
        GrenadeProjectile grenade = new GrenadeProjectile(this.level(), this);
        grenade.shoot(target.getX() - this.getX(), target.getEyeY() - this.getEyeY(), target.getZ() - this.getZ(), 1.2F, 1.0F);
        this.level().addFreshEntity(grenade);
        this.playSound(SoundEvents.SNOWBALL_THROW, 1.0F, 1.0F);
    }

    private void broadcastChatMessage(Player player) {
        String[] messages = {"各单位注意，保持警戒。", "有什么需要协助的吗？", "今天也是个巡逻的好天气。"};
        String msg = messages[this.getRandom().nextInt(messages.length)];
        player.sendSystemMessage(Component.literal("村民: " + msg)); // 玩家要求格式
        chatCooldown = 600; 
    }

    // 交互逻辑：给予武器
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        
        // 如果手里拿着枪或者手榴弹
        if (itemstack.getItem() == ModItems.TACTICAL_GUN.get() || itemstack.getItem() == ModItems.GRENADE.get()) {
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