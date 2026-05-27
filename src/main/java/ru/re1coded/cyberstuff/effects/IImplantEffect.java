package ru.re1coded.cyberstuff.effects;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Rarity;

public sealed interface IImplantEffect permits
        IImplantEffect.AttributeModifierEffect,
        IImplantEffect.StatusEffect,
        IImplantEffect.OnHitEffect,
        IImplantEffect.OnTickEffect,
        IImplantEffect.OnNearbyDeathEffect {

    void apply(ServerPlayer player, Rarity rarity, boolean bonusActive);
    void remove(ServerPlayer player);

    static double getMultiplier(Rarity rarity, boolean bonusActive) {
        double base = switch (rarity) {
            case COMMON -> 1.0;
            case UNCOMMON -> 1.5;
            case RARE -> 2.25;
            case EPIC -> 3.0;
        };
        return bonusActive ? base * 1.5 : base;
    }

    record AttributeModifierEffect(
            Holder<Attribute> attributeHolder,
            Identifier id,
            double baseValue,
            AttributeModifier.Operation operation
    ) implements IImplantEffect {

        @Override
        public void apply(ServerPlayer player, Rarity rarity, boolean bonusActive) {
            AttributeInstance instance = player.getAttribute(attributeHolder);
            if (instance == null) return;
            // Убираем старый если есть, чтобы не дублировать
            instance.removeModifier(id);
            double value = baseValue * getMultiplier(rarity, bonusActive);
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
        public void apply(ServerPlayer player, Rarity rarity, boolean bonusActive) {
            int amplifier = baseAmplifier + switch (rarity) {
                case COMMON   -> 0;
                case UNCOMMON -> 1;
                case RARE     -> 2;
                case EPIC     -> 3;
            } + (bonusActive ? 1 : 0);
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
        public void apply(ServerPlayer player, Rarity rarity, boolean bonusActive) {}

        @Override
        public void remove(ServerPlayer player) {}
    }

    record OnTickEffect(
            int intervalTicks,
            double baseAmount
    ) implements IImplantEffect {

        public void onTick(ServerPlayer player, Rarity rarity, boolean hasBonus) {
            if (player.tickCount % intervalTicks != 0) return;
            float amount = (float)(baseAmount * getMultiplier(rarity, hasBonus));
            player.heal(amount);
        }
        @Override
        public void apply(ServerPlayer player, Rarity rarity, boolean bonusActive) {}

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
        public void apply(ServerPlayer player, Rarity rarity, boolean bonusActive) {}

        @Override
        public void remove(ServerPlayer player) {}
    }
}
