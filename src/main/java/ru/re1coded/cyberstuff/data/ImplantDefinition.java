package ru.re1coded.cyberstuff.data;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Rarity;
import ru.re1coded.cyberstuff.effects.IImplantEffect;

import java.util.List;

public record ImplantDefinition(Identifier id, ImplantSlotType slotType, boolean isUnique, List<IImplantEffect> effectList) {

    public void applyAll(ServerPlayer player, Rarity rarity, boolean isUnique) {
        for (IImplantEffect effect : effectList) {
            effect.apply(player, rarity, isUnique);
        }
    }

    public void removeAll(ServerPlayer player) {
        for (IImplantEffect effect : effectList) {
            effect.remove(player);
        }
    }
}
