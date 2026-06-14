package ru.re1coded.cyberstuff.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import ru.re1coded.cyberstuff.CyberStuff;
import ru.re1coded.cyberstuff.attachments.ModAttachments;
import ru.re1coded.cyberstuff.data.ImplantData;
import ru.re1coded.cyberstuff.data.ImplantRegistry;
import ru.re1coded.cyberstuff.data.ImplantSlots;
import ru.re1coded.cyberstuff.effects.IActiveImplantEffect;
import ru.re1coded.cyberstuff.register.ModItems;

import java.util.List;

@EventBusSubscriber(modid = CyberStuff.MODID, value = Dist.CLIENT)
public class ImplantHUDRenderer {

    // Размеры одного слота HUD
    private static final int SLOT_SIZE = 24;
    private static final int SLOT_PADDING = 4;
    private static final int ICON_SIZE = 16;
    private static final int ICON_OFFSET = (SLOT_SIZE - ICON_SIZE) / 2; // центрирование иконки

    @SubscribeEvent
    public static void onRenderHud(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        Player player = mc.player;
        // Клиентский игрок — получаем данные через attachment
        ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());

        // Собираем только активируемые импланты
        List<ImplantData> activeImplants = slots.getInstalled().stream()
                .filter(data -> ImplantRegistry.get(data.id())
                        .map(def -> def.effectList().stream()
                                .anyMatch(e -> e instanceof IActiveImplantEffect))
                        .orElse(false))
                .toList();

        if (activeImplants.isEmpty()) return;

        GuiGraphicsExtractor graphics = event.getGuiGraphics();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        // Позиция HUD — внизу по центру, над хотбаром
        int totalWidth = activeImplants.size() * (SLOT_SIZE + SLOT_PADDING) - SLOT_PADDING;
        int startX = (screenWidth - totalWidth) / 2 + 140;
        int startY = screenHeight - 60; // над хотбаром

        for (int i = 0; i < activeImplants.size(); i++) {
            ImplantData data = activeImplants.get(i);
            int x = startX + i * (SLOT_SIZE + SLOT_PADDING);
            int y = startY;

            // Фон слота — тёмный прямоугольник
            graphics.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0xAA000000);
            // Рамка слота
            graphics.fill(x, y, x + SLOT_SIZE, y + 1, 0xFF888888);             // top
            graphics.fill(x, y + SLOT_SIZE - 1, x + SLOT_SIZE, y + SLOT_SIZE, 0xFF888888); // bottom
            graphics.fill(x, y, x + 1, y + SLOT_SIZE, 0xFF888888);             // left
            graphics.fill(x + SLOT_SIZE - 1, y, x + SLOT_SIZE, y + SLOT_SIZE, 0xFF888888); // right

            // Иконка импланта — рисуем как предмет
            ItemStack iconStack = ImplantRegistry.createStack(
                    ModItems.IMPLANT.get(),
                    data.id(),
                    data.rarity()
            );
            graphics.item(iconStack, x + ICON_OFFSET, y + ICON_OFFSET);

            // Полоса кулдауна
            int cooldown = slots.getCooldown(data.id());
            ImplantRegistry.get(data.id()).ifPresent(def -> {
                // Ищем максимальный кулдаун для расчёта прогресса
                def.effectList().stream()
                        .filter(e -> e instanceof IActiveImplantEffect)
                        .map(e -> (IActiveImplantEffect) e)
                        .findFirst()
                        .ifPresent(active -> {
                            int maxCooldown = active.getCooldown(data.rarity(), def.isUnique());
                            if (cooldown > 0 && maxCooldown > 0) {
                                float progress = (float) cooldown / maxCooldown;
                                int overlayHeight = (int)(SLOT_SIZE * progress);

                                // Тёмный оверлей поверх иконки — показывает оставшийся кулдаун
                                graphics.fill(
                                        x + 1, y + 1,
                                        x + SLOT_SIZE - 1, y + 1 + overlayHeight,
                                        0xAA000000
                                );

                                // Текст с оставшимися секундами
                                int secondsLeft = cooldown / 20 + 1;
                                String cooldownText = String.valueOf(secondsLeft);
                                int textX = x + SLOT_SIZE / 2 - mc.font.width(cooldownText) / 2;
                                int textY = y + SLOT_SIZE / 2 - 4;
                                graphics.text(mc.font, cooldownText, textX, textY, 0xFFFFFF, true);
                            }
                        });
            });

            // Подсказка с названием импланта под слотом
            String name = Component.translatable(
                   "tooltip.cyberstuff.implant.desc." + data.id().getPath()
            ).getString();
            int nameX = x + SLOT_SIZE / 2 - mc.font.width(name) / 2;
            graphics.text(mc.font, name, nameX, y + SLOT_SIZE + 2, 0xFFFFFF, true);
        }
    }
}
