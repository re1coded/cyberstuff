package ru.re1coded.cyberstuff.effects;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.Rarity;
import ru.re1coded.cyberstuff.attachments.ModAttachments;
import ru.re1coded.cyberstuff.data.ImplantData;
import ru.re1coded.cyberstuff.data.ImplantSlots;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public interface IImplantEffect {

    void apply(ServerPlayer player, Rarity rarity, boolean isUnique);
    void remove(ServerPlayer player);

    static double getMultiplier(Rarity rarity, boolean isUnique) {
        double base = switch (rarity) {
            case COMMON -> 1.0;
            case UNCOMMON -> 1.5;
            case RARE -> 2.25;
            case EPIC -> 3.0;
        };
        return isUnique ? base * 1.5 : base;
    }

    record AttributeModifierEffect(
            Identifier id,
            Holder<Attribute> attributeHolder,
            double baseValue,
            AttributeModifier.Operation operation
    ) implements IImplantEffect {

        @Override
        public void apply(ServerPlayer player, Rarity rarity, boolean isUnique) {
            AttributeInstance instance = player.getAttribute(attributeHolder);
            if (instance == null) return;
            // Убираем старый если есть, чтобы не дублировать
            instance.removeModifier(id);
            double value = baseValue * getMultiplier(rarity, isUnique);
            instance.addPermanentModifier(
                    new AttributeModifier(id, value, operation)
            );
        }

        @Override
        public void remove(ServerPlayer player) {
            AttributeInstance instance = player.getAttribute(attributeHolder);
            if (instance != null) instance.removeModifier(id);
        }
    }

    record StatusEffect(
            Holder<MobEffect> effectHolder,
            int baseAmplifier
    ) implements IImplantEffect {

        @Override
        public void apply(ServerPlayer player, Rarity rarity, boolean isUnique) {
            int amplifier = baseAmplifier + switch (rarity) {
                case COMMON   -> 0;
                case UNCOMMON -> 1;
                case RARE     -> 2;
                case EPIC     -> 3;
            } + (isUnique ? 1 : 0);
            player.addEffect(new MobEffectInstance(
                    effectHolder, Integer.MAX_VALUE, amplifier, false, false
            ));
        }

        @Override
        public void remove(ServerPlayer player) {
            player.removeEffect(effectHolder);
        }
    }

    record OnHitEffect(
            Holder<MobEffect> effectHolder,
            int baseAmplifier,
            int baseDuration
    ) implements IImplantEffect {

        public void onHit(ServerPlayer player, LivingEntity target, Rarity rarity, boolean hasBonus) {
            int amplifier = baseAmplifier + switch (rarity ){
                case COMMON -> 0; case UNCOMMON -> 1;
                case EPIC -> 3; case RARE -> 2;
            } + (hasBonus ? 1 : 0);
            int duration = (int)(baseDuration * getMultiplier(rarity, hasBonus));

            target.addEffect(new MobEffectInstance(effectHolder, duration, amplifier), player);
        }
        @Override
        public void apply(ServerPlayer player, Rarity rarity, boolean isUnique) {}

        @Override
        public void remove(ServerPlayer player) {}
    }

    record OnTickEffect(
            int intervalTicks,
            double baseAmount,
            Holder<MobEffect> effects
    ) implements IImplantEffect {

        public void onTick(ServerPlayer player, Rarity rarity, boolean hasBonus) {
            if (player.tickCount % intervalTicks != 0) return;
            float amount = (float)(baseAmount * getMultiplier(rarity, hasBonus));
            player.addEffect(new MobEffectInstance(effects, Integer.MAX_VALUE, (int) amount, false, false));
        }
        @Override
        public void apply(ServerPlayer player, Rarity rarity, boolean isUnique) {}

        @Override
        public void remove(ServerPlayer player) {}
    }

    record OnNearbyDeathEffect(
            double baseRadius,
            Holder<MobEffect> effectHolder,
            int baseAmplifier,
            int baseDuration
    ) implements IImplantEffect {

        public void onNearbyDeath(ServerPlayer player, LivingEntity target, Rarity rarity, boolean hasBonus) {
            double radius = baseRadius * getMultiplier(rarity, hasBonus);
            if (player.distanceTo(target) > radius) return;
            int amplifier = baseAmplifier + switch (rarity) {
                case COMMON -> 0; case UNCOMMON -> 1;
                case RARE -> 2; case EPIC -> 3;
            } + (hasBonus ? 1 : 0);
            int duration = (int)(baseDuration * getMultiplier(rarity, hasBonus));
            player.addEffect(new MobEffectInstance(effectHolder, duration, amplifier));
        }

        public double maxPossibleRadius() {
            return baseRadius * 4.5;
        }

        @Override
        public void apply(ServerPlayer player, Rarity rarity, boolean isUnique) {}

        @Override
        public void remove(ServerPlayer player) {}
    }

    record ConditionalEffect(
            Identifier requiredId,
            IImplantEffect effect
    ) implements IImplantEffect {

        @Override
        public void apply(ServerPlayer player, Rarity rarity, boolean isUnique) {
            ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());
            if (slots.hasImplant(requiredId)) {
                effect.apply(player, rarity, isUnique);
            }
        }

        @Override
        public void remove(ServerPlayer player) {
            effect.remove(player);
        }
    }

    record ProjectileDeflectEffect(
            double baseChance
    ) implements IImplantEffect {

        public boolean tryDeflect(Rarity rarity, boolean hasBonus) {
            double chance = Math.min(baseChance * getMultiplier(rarity, hasBonus), 1.0);
            return Math.random() < chance;
        }

        @Override
        public void apply(ServerPlayer player, Rarity rarity, boolean isUnique) {

        }

        @Override
        public void remove(ServerPlayer player) {

        }
    }

    record DamageReductionEffect(
            double baseReduction,
            @Nullable ResourceKey<DamageType> damage
    ) implements IImplantEffect {

        public float reduce(float amount, DamageSource source, Rarity rarity, boolean isUnique) {

            if (damage != null && !source.is(damage)) return amount;
            double multiplier = Math.min(baseReduction * getMultiplier(rarity, isUnique), 0.9);
            return (float)(amount * (1.0 - multiplier));
        }
        @Override
        public void apply(ServerPlayer player, Rarity rarity, boolean isUnique) {

        }

        @Override
        public void remove(ServerPlayer player) {

        }
    }

    record DistanceDamageReductionEffect(
            double maxReduction,
            double maxDistance,
            boolean doInvertDistance
    ) implements IImplantEffect {

        public float reduce(float amount, DamageSource source,
                            ServerPlayer player, Rarity rarity, boolean hasBonus) {
            if (!(source.getEntity() instanceof LivingEntity attacker)) return amount;

            double distance = player.distanceTo(attacker);
            double effectiveMaxDistance = maxDistance * getMultiplier(rarity, hasBonus);

            double factor = Math.min(distance / effectiveMaxDistance, 1.0);
            if (doInvertDistance) factor = 1.0 - factor; // инвертируем для ближнего боя

            double reduction = Math.min(maxReduction * factor * getMultiplier(rarity, hasBonus), 0.9);
            return (float)(amount * (1.0 - reduction));
        }

        @Override
        public void apply(ServerPlayer player, Rarity rarity, boolean isUnique) {

        }

        @Override
        public void remove(ServerPlayer player) {

        }
    }

    record ElectricShockEffect(
            double radius,
            int baseAmplifier,
            int baseDuration
    ) implements IImplantEffect {

        public void shock(ServerPlayer player, Rarity rarity, boolean hasBonus) {
            double effectiveRadius = radius * getMultiplier(rarity, hasBonus);
            int amplifier = baseAmplifier + switch (rarity) {
                case COMMON -> 0; case UNCOMMON -> 1;
                case RARE   -> 2; case EPIC     -> 3;
            } + (hasBonus ? 1 : 0);
            int duration = (int)(baseDuration * getMultiplier(rarity, hasBonus));

            player.level().getEntitiesOfClass(
                    LivingEntity.class,
                    player.getBoundingBox().inflate(effectiveRadius),
                    entity -> entity != player && entity instanceof Enemy
            ).forEach(entity -> {
                entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, duration, amplifier));
                entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, duration, amplifier));
                entity.addEffect(new MobEffectInstance(MobEffects.POISON, duration, amplifier));
            });
        }

        @Override public void apply(ServerPlayer player, Rarity rarity, boolean hasBonus) {}
        @Override public void remove(ServerPlayer player) {}
    }

    record LowHealthRegenEffect(
            double healthThreshold,
            int baseAmplifier,
            int baseDuration
    ) implements IImplantEffect {

        public void onTick(ServerPlayer player, Rarity rarity, boolean hasBonus) {
            if (player.tickCount % 20 != 0) return;

            float currentHealth = player.getHealth();
            float maxHealth = player.getMaxHealth();

            if (currentHealth / maxHealth <= healthThreshold) {
                int amplifier = baseAmplifier + switch (rarity) {
                    case COMMON -> 0; case UNCOMMON -> 1;
                    case RARE   -> 2; case EPIC     -> 3;
                } + (hasBonus ? 1 : 0);
                int duration = (int)(baseDuration * getMultiplier(rarity, hasBonus));

                player.addEffect(new MobEffectInstance(
                        MobEffects.REGENERATION, duration, amplifier, false, true
                ));
            } else {
                // Убираем эффект если здоровье восстановилось выше порога
                player.removeEffect(MobEffects.REGENERATION);
            }
        }

        @Override public void apply(ServerPlayer player, Rarity rarity, boolean hasBonus) {}
        @Override public void remove(ServerPlayer player) {
            player.removeEffect(MobEffects.REGENERATION);
        }
    }

    record CrouchBonusEffect(
            Identifier id,              // can contain "armor", "jump_boost", "sneak", hardcoded for now
            double baseModifier,           // set to 0 if using "jump_boost" or "sneak"
            int baseAmplifier
    ) implements IImplantEffect {



        @Override
        public void apply(ServerPlayer player, Rarity rarity, boolean hasBonus) {
            if (player.isCrouching()) {
                if (id.toString().contains("armor")) {
                    // Броня через атрибут
                    AttributeInstance armorAttr = player.getAttribute(Attributes.ARMOR);
                    if (armorAttr != null) {
                        armorAttr.removeModifier(id);
                        armorAttr.addPermanentModifier(new AttributeModifier(
                                id,
                                baseModifier * getMultiplier(rarity, hasBonus),
                                AttributeModifier.Operation.ADD_VALUE
                        ));
                    }
                    // Замедление
                    int amplifier = baseAmplifier + switch (rarity) {
                        case COMMON -> 0; case UNCOMMON -> 1;
                        case RARE   -> 2; case EPIC     -> 3;
                    } + (hasBonus ? 1 : 0);
                    player.addEffect(new MobEffectInstance(
                            MobEffects.SLOWNESS, 40, amplifier, false, false
                    ));
                } else if (id.toString().contains("jump_boost")) {
                    int amplifier = baseAmplifier + switch (rarity) {
                        case COMMON -> 0; case UNCOMMON -> 1;
                        case RARE   -> 2; case EPIC     -> 3;
                    } + (hasBonus ? 1 : 0);
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, -1, amplifier, false, false, true));
                } else if (id.toString().contains("sneak")) {
                    AttributeInstance sneakAttr = player.getAttribute(Attributes.SNEAKING_SPEED);
                    if (sneakAttr != null) {
                        sneakAttr.removeModifier(id);
                        sneakAttr.addPermanentModifier(new AttributeModifier(
                                id,
                                baseModifier * getMultiplier(rarity, hasBonus),
                                AttributeModifier.Operation.ADD_VALUE
                        ));
                    }
                }
            } else {
                // Игрок встал — снимаем всё
                if (id.toString().contains("armor")) {
                    AttributeInstance armorAttr = player.getAttribute(Attributes.ARMOR);
                    if (armorAttr != null) armorAttr.removeModifier(id);
                    player.removeEffect(MobEffects.SLOWNESS);
                } else if (id.toString().contains("jump_boost")) {
                    player.removeEffect(MobEffects.JUMP_BOOST);
                } else if (id.toString().contains("sneak")) {
                    AttributeInstance sneakAttr = player.getAttribute(Attributes.SNEAKING_SPEED);
                    if (sneakAttr != null) sneakAttr.removeModifier(id);
                }
            }
        }

        @Override
        public void remove(ServerPlayer player) {
            AttributeInstance armorAttr = player.getAttribute(Attributes.ARMOR);
            if (armorAttr != null) armorAttr.removeModifier(id);
            AttributeInstance sneakAttr = player.getAttribute(Attributes.SNEAKING_SPEED);
            if (sneakAttr != null) sneakAttr.removeModifier(id);
            player.removeEffect(MobEffects.SLOWNESS);
            player.removeEffect(MobEffects.JUMP_BOOST);
        }
    }

    record OnEatEffect(
            List<EatEffectEntry> effects
    ) implements IImplantEffect {

        public record EatEffectEntry(
                Holder<MobEffect> effect,
                int baseAmplifier,
                int baseDuration
        ) {}

        public void onEat(ServerPlayer player, Rarity rarity, boolean hasBonus) {
            for (EatEffectEntry entry : effects) {
                int amplifier = entry.baseAmplifier() + switch (rarity) {
                    case COMMON -> 0; case UNCOMMON -> 1;
                    case RARE   -> 2; case EPIC     -> 3;
                } + (hasBonus ? 1 : 0);
                int duration = (int)(entry.baseDuration() * getMultiplier(rarity, hasBonus));
                player.addEffect(new MobEffectInstance(entry.effect(), duration, amplifier));
            }
        }

        @Override public void apply(ServerPlayer player, Rarity rarity, boolean hasBonus) {}
        @Override public void remove(ServerPlayer player) {}
    }

    record CamoEffect(
            double baseVisibility  // 0.0 - 1.0
    ) implements IImplantEffect {

        public double modify(Rarity rarity, boolean hasBonus) {
            return Math.max(baseVisibility - (getMultiplier(rarity, hasBonus) - 1) * 0.2, 0);
        }

        @Override public void apply(ServerPlayer player, Rarity rarity, boolean hasBonus) {}
        @Override public void remove(ServerPlayer player) {}
    }

    record OnKillCooldownReduceEffect(
            double baseReduction  // 0.0 - 1.0, например 0.3 = 30% от текущего кулдауна
    ) implements IImplantEffect {

        public void onKill(ServerPlayer player, Rarity rarity, boolean hasBonus) {
            double reduction = baseReduction * getMultiplier(rarity, hasBonus);
            ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());

            for (ImplantData data : slots.getInstalled()) {
                int current = slots.getCooldown(data.id());
                if (current <= 0) continue;

                int reduced = (int)(current * (1.0 - reduction));
                slots.setCooldown(data.id(), reduced);
            }
        }

        @Override public void apply(ServerPlayer player, Rarity rarity, boolean hasBonus) {}
        @Override public void remove(ServerPlayer player) {}
    }

    record CooldownResetEffect(
            int baseCooldownTicks  // собственный кулдаун усилителя
    ) implements IImplantEffect {

        public void onOtherImplantActivated(ServerPlayer player, Identifier activatedImplantId,
                                            Identifier ownImplantId, Rarity rarity, boolean hasBonus) {
            ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());

            // Сам усилитель не должен быть на кулдауне
            if (slots.isOnCooldown(ownImplantId)) return;

            // Сбрасываем кулдаун активированного импланта
            slots.setCooldown(activatedImplantId, 0);

            // Ставим кулдаун на себя
            int cooldown = (int)(baseCooldownTicks / getMultiplier(rarity, hasBonus));
            slots.setCooldown(ownImplantId, cooldown);
        }

        @Override public void apply(ServerPlayer player, Rarity rarity, boolean hasBonus) {}
        @Override public void remove(ServerPlayer player) {}
    }

    record DoubleJumpEffect(
            double baseJumpStrength  // сила прыжка, 1.0 = обычный прыжок
    ) implements IImplantEffect {

        @Override
        public void apply(ServerPlayer player, Rarity rarity, boolean hasBonus) {
        }

        @Override
        public void remove(ServerPlayer player) {
        }
    }

}
