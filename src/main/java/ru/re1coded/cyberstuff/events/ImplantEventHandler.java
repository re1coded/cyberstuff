package ru.re1coded.cyberstuff.events;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import ru.re1coded.cyberstuff.CyberStuff;
import ru.re1coded.cyberstuff.attachments.ModAttachments;
import ru.re1coded.cyberstuff.data.*;
import ru.re1coded.cyberstuff.effects.IActiveImplantEffect;
import ru.re1coded.cyberstuff.effects.IImplantEffect;
import ru.re1coded.cyberstuff.network.ActivateImplantPacket;

@EventBusSubscriber(modid = CyberStuff.MODID)
public class ImplantEventHandler {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS);

        slots.setWasOnGround(player.onGround());

        slots.tickCooldowns();

        for (ImplantData data : slots.getInstalled()) {
            ImplantRegistry.get(data.id()).ifPresent(def -> {
                for (IImplantEffect effect : def.effectList()) {
                    if (effect instanceof IImplantEffect.OnTickEffect onTick) {
                        onTick.onTick(player, data.rarity(), def.isUnique());
                    }

                    if (player.tickCount % 20 == 0) {
                        if (effect instanceof IImplantEffect.StatusEffect || effect instanceof IImplantEffect.AttributeModifierEffect || effect instanceof IImplantEffect.ConditionalEffect || effect instanceof IImplantEffect.CrouchBonusEffect) {
                            effect.apply(player, data.rarity(), def.isUnique());
                        }

                        if (effect instanceof IImplantEffect.LowHealthRegenEffect lowHealthRegen) {
                            lowHealthRegen.onTick(player, data.rarity(), def.isUnique());
                        }
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());

        // Игрок прыгнул с земли — сбрасываем флаг
        if (player.onGround()) {
            slots.setDoubleJumpUsed(false);
            return;
        }

        // Игрок в воздухе — проверяем двойной прыжок
        if (slots.isDoubleJumpUsed()) return;

        for (ImplantData data : slots.getInstalled()) {
            ImplantRegistry.get(data.id()).ifPresent(def -> {
                for (IImplantEffect effect : def.effectList()) {
                    if (!(effect instanceof IImplantEffect.DoubleJumpEffect doubleJump)) continue;

                    double strength = doubleJump.baseJumpStrength();

                    // Добавляем вертикальную скорость
                    Vec3 velocity = player.getDeltaMovement();
                    player.setDeltaMovement(velocity.x, strength * 0.42, velocity.z);
                    player.hurtMarked = true; // синхронизируем с клиентом

                    slots.setDoubleJumpUsed(true);

                    // Частицы для визуального feedback
                    ((ServerLevel) player.level()).sendParticles(
                            player,
                            ParticleTypes.CLOUD,
                            true,
                            true,
                            player.getX(), player.getY(), player.getZ(),
                            10, 0.3, 0, 0.3, 0.05
                    );
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerEat(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!event.getItem().has(DataComponents.CONSUMABLE)) return;

        ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());
        for (ImplantData data : slots.getInstalled()) {
            ImplantRegistry.get(data.id()).ifPresent(def -> {
                for (IImplantEffect effect : def.effectList()) {
                    if (effect instanceof IImplantEffect.OnEatEffect onEat) {
                        onEat.onEat(player, data.rarity(), def.isUnique());
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerHit(LivingIncomingDamageEvent event) {
        // Прямой урон от игрока
        Entity attacker = event.getSource().getEntity();
        // Косвенный урон - стрелы, снаряды выпущенные игроком
        Entity directAttacker = event.getSource().getDirectEntity();

        ServerPlayer player = null;
        if (attacker instanceof ServerPlayer p) {
            player = p;
        } else if (directAttacker instanceof Projectile projectile
                && projectile.getOwner() instanceof ServerPlayer p) {
            player = p;
        }

        if (player == null) return;
        LivingEntity target = event.getEntity();

        ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());
        ServerPlayer finalPlayer = player;
        for (ImplantData data : slots.getInstalled()) {
            ImplantRegistry.get(data.id()).ifPresent(def -> {
                for (IImplantEffect effect : def.effectList()) {
                    if (effect instanceof IImplantEffect.OnHitEffect onHit) {
                        onHit.onHit(finalPlayer, target, data.rarity(), def.isUnique());
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target.level() instanceof ServerLevel level)) return;

        if (event.getSource().getEntity() instanceof ServerPlayer killer) {
            ImplantSlots slots = killer.getData(ModAttachments.IMPLANT_SLOTS.get());
            for (ImplantData data : slots.getInstalled()) {
                ImplantRegistry.get(data.id()).ifPresent(def -> {
                    for (IImplantEffect effect : def.effectList()) {
                        if (effect instanceof IImplantEffect.OnKillCooldownReduceEffect reduce) {
                            reduce.onKill(killer, data.rarity(), def.isUnique());
                        }
                    }
                });
            }
        }

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
                                    onDeath.onNearbyDeath(player, target, data.rarity(), def.isUnique());
                                }
                            }
                        });
                    }
                });
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (!(event.getRayTraceResult() instanceof EntityHitResult hitResult)) return;
        if (!(hitResult.getEntity() instanceof ServerPlayer player)) return;

        ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());
        for (ImplantData data : slots.getInstalled()) {
            ImplantRegistry.get(data.id()).ifPresent(def -> {
                for (IImplantEffect effect : def.effectList()) {
                    if (effect instanceof IImplantEffect.ProjectileDeflectEffect deflect) {
                        if (deflect.tryDeflect(data.rarity(), def.isUnique())) {
                            // Отменяем попадание и уничтожаем снаряд
                            event.setCanceled(true);
                            event.getProjectile().discard();
                        }
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());

        for (ImplantData data : slots.getInstalled()) {
            ImplantRegistry.get(data.id()).ifPresent(def -> {
                for (IImplantEffect effect : def.effectList()) {
                    if (effect instanceof IImplantEffect.DamageReductionEffect reduction) {
                        event.setAmount(reduction.reduce(
                                event.getAmount(),
                                event.getSource(),
                                data.rarity(),
                                def.isUnique()
                        ));
                    }

                    if (effect instanceof IImplantEffect.DistanceDamageReductionEffect reduction) {
                        event.setAmount(reduction.reduce(
                                event.getAmount(),
                                event.getSource(),
                                player,
                                data.rarity(),
                                def.isUnique()
                        ));
                    }

                    if (effect instanceof IImplantEffect.ElectricShockEffect shock) {
                        shock.shock(player, data.rarity(), def.isUnique());
                    }
                }
            });
        }
    }

    public static void handleActivateImplant(ActivateImplantPacket packet,
                                             IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());

            for (ImplantData data : slots.getInstalled()) {
                ImplantRegistry.get(data.id()).ifPresent(def -> {
                    for (IImplantEffect effect : def.effectList()) {
                        if (!(effect instanceof IActiveImplantEffect active)) continue;
                        if (slots.isOnCooldown(data.id())) return;

                        active.activate(player, data.rarity(), def.isUnique());
                        slots.setCooldown(
                                data.id(),
                                active.getCooldown(data.rarity(), def.isUnique())
                        );

                        triggerCooldownReset(player, data.id());
                    }
                });
            }
        });
    }

    private static void triggerCooldownReset(ServerPlayer player, Identifier activatedId) {
        ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());

        for (ImplantData data : slots.getInstalled()) {
            // Не триггерим на том же импланте который только что активировали
            if (data.id().equals(activatedId)) continue;

            ImplantRegistry.get(data.id()).ifPresent(def -> {
                for (IImplantEffect effect : def.effectList()) {
                    if (effect instanceof IImplantEffect.CooldownResetEffect reset) {
                        reset.onOtherImplantActivated(
                                player, activatedId, data.id(),
                                data.rarity(), def.isUnique()
                        );
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onVisibilityCheck(LivingEvent.LivingVisibilityEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());

        for (ImplantData data : slots.getInstalled()) {
            ImplantRegistry.get(data.id()).ifPresent(def -> {
                for (IImplantEffect effect : def.effectList()) {
                    if (!(effect instanceof IImplantEffect.CamoEffect camo)) continue;

                    // Статичный CamoEffect — всегда работает
                    if (!(effect instanceof IActiveImplantEffect)) {
                        event.modifyVisibility(camo.modify(data.rarity(), def.isUnique()));
                        return;
                    }

                    // Переключаемый — только если включён
                    if (slots.isToggled(data.id())) {
                        event.modifyVisibility(camo.modify(data.rarity(), def.isUnique()));
                    }
                }
            });
        }
    }
}