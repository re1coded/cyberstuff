package ru.re1coded.cyberstuff.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Rarity;

public record SyringeData(Identifier implantId, Rarity rarity) {

    public static final Codec<SyringeData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("implant_id").forGetter(SyringeData::implantId),
                    Rarity.CODEC.fieldOf("rarity").forGetter(SyringeData::rarity)
            ).apply(instance, SyringeData::new)
    );

    public static final StreamCodec<ByteBuf, SyringeData> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, SyringeData::implantId,
            Rarity.STREAM_CODEC, SyringeData::rarity,
            SyringeData::new
            );
}
