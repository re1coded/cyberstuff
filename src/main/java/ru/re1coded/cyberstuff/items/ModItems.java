package ru.re1coded.cyberstuff.items;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.re1coded.cyberstuff.CyberStuff;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(
            CyberStuff.MODID
    );

    //public static final DeferredItem<Item>


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
