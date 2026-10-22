package com.yourname.tacticalvillagers;

import com.yourname.tacticalvillagers.registry.ModEntities;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TacticalVillagersMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.POLICE.get(), VillagerRenderer::new);
        event.registerEntityRenderer(ModEntities.SWAT.get(), VillagerRenderer::new);
        event.registerEntityRenderer(ModEntities.ARMY.get(), VillagerRenderer::new);
        
        // 偷懒直接借用原版船的渲染器，在游戏里看起来就是一个木船在开炮/飞行（后续你可以自己画模型）
        event.registerEntityRenderer(ModEntities.TANK.get(), BoatRenderer::new);
        event.registerEntityRenderer(ModEntities.PLANE.get(), BoatRenderer::new);
    }
}