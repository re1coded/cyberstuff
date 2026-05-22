package ru.re1coded.cyberstuff.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SyringeData(String type, String implantName) {

    public static final Codec<SyringeData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("type").forGetter(SyringeData::type),
                    Codec.STRING.fieldOf("implant_name").forGetter(SyringeData::implantName)
            ).apply(instance, SyringeData::new)
    );

    public static final StreamCodec<ByteBuf, SyringeData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SyringeData::type,
            ByteBufCodecs.STRING_UTF8, SyringeData::implantName,
            SyringeData::new
            );
}
