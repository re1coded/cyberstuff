package ru.re1coded.cyberstuff.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.re1coded.cyberstuff.CyberStuff;
import ru.re1coded.cyberstuff.menu.ReplicatorMenu;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, CyberStuff.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<ReplicatorMenu>> REPLICATOR_MENU =
            MENU_TYPES.register("replicator_menu", () ->
                    IMenuTypeExtension.create(ReplicatorMenu::new)
            );

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
