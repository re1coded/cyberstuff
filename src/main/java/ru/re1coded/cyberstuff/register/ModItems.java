package ru.re1coded.cyberstuff.register;

import com.jcraft.jorbis.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.re1coded.cyberstuff.CyberStuff;
import ru.re1coded.cyberstuff.blocks.ReplicatorBlock;
import ru.re1coded.cyberstuff.items.ImplantItem;
import ru.re1coded.cyberstuff.items.RemovalSyringeItem;
import ru.re1coded.cyberstuff.items.SyringeItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(
            CyberStuff.MODID
    );

    public static final DeferredItem<Item> SYRINGE = ITEMS.registerItem("syringe",
            SyringeItem::new);

    public static final DeferredItem<Item> SYRINGE_USED = ITEMS.registerSimpleItem("syringe_used");

    public static final DeferredItem<Item> REMOVAL_SYRINGE =
            ITEMS.registerItem("removal_syringe", RemovalSyringeItem::new);

    public static final DeferredItem<Item> NANOBOT_VIAL = ITEMS.registerSimpleItem("nanobot_vial");

    public static final DeferredItem<Item> NANOBOT_VIAL_USED = ITEMS.registerSimpleItem("vial_used");

    public static final DeferredItem<Item> MIXED_IRON = ITEMS.registerSimpleItem("mixed_iron");

    public static final DeferredItem<Item> IMPLANT = ITEMS.registerItem("implant", ImplantItem::new);

    public static final DeferredItem<BlockItem> REPLICATOR = ITEMS.registerSimpleBlockItem("replicator", ModBlocks.REPLICATOR);


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
