package ru.re1coded.cyberstuff.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.re1coded.cyberstuff.CyberStuff;
import ru.re1coded.cyberstuff.effects.SyringeEffect;

public class ModEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, CyberStuff.MODID);

    public static final DeferredHolder<MobEffect, SyringeEffect> SYRINGE_EFFECT =
            MOB_EFFECTS.register("syringe_effect", () -> new SyringeEffect(
                    MobEffectCategory.NEUTRAL,
                    0x00FF88
            ) );

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
