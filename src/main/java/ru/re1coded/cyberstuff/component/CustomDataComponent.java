package ru.re1coded.cyberstuff.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.re1coded.cyberstuff.CyberStuff;
import ru.re1coded.cyberstuff.items.SyringeData;
import ru.re1coded.cyberstuff.items.SyringeItem;

import java.util.function.Supplier;

public class CustomDataComponent {
    public static final DeferredRegister.DataComponents REGISTER = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CyberStuff.MODID);


    public static final Supplier<DataComponentType<SyringeData>> SYRINGE_BASIC = REGISTER.registerComponentType(
            "basic",
            builder -> builder
                    // The codec to read/write the data to disk
                    .persistent(SyringeData.CODEC)
                    // The codec to read/write the data across the network
                    .networkSynchronized(SyringeData.STREAM_CODEC)
    );

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

}