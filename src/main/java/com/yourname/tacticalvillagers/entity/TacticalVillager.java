package com.yourname.tacticalvillagers.entity;

import com.yourname.tacticalvillagers.entity.projectile.BulletProjectile;
import com.yourname.tacticalvillagers.entity.projectile.TaserProjectile;
import com.yourname.tacticalvillagers.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TacticalVillager extends Villager {
    private int aiTimer = 0;
    private int chatCooldown = 0;

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
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) { handleCombat(target); return; }
        
        if (chatCooldown <= 0 && this.getRandom().nextFloat() < 0.05F) {
            Player nearestPlayer = this.level().getNearestPlayer(this, 10.0D);
            if (nearestPlayer != null) broadcastChatMessage(nearestPlayer);
        }
    }

    private void handleCombat(LivingEntity target) {
        this.getLookControl().setLookAt(target, 30.0F, 30.0F);
        ItemStack gun = new ItemStack(this instanceof PoliceVillager ? ModItems.TASER_GUN.get() : ModItems.TACTICAL_GUN.get());
        if (!ItemStack.isSameItem(this.getMainHandItem(), gun)) this.setItemInHand(InteractionHand.MAIN_HAND, gun);

        if (this.tickCount % 30 == 0) {
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
        }
    }

    private void broadcastChatMessage(Player player) {
        String[] messages = {"各单位注意，保持警戒。", "有什么需要协助的吗？", "今天也是个巡逻的好天气。"};
        String msg = messages[this.getRandom().nextInt(messages.length)];
        player.sendSystemMessage(Component.literal("[" + this.getName().getString() + "]: " + msg));
        chatCooldown = 600; 
    }
}