package ru.re1coded.cyberstuff.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import ru.re1coded.cyberstuff.CyberStuff;
import ru.re1coded.cyberstuff.attachments.ModAttachments;
import ru.re1coded.cyberstuff.data.ImplantData;
import ru.re1coded.cyberstuff.data.ImplantRegistry;
import ru.re1coded.cyberstuff.data.ImplantSlots;
import ru.re1coded.cyberstuff.effects.IImplantEffect;

@EventBusSubscriber(modid = CyberStuff.MODID)
public class ImplantEventHandler {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS);
        for (ImplantData data : slots.getInstalled()) {
            ImplantRegistry.get(data.id()).ifPresent(def -> {
                for (IImplantEffect effect : def.effectList()) {
                    if (effect instanceof IImplantEffect.OnTickEffect onTick) {
                        onTick.onTick(player, data.rarity(), data.bonusActive());
                    }

                    if (player.tickCount % 20 == 0) {
                        if (effect instanceof IImplantEffect.StatusEffect || effect instanceof IImplantEffect.AttributeModifierEffect) {
                            effect.apply(player, data.rarity(), data.bonusActive());
                        }
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target.level() instanceof ServerLevel level)) return;

        double maxRadius = ImplantRegistry.getAll().stream()
                .flatMap(def -> def.effectList().stream())
                .filter(e -> e instanceof IImplantEffect.OnNearbyDeathEffect)
                .mapToDouble(e -> ((IImplantEffect.OnNearbyDeathEffect) e).maxPossibleRadius())
                .max()
                .orElse(32.0);

        level.getEntitiesOfClass(ServerPlayer.class, target.getBoundingBox().inflate(maxRadius))
                .forEach(player -> {
                    ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS);
                    for (ImplantData data : slots.getInstalled()) {
                        ImplantRegistry.get(data.id()).ifPresent(def -> {
                            for (IImplantEffect effect : def.effectList()) {
                                if (effect instanceof IImplantEffect.OnNearbyDeathEffect onDeath) {
                                    onDeath.onNearbyDeath(player, target, data.rarity(), data.bonusActive());
                                }
                            }
                        });
                    }
                });
    }
}