package ru.re1coded.cyberstuff;

import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.*;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.re1coded.cyberstuff.attachments.ModAttachments;
import ru.re1coded.cyberstuff.blocks.ModBlocks;
import ru.re1coded.cyberstuff.component.ModDataComponent;
import ru.re1coded.cyberstuff.data.ImplantDefinition;
import ru.re1coded.cyberstuff.data.ImplantRegistry;
import ru.re1coded.cyberstuff.items.ModImplants;
import ru.re1coded.cyberstuff.items.ModItems;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CyberStuff.MODID)
public class CyberStuff {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "cyberstuff";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "cyberstuff" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a creative tab with the id "cyberstuff:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.cyberstuff")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ImplantRegistry.createStack(
                    ModItems.IMPLANT.get(),
                    Identifier.fromNamespaceAndPath(CyberStuff.MODID, "adrenaline_booster"),
                    Rarity.EPIC,
                    false
            ))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.SYRINGE.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
                output.accept(ModItems.SYRINGE_USED.get());
                for (ImplantDefinition def : ImplantRegistry.getAll()) {
                    output.accept(ImplantRegistry.createStack(
                            ModItems.IMPLANT.get(),
                            def.id(),
                            Rarity.EPIC,
                            false
                    ));
                }
            }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public CyberStuff(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        ModDataComponent.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);


        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (CyberStuff) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        ModImplants.register(); // not a custom registry, more like an init for implants
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
}
