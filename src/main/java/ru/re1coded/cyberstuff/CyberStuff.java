package ru.re1coded.cyberstuff;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.re1coded.cyberstuff.attachments.ModAttachments;
import ru.re1coded.cyberstuff.client.gui.ImplantScreen;
import ru.re1coded.cyberstuff.client.gui.ReplicatorScreen;
import ru.re1coded.cyberstuff.client.keymappings.ModKeyBindings;
import ru.re1coded.cyberstuff.data.ImplantSlots;
import ru.re1coded.cyberstuff.network.RemoveImplantPacket;
import ru.re1coded.cyberstuff.network.RequestSyncImplantSlotsPacket;
import ru.re1coded.cyberstuff.network.SyncImplantSlotsPacket;
import ru.re1coded.cyberstuff.register.*;
import ru.re1coded.cyberstuff.component.ModDataComponent;
import ru.re1coded.cyberstuff.data.ImplantDefinition;
import ru.re1coded.cyberstuff.data.ImplantRegistry;
import ru.re1coded.cyberstuff.events.ImplantEventHandler;
import ru.re1coded.cyberstuff.network.ActivateImplantPacket;

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
                    Rarity.EPIC
            ))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.SYRINGE.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
                output.accept(ModItems.MIXED_IRON.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
                output.accept(ModItems.SYRINGE_USED.get());
                output.accept(ModItems.REMOVAL_SYRINGE.get());
                output.accept(ModItems.NANOBOT_VIAL.get());
                output.accept(ModItems.NANOBOT_VIAL_USED.get());
                output.accept(ModItems.REPLICATOR.get());
                for (ImplantDefinition def : ImplantRegistry.getAll()) {
                    output.accept(ImplantRegistry.createStack(
                            ModItems.IMPLANT.get(),
                            def.id(),
                            Rarity.EPIC
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

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModEffects.register(modEventBus);

        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);


        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (CyberStuff) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        modEventBus.addListener(this::registerPayloads);
        modEventBus.addListener(ReplicatorScreen::register);
        modEventBus.addListener(ModKeyBindings::register);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        ModImplants.register(); // not a custom registry, more like an init for implants
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                ActivateImplantPacket.TYPE,
                ActivateImplantPacket.STREAM_CODEC,
                ImplantEventHandler::handleActivateImplant
        );

        registrar.playToServer(
                RemoveImplantPacket.TYPE,
                RemoveImplantPacket.STREAM_CODEC,
                ImplantEventHandler::handleRemoveImplant
        );

        registrar.playToClient(
                SyncImplantSlotsPacket.TYPE,
                SyncImplantSlotsPacket.STREAM_CODEC,
                (packet, context) -> context.enqueueWork(() -> {
                    Minecraft mc = Minecraft.getInstance();
                    if (mc.player == null) return;
                    // Обновляем данные на клиенте
                    mc.player.setData(ModAttachments.IMPLANT_SLOTS.get(), packet.slots());
                    // Обновляем экран если открыт
                    if (mc.screen instanceof ImplantScreen screen) {
                        screen.refreshImplants();
                    }
                })
        );

        registrar.playToServer(
                RequestSyncImplantSlotsPacket.TYPE,
                RequestSyncImplantSlotsPacket.STREAM_CODEC,
                (packet, context) -> context.enqueueWork(() -> {
                    if (!(context.player() instanceof ServerPlayer player)) return;
                    ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());
                    PacketDistributor.sendToPlayer(player, new SyncImplantSlotsPacket(slots));
                })
        );
    }
}
