package ru.re1coded.cyberstuff.items;

import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffects;
import ru.re1coded.cyberstuff.data.ImplantDefinition;
import ru.re1coded.cyberstuff.data.ImplantRegistry;
import ru.re1coded.cyberstuff.data.ImplantSlotType;
import ru.re1coded.cyberstuff.effects.IImplantEffect;

import java.util.List;

public class ModImplants {
    public static void register() {
        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "adrenaline_booster"),
                ImplantSlotType.BLOOD_SYSTEM,
                List.of(
                        new IImplantEffect.OnNearbyDeathEffect(
                                2.0,
                                MobEffects.SATURATION,
                                1,
                                400
                        )
                )
        ));

        ImplantRegistry.register(new ImplantDefinition(
                Identifier.fromNamespaceAndPath("cyberstuff", "adrenaline_booster"),
                ImplantSlotType.BLOOD_SYSTEM,
                List.of(
                        new IImplantEffect.OnNearbyDeathEffect(
                                2.0,
                                MobEffects.SATURATION,
                                1,
                                400
                        )
                )
        ));
    }
}
