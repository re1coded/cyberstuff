package ru.re1coded.cyberstuff.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import ru.re1coded.cyberstuff.attachments.ModAttachments;
import ru.re1coded.cyberstuff.component.ModDataComponent;
import ru.re1coded.cyberstuff.data.*;
import ru.re1coded.cyberstuff.register.ModEffects;
import ru.re1coded.cyberstuff.register.ModItems;

import java.util.Objects;
import java.util.function.Consumer;


public class SyringeItem extends Item {

    private String implant_name;

    private static final int EFFECT_DURATION = 300;

    public SyringeItem(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.implant_name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else {
            return obj instanceof SyringeItem item
                    && Objects.equals(this.implant_name, item.implant_name);
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResult.PASS;
        ItemStack stack = player.getItemInHand(hand);
        SyringeData syringeData = stack.get(ModDataComponent.SYRINGE_BASIC.get());
        ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());
        if (syringeData == null) return InteractionResult.FAIL;

        // Проверяем что такой имплант существует
        ImplantDefinition def = ImplantRegistry.get(syringeData.implantId()).orElse(null);
        if (def == null) return InteractionResult.FAIL;

        // Применяем кастомный эффект — несёт в себе id импланта через amplifier
        // amplifier используем как временное хранилище индекса — подробнее ниже
        player.addEffect(new MobEffectInstance(
                ModEffects.SYRINGE_EFFECT,
                EFFECT_DURATION,
                0,
                false,
                true  // показываем частицы
        ));

        // Сохраняем id импланта в отдельный компонент на игроке
        // чтобы знать что выдать после окончания эффекта
        stack.set(ModDataComponent.PENDING_IMPLANT.get(), syringeData.implantId());

        slots.setPendingImplant(new PendingImplant(
                syringeData.implantId(),
                syringeData.rarity()
        ));

        // Применяем сопутствующие ванильные эффекты (анестезия, операция)
        player.addEffect(new MobEffectInstance(
                MobEffects.BLINDNESS, EFFECT_DURATION, 0, false, false
        ));
        player.addEffect(new MobEffectInstance(
                MobEffects.SLOWNESS, EFFECT_DURATION, 2, false, false
        ));
        player.addEffect(new MobEffectInstance(
                MobEffects.WEAKNESS, EFFECT_DURATION, 1, false, false
        ));

        // Звук и расход шприца
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BEE_STING, SoundSource.PLAYERS, 1.0f, 1.0f);
        stack.shrink(1);
        player.addItem(new ItemStack(ModItems.SYRINGE_USED.get()));

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        SyringeData syringeData = itemStack.get(ModDataComponent.SYRINGE_BASIC.get());
        if (syringeData == null) return;
        //description
        builder.accept(Component.translatable("tooltip.cyberstuff.implant.desc." + syringeData.implantId().getPath()).withStyle(ChatFormatting.GRAY));
    }
}
