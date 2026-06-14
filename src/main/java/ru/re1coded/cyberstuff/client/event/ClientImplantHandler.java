package ru.re1coded.cyberstuff.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import ru.re1coded.cyberstuff.CyberStuff;
import ru.re1coded.cyberstuff.attachments.ModAttachments;
import ru.re1coded.cyberstuff.client.gui.ImplantScreen;
import ru.re1coded.cyberstuff.client.gui.ImplantScreenMode;
import ru.re1coded.cyberstuff.client.keymappings.ModKeyBindings;
import ru.re1coded.cyberstuff.data.ImplantData;
import ru.re1coded.cyberstuff.data.ImplantRegistry;
import ru.re1coded.cyberstuff.data.ImplantSlots;
import ru.re1coded.cyberstuff.effects.IImplantEffect;
import ru.re1coded.cyberstuff.network.ActivateImplantPacket;
import ru.re1coded.cyberstuff.network.RequestSyncImplantSlotsPacket;

@EventBusSubscriber(value = Dist.CLIENT, modid = CyberStuff.MODID)
public class ClientImplantHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // Кнопки — всегда проверяем первыми
        while (ModKeyBindings.ACTIVATE_IMPLANT.get().consumeClick()) {
            ClientPacketDistributor.sendToServer(new ActivateImplantPacket());
        }

        while (ModKeyBindings.OPEN_IMPLANTS.get().consumeClick()) {
            if (mc.screen == null) {
                // Сначала запрашиваем синхронизацию
                ClientPacketDistributor.sendToServer(new RequestSyncImplantSlotsPacket());
                // Открываем экран
                mc.setScreen(new ImplantScreen(ImplantScreenMode.VIEW, null));
            }
        }

        // Траектория стрелы — только если игрок натягивает лук
        Player player = mc.player;
        if (!player.isUsingItem()) return;

        ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());
        for (ImplantData data : slots.getInstalled()) {
            ImplantRegistry.get(data.id()).ifPresent(def -> {
                for (IImplantEffect effect : def.effectList()) {
                    if (effect instanceof IImplantEffect.ArrowTrajectoryEffect trajectory) {
                        trajectory.renderTrajectory(player,
                                mc.getDeltaTracker().getGameTimeDeltaPartialTick(true));
                    }
                }
            });
        }
    }
}
