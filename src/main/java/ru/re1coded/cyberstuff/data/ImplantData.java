package ru.re1coded.cyberstuff.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Rarity;

public record ImplantData(Identifier id, Rarity rarity) {

    // Ванильная редкость (COMMON, UNCOMMON, RARE, EPIC)
    public static final Codec<ImplantData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Identifier.CODEC.fieldOf("id").forGetter(ImplantData::id),
            Rarity.CODEC.fieldOf("rarity").forGetter(ImplantData::rarity)
    ).apply(inst, ImplantData::new));

    public static final StreamCodec<ByteBuf, ImplantData> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, ImplantData::id,
            ByteBufCodecs.fromCodec(Rarity.CODEC), ImplantData::rarity,
            ImplantData::new
    );
}
