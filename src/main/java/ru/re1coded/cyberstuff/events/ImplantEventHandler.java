package ru.re1coded.cyberstuff.events;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import ru.re1coded.cyberstuff.CyberStuff;
import ru.re1coded.cyberstuff.attachments.ModAttachments;
import ru.re1coded.cyberstuff.component.ModDataComponent;
import ru.re1coded.cyberstuff.data.*;
import ru.re1coded.cyberstuff.effects.IActiveImplantEffect;
import ru.re1coded.cyberstuff.effects.IImplantEffect;
import ru.re1coded.cyberstuff.network.ActivateImplantPacket;
import ru.re1coded.cyberstuff.network.RemoveImplantPacket;
import ru.re1coded.cyberstuff.register.ModEffects;
import ru.re1coded.cyberstuff.register.ModItems;

import java.util.Optional;

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

                        if (effect instanceof IImplantEffect.LowHealthEffect lowHealth) {
                            lowHealth.onTick(player, data.rarity(), def.isUnique());
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
            if (data.id().equals(Identifier.fromNamespaceAndPath(CyberStuff.MODID, "black_mambo")) && !(target.hasEffect(MobEffects.POISON))) continue;
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

                    if (effect instanceof IImplantEffect.AutoHealEffect autoHeal) {
                        // Считаем здоровье после получения урона
                        float healthAfterDamage = player.getHealth() - event.getAmount();
                        float maxHealth = player.getMaxHealth();
                        if (healthAfterDamage / maxHealth <= 0.5) {
                            autoHeal.tryAutoHeal(player, data.id(), data.rarity(), def.isUnique());
                        }
                    }

                    if (effect instanceof IImplantEffect.ElectricShockEffect shock) {
                        shock.shock(player, data.rarity(), def.isUnique());
                    }
                    if (effect instanceof IImplantEffect.ItemHoldShockEffect itemShock) {
                        itemShock.shock(player, data.rarity(), def.isUnique());
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

    public static void handleRemoveImplant(RemoveImplantPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());
            Optional<ImplantData> removed = slots.remove(packet.slotIndex(), player);

            removed.ifPresent(data -> {
                // Создаём шприц с данными снятого импланта
                ItemStack syringeStack = new ItemStack(ModItems.SYRINGE.get());
                syringeStack.set(ModDataComponent.SYRINGE_BASIC.get(),
                        new SyringeData(data.id(), data.rarity())
                );

                // Расходуем шприц для снятия из руки
                ItemStack handStack = player.getItemInHand(packet.hand());
                handStack.shrink(1);

                // Выдаём шприц с имплантом
                player.addItem(syringeStack);

                // Звук
                player.level().playSound(null,
                        player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BEE_STING, SoundSource.PLAYERS,
                        1.0f, 0.8f
                );

                // Сообщение
                player.sendSystemMessage(
                        Component.translatable(
                                "message.cyberstuff.implant_removed",
                                Component.translatable(
                                        "tooltip.cyberstuff.implant.desc." + data.id().getPath()
                                )
                        ).withStyle(ChatFormatting.YELLOW),
                        false
                );
            });
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

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!event.getEffectInstance().is(
                ModEffects.SYRINGE_EFFECT)) return;

        ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());

        slots.getPendingImplant().ifPresent(pending -> {
            ImplantDefinition def = ImplantRegistry.get(pending.implantId()).orElse(null);
            if (def == null) {
                slots.setPendingImplant(null);
                return;
            }

            // Ищем свободный слот
            boolean installed = false;
            for (int i = 0; i < ImplantSlots.MAX_SLOTS; i++) {
                if (slots.get(i).isPresent()) continue;

                slots.install(i, new ImplantData(pending.implantId(), pending.rarity()));
                def.applyAll(player, pending.rarity(), def.isUnique());

                player.sendSystemMessage(
                        Component.translatable(
                                "message.cyberstuff.implant_installed",
                                Component.translatable(
                                        "tooltip.cyberstuff.implant.desc."
                                                + pending.implantId().getPath()
                                )
                        ).withStyle(ChatFormatting.GREEN),
                        false
                );

                player.level().playSound(null,
                        player.getX(), player.getY(), player.getZ(),
                        SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS,
                        1.0f, 1.0f
                );

                installed = true;
                break;
            }

            // Нет свободных слотов — возвращаем предмет игроку
            if (!installed) {
                ItemStack implantStack = ImplantRegistry.createStack(
                        ModItems.IMPLANT.get(),
                        pending.implantId(),
                        pending.rarity()
                );
                player.addItem(implantStack);
                player.sendSystemMessage(
                        Component.translatable("message.cyberstuff.no_free_slot")
                                .withStyle(ChatFormatting.RED),
                        false
                );
            }

            slots.setPendingImplant(null);
        });
    }
}