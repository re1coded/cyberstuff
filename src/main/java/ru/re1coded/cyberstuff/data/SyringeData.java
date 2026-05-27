package ru.re1coded.cyberstuff.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record SyringeData(Identifier implantName) {

    public static final Codec<SyringeData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("implant_name").forGetter(SyringeData::implantName)
            ).apply(instance, SyringeData::new)
    );

    public static final StreamCodec<ByteBuf, SyringeData> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, SyringeData::implantName,
            SyringeData::new
            );
}
