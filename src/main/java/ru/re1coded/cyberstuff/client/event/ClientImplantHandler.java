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
import ru.re1coded.cyberstuff.client.gui.ImplantViewScreen;
import ru.re1coded.cyberstuff.client.keymappings.ModKeyBindings;
import ru.re1coded.cyberstuff.data.ImplantData;
import ru.re1coded.cyberstuff.data.ImplantRegistry;
import ru.re1coded.cyberstuff.data.ImplantSlots;
import ru.re1coded.cyberstuff.effects.IImplantEffect;
import ru.re1coded.cyberstuff.network.ActivateImplantPacket;

@EventBusSubscriber(value = Dist.CLIENT, modid = CyberStuff.MODID)
public class ClientImplantHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (ModKeyBindings.ACTIVATE_IMPLANT.get().consumeClick()) {
            ClientPacketDistributor.sendToServer(new ActivateImplantPacket());
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        Player player = mc.player;

        // Проверяем импланты только если игрок натягивает лук
        if (!player.isUsingItem()) return;

        // Получаем данные имплантов — на клиенте через getData тоже работает
        ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());
        for (ImplantData data : slots.getInstalled()) {
            ImplantRegistry.get(data.id()).ifPresent(def -> {
                for (IImplantEffect effect : def.effectList()) {
                    if (effect instanceof IImplantEffect.ArrowTrajectoryEffect trajectory) {
                        trajectory.renderTrajectory(player, mc.getDeltaTracker().getGameTimeDeltaPartialTick(true));
                    }
                }
            });
        }

        if (ModKeyBindings.OPEN_IMPLANTS.get().consumeClick()) {
            Minecraft.getInstance().setScreen(new ImplantViewScreen());
        }
    }
}
