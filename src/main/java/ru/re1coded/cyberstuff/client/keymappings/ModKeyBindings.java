package ru.re1coded.cyberstuff.client.keymappings;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;
import ru.re1coded.cyberstuff.CyberStuff;

public class ModKeyBindings {

    public static final KeyMapping.Category CYBERSTUFF_CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(CyberStuff.MODID, "category"));


    public static final Lazy<KeyMapping> ACTIVATE_IMPLANT = Lazy.of(() ->
            new KeyMapping(
                    "key.cyberstuff.activate_implant",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_Y,
                    CYBERSTUFF_CATEGORY
            )
            );

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.registerCategory(CYBERSTUFF_CATEGORY);
        event.register(ACTIVATE_IMPLANT.get());
    }
}
