package ru.re1coded.cyberstuff.items;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.re1coded.cyberstuff.CyberStuff;
import ru.re1coded.cyberstuff.component.ModDataComponent;
import ru.re1coded.cyberstuff.events.ImplantEventHandler;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(
            CyberStuff.MODID
    );

    public static final DeferredItem<Item> SYRINGE = ITEMS.registerItem("syringe",
            SyringeItem::new);

    public static final DeferredItem<Item> SYRINGE_USED = ITEMS.registerSimpleItem("syringe_used");

    public static final DeferredItem<Item> IMPLANT = ITEMS.registerItem("implant", ImplantItem::new);


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
