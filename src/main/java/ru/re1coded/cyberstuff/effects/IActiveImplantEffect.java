package ru.re1coded.cyberstuff.effects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import ru.re1coded.cyberstuff.attachments.ModAttachments;
import ru.re1coded.cyberstuff.data.ImplantRegistry;
import ru.re1coded.cyberstuff.data.ImplantSlots;

import java.util.function.Predicate;

public non-sealed interface IActiveImplantEffect extends IImplantEffect {
    // Активация эффекта — вызывается при нажатии кнопки
    void activate(ServerPlayer player, Rarity rarity, boolean hasBonus);

    // Кулдаун в тиках
    int baseCooldownTicks();

    default int getCooldown(Rarity rarity, boolean hasBonus) {
        return (int) (baseCooldownTicks() / IImplantEffect.getMultiplier(rarity, hasBonus));
    }


    @Override
    default void apply(ServerPlayer player, Rarity rarity, boolean hasBonus) {
    }

    @Override
    default void remove(ServerPlayer player) {
    }

    record ActiveGlowEffect(
            double baseRadius,
            int baseDuration,
            int baseCooldownTicks,
            Predicate<LivingEntity> entityFilter
    ) implements IActiveImplantEffect {

        @Override
        public void activate(ServerPlayer player, Rarity rarity, boolean hasBonus) {
            double radius = baseRadius * IImplantEffect.getMultiplier(rarity, hasBonus);
            int duration = (int) (baseDuration * IImplantEffect.getMultiplier(rarity, hasBonus));

            player.level().getEntitiesOfClass(
                    LivingEntity.class,
                    player.getBoundingBox().inflate(radius),
                    entity -> entity != player && entityFilter.test(entity)
            ).forEach(entity ->
                    entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, duration, 0))
            );
        }
    }

    record ActiveToggleEffect(
            IImplantEffect innerEffect,  // какой эффект включается при активации
            int baseCooldownTicks
    ) implements IActiveImplantEffect {

        @Override
        public void activate(ServerPlayer player, Rarity rarity, boolean hasBonus) {
            ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());
            // Найти id импланта у которого этот эффект
            slots.getInstalled().stream()
                    .filter(data -> ImplantRegistry.get(data.id())
                            .map(def -> def.effectList().contains(this))
                            .orElse(false))
                    .findFirst()
                    .ifPresent(data -> {
                        slots.toggle(data.id());
                        // Если выключили — снимаем эффект
                        if (!slots.isToggled(data.id())) {
                            innerEffect.remove(player);
                        }
                    });
        }

        @Override
        public int baseCooldownTicks() {
            return baseCooldownTicks;
        }

        @Override
        public void apply(ServerPlayer player, Rarity rarity, boolean hasBonus) {
        }

        @Override
        public void remove(ServerPlayer player) {
            innerEffect.remove(player);
        }

    }

    record TrapDetectionEffect(
            double baseRadius,
            int baseCooldownTicks
    ) implements IActiveImplantEffect {

        @Override
        public void activate(ServerPlayer player, Rarity rarity, boolean hasBonus) {
            double radius = baseRadius * IImplantEffect.getMultiplier(rarity, hasBonus);
            Level level = player.level();
            Vec3 playerEyePos = player.getEyePosition();
            BlockPos playerPos = player.blockPosition();
            int r = (int) Math.ceil(radius);

            for (BlockPos pos : BlockPos.betweenClosed(
                    playerPos.offset(-r, -r, -r),
                    playerPos.offset(r, r, r)
            )) {
                if (!level.getBlockState(pos).is(Blocks.TNT) && !level.getBlockState(pos).is(Blocks.TRIPWIRE_HOOK)) continue;
                if (playerPos.distSqr(pos) > radius * radius) continue;

                // Центр блока
                Vec3 blockCenter = Vec3.atCenterOf(pos);

                // Рисуем линию частиц от глаз игрока до блока
                drawParticleLine(player, playerEyePos, blockCenter);
            }
        }

        private void drawParticleLine(ServerPlayer player, Vec3 from, Vec3 to) {
            Vec3 direction = to.subtract(from);
            double distance = direction.length();
            Vec3 step = direction.normalize().scale(0.5);

            int count = (int)(distance / 0.5);
            for (int i = 0; i <= count; i++) {
                Vec3 pos = from.add(step.scale(i));
                ((ServerLevel) player.level()).sendParticles(
                        player,
                        ParticleTypes.END_ROD,
                        true,
                        true,
                        pos.x, pos.y, pos.z,
                        1,
                        0, 0, 0,
                        0
                );
            }
        }

        @Override public int baseCooldownTicks() { return baseCooldownTicks; }
        @Override public void apply(ServerPlayer player, Rarity rarity, boolean hasBonus) {}
        @Override public void remove(ServerPlayer player) {}
    }

}
