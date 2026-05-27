package ru.re1coded.cyberstuff.component;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.re1coded.cyberstuff.CyberStuff;
import ru.re1coded.cyberstuff.data.ImplantData;
import ru.re1coded.cyberstuff.data.SyringeData;

import java.util.function.Supplier;

public class ModDataComponent {
    public static final DeferredRegister.DataComponents REGISTER = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CyberStuff.MODID);


    public static final Supplier<DataComponentType<SyringeData>> SYRINGE_BASIC = REGISTER.registerComponentType(
            "basic",
            builder -> builder
                    // The codec to read/write the data to disk
                    .persistent(SyringeData.CODEC)
                    // The codec to read/write the data across the network
                    .networkSynchronized(SyringeData.STREAM_CODEC)
    );

    public static final Supplier<DataComponentType<ImplantData>> IMPLANT_BASIC = REGISTER.registerComponentType(
            "implant_basic",
            builder -> builder
                    .persistent(ImplantData.CODEC)
                    .networkSynchronized(ImplantData.STREAM_CODEC)
    );

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

}