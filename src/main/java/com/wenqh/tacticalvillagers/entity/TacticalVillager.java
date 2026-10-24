package com.wenqh.tacticalvillagers.entity;

import com.wenqh.tacticalvillagers.entity.projectile.BulletProjectile;
import com.wenqh.tacticalvillagers.entity.projectile.TaserProjectile;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class TacticalVillager extends Villager {
    private int aiTimer = 0;
    private int chatCooldown = 0;
    private int reloadTimer = 0;
    
    private int combatExp = 0;
    private int villagerLevel = 1; 

    private static final String[] VEHICLE_NAMESPACES = {"superbwarfare", "ashvehicle", "vvp", "mscp", "dr_reforge", "fcp"};

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
            tryEatFood();
        }
    }
    
    @Override
    public boolean killedEntity(net.minecraft.server.level.ServerLevel level, LivingEntity entity) {
        boolean result = super.killedEntity(level, entity);
        combatExp += 10;
        checkLevelUp();
        return result;
    }
    
    private void checkLevelUp() {
        if (combatExp >= 30 && villagerLevel < 2) {
            villagerLevel = 2;
            this.setCustomName(Component.literal("老兵村民"));
        } else if (combatExp >= 100 && villagerLevel < 3) {
            villagerLevel = 3;
            this.setCustomName(Component.literal("精英村民"));
        }
    }

    private boolean isSuperbWarfareItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id.getNamespace().equals("superbwarfare") || id.getPath().contains("grenade") || stack.isEdible();
    }

    private boolean isSuperbVehicle(Entity entity) {
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        for (String ns : VEHICLE_NAMESPACES) if (id.getNamespace().equals(ns)) return true;
        return false;
    }

    private void evaluateSimpleAI() {
        if (!this.isPassenger() && tryRideVehicle()) return;

        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            ItemStack mainHand = this.getMainHandItem();
            if (isSuperbWarfareItem(mainHand) && !mainHand.isEdible()) {
                double distSqr = this.distanceToSqr(target);
                if (distSqr > 49.0D && mainHand.getItem().toString().contains("grenade")) {
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
    
    private void tryEatFood() {
        if (this.getHealth() < this.getMaxHealth() * 0.5F) {
            ItemStack offHand = this.getOffhandItem();
            if (offHand.isEdible()) {
                FoodProperties food = offHand.getItem().getFoodProperties();
                if (food != null) {
                    this.heal(food.getNutrition());
                    this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                    offHand.shrink(1);
                }
            }
        }
    }

    private boolean tryRideVehicle() {
        List<Entity> vehicles = this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(15.0D), e -> isSuperbVehicle(e) && e.getPassengers().isEmpty());
        if (!vehicles.isEmpty()) {
            Entity vehicle = vehicles.get(0);
            if (this.distanceToSqr(vehicle) < 9.0D) {
                this.startRiding(vehicle);
                return true;
            } else {
                this.getNavigation().moveTo(vehicle, 1.2D);
            }
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

        int shootCooldown = villagerLevel == 3 ? 20 : (villagerLevel == 2 ? 30 : 40);

        if (this.tickCount % shootCooldown == 0) {
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
            this.reloadTimer = 15; 
            this.playSound(SoundEvents.CROSSBOW_LOADING_START, 1.0F, 1.0F);
        }
    }

    private void throwGrenade(LivingEntity target) {
        this.swing(InteractionHand.MAIN_HAND, true);
        net.minecraft.world.entity.projectile.Snowball grenade = new net.minecraft.world.entity.projectile.Snowball(this.level(), this);
        grenade.shoot(target.getX() - this.getX(), target.getEyeY() - this.getEyeY(), target.getZ() - this.getZ(), 1.2F, 1.0F);
        this.level().addFreshEntity(grenade);
        this.level().explode(this, target.getX(), target.getY(), target.getZ(), 2.0F, Level.ExplosionInteraction.MOB);
    }

    private void broadcastChatMessage(Player player) {
        String[] messages = {"隔壁老王家的牛又跑了。", "今天也是个巡逻的好天气。", "听说最近野外有匪徒出没，小心点。", "长官，准备就绪！"};
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
            if (itemstack.isEdible()) {
                this.setItemInHand(InteractionHand.OFF_HAND, copy); 
            } else {
                this.setItemInHand(InteractionHand.MAIN_HAND, copy); 
            }
            if (!player.isCreative()) itemstack.shrink(1);
            this.playSound(SoundEvents.VILLAGER_YES, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }
    
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("CombatExp", combatExp);
        tag.putInt("VillagerLevel", villagerLevel);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        combatExp = tag.getInt("CombatExp");
        villagerLevel = tag.getInt("VillagerLevel");
    }
}