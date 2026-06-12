package ru.re1coded.cyberstuff.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Rarity;

public record PendingImplant(Identifier implantId, Rarity rarity) {

    public static final Codec<PendingImplant> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Identifier.CODEC.fieldOf("implant_id").forGetter(PendingImplant::implantId),
                    Rarity.CODEC.fieldOf("rarity").forGetter(PendingImplant::rarity)
            ).apply(inst, PendingImplant::new)
    );


}
