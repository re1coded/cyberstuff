package ru.re1coded.cyberstuff.data;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Rarity;
import ru.re1coded.cyberstuff.effects.IImplantEffect;

import java.util.List;

public record ImplantDefinition(Identifier id, ImplantSlotType slotType, List<IImplantEffect> effectList) {

    public void applyAll(ServerPlayer player, Rarity rarity, boolean hasBonus) {
        for (IImplantEffect effect : effectList) {
            effect.apply(player, rarity, hasBonus);
        }
    }

    public void removeAll(ServerPlayer player) {
        for (IImplantEffect effect : effectList) {
            effect.remove(player);
        }
    }

}
