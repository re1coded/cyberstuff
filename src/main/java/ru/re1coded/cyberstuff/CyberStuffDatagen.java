package ru.re1coded.cyberstuff;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = CyberStuff.MODID)
public class CyberStuffDatagen {

    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client clientEvent) {

    }
}
