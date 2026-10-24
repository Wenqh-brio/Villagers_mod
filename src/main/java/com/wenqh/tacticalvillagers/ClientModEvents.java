package com.wenqh.tacticalvillagers;

import com.wenqh.tacticalvillagers.registry.ModEntities;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TacticalVillagersMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // 仅注册本模组自定义的战术村民渲染器（使用原版村民模型）
        event.registerEntityRenderer(ModEntities.POLICE.get(), VillagerRenderer::new);
        event.registerEntityRenderer(ModEntities.SWAT.get(), VillagerRenderer::new);
        event.registerEntityRenderer(ModEntities.ARMY.get(), VillagerRenderer::new);
    }
}