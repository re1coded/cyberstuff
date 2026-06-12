package ru.re1coded.cyberstuff.client.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import ru.re1coded.cyberstuff.attachments.ModAttachments;
import ru.re1coded.cyberstuff.data.ImplantData;
import ru.re1coded.cyberstuff.data.ImplantRegistry;
import ru.re1coded.cyberstuff.data.ImplantSlots;
import ru.re1coded.cyberstuff.register.ModItems;

import javax.annotation.Nullable;
import java.util.List;

public class ImplantViewScreen extends Screen {

    private static final int PANEL_WIDTH = 200;
    private static final int PANEL_HEIGHT = 220;
    private static final int SLOT_SIZE = 24;
    private static final int SLOT_PADDING = 4;
    private static final int SLOTS_PER_ROW = 3;

    private int leftPos;
    private int topPos;

    // Выбранный имплант для показа описания
    @Nullable
    private ImplantData selectedImplant = null;

    public ImplantViewScreen() {
        super(Component.translatable("screen.cyberstuff.implants"));
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - PANEL_WIDTH) / 2;
        this.topPos = (this.height - PANEL_HEIGHT) / 2;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());
        List<ImplantData> installed = slots.getInstalled();

        // Фон панели
        graphics.fill(leftPos, topPos, leftPos + PANEL_WIDTH, topPos + PANEL_HEIGHT, 0xCC000000);
        // Рамка
        graphics.fill(leftPos, topPos, leftPos + PANEL_WIDTH, topPos + 1, 0xFF888888);
        graphics.fill(leftPos, topPos + PANEL_HEIGHT - 1, leftPos + PANEL_WIDTH, topPos + PANEL_HEIGHT, 0xFF888888);
        graphics.fill(leftPos, topPos, leftPos + 1, topPos + PANEL_HEIGHT, 0xFF888888);
        graphics.fill(leftPos + PANEL_WIDTH - 1, topPos, leftPos + PANEL_WIDTH, topPos + PANEL_HEIGHT, 0xFF888888);

        // Заголовок
        graphics.centeredText(
                this.font,
                this.title,
                leftPos + PANEL_WIDTH / 2,
                topPos + 6,
                0xFFFFFF
        );

        // Разделитель под заголовком
        graphics.fill(leftPos + 4, topPos + 18, leftPos + PANEL_WIDTH - 4, topPos + 19, 0xFF888888);

        // Слоты имплантов
        for (int i = 0; i < ImplantSlots.MAX_SLOTS; i++) {
            int row = i / SLOTS_PER_ROW;
            int col = i % SLOTS_PER_ROW;

            int x = leftPos + 8 + col * (SLOT_SIZE + SLOT_PADDING);
            int y = topPos + 24 + row * (SLOT_SIZE + SLOT_PADDING);

            // Фон слота
            graphics.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0xFF333333);
            // Рамка слота
            graphics.fill(x, y, x + SLOT_SIZE, y + 1, 0xFF666666);
            graphics.fill(x, y + SLOT_SIZE - 1, x + SLOT_SIZE, y + SLOT_SIZE, 0xFF666666);
            graphics.fill(x, y, x + 1, y + SLOT_SIZE, 0xFF666666);
            graphics.fill(x + SLOT_SIZE - 1, y, x + SLOT_SIZE, y + SLOT_SIZE, 0xFF666666);

            // Если слот занят — рисуем предмет
            if (i < installed.size()) {
                ImplantData data = installed.get(i);

                // Подсветка выбранного слота
                if (data.equals(selectedImplant)) {
                    graphics.fill(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0x44FFFFFF);
                }

                ItemStack stack = ImplantRegistry.createStack(
                        ModItems.IMPLANT.get(),
                        data.id(),
                        data.rarity()
                );
                graphics.item(stack, x + 4, y + 4);
            } else {
                // Пустой слот — показываем тип слота
                // (опционально, если хочешь показывать какой тип слота)
            }
        }

        // Разделитель перед описанием
        graphics.fill(leftPos + 4, topPos + 90, leftPos + PANEL_WIDTH - 4, topPos + 91, 0xFF888888);

        // Описание выбранного импланта
        if (selectedImplant != null) {
            ImplantRegistry.get(selectedImplant.id()).ifPresent(def -> {
                int textX = leftPos + 8;
                int textY = topPos + 96;

                // Название
                Component name = Component.translatable(
                        "tooltip.cyberstuff.implant.desc." + selectedImplant.id().getPath()
                ).withStyle(selectedImplant.rarity().color());
                graphics.text(this.font, name, textX, textY, 0xFFFFFF, false);

                // Слот
                Component slot = Component.translatable(def.slotType().translationKey())
                        .withStyle(ChatFormatting.GRAY);
                graphics.text(this.font, slot, textX, textY + 12, 0xFFFFFF, false);

                // Редкость
                Component rarity = Component.translatable(
                        "tooltip.cyberstuff.implant.rarity." + selectedImplant.rarity().name().toLowerCase()
                ).withStyle(selectedImplant.rarity().color());
                graphics.text(this.font, rarity, textX, textY + 24, 0xFFFFFF, false);

                // hasBonus метка
                if (def.isUnique()) {
                    graphics.text(
                            this.font,
                            Component.translatable("tooltip.cyberstuff.implant.bonus")
                                    .withStyle(ChatFormatting.GOLD),
                            textX, textY + 36, 0xFFFFFF, false
                    );
                }

                // Описание эффектов — из ключа перевода
                Component desc = Component.translatable(
                        "tooltip.cyberstuff.implant.effect." + selectedImplant.id().getPath()
                ).withStyle(ChatFormatting.GRAY);

                // Перенос строк для длинного описания
                List<FormattedCharSequence> lines = this.font.split(desc, PANEL_WIDTH - 16);
                for (int i = 0; i < lines.size(); i++) {
                    graphics.text(this.font, lines.get(i), textX, textY + 48 + i * 10, 0xFFFFFF, false);
                }
            });
        } else {
            // Подсказка если ничего не выбрано
            graphics.centeredText(
                    this.font,
                    Component.translatable("screen.cyberstuff.implants.select_hint")
                            .withStyle(ChatFormatting.GRAY),
                    leftPos + PANEL_WIDTH / 2,
                    topPos + 110,
                    0xFFFFFF
            );
        }

        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    // Клик по слоту — выбираем имплант
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return false;

        ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());
        List<ImplantData> installed = slots.getInstalled();

        for (int i = 0; i < ImplantSlots.MAX_SLOTS; i++) {
            int row = i / SLOTS_PER_ROW;
            int col = i % SLOTS_PER_ROW;

            int x = leftPos + 8 + col * (SLOT_SIZE + SLOT_PADDING);
            int y = topPos + 24 + row * (SLOT_SIZE + SLOT_PADDING);

            if (event.x() >= x && event.x() <= x + SLOT_SIZE
                    && event.y() >= y && event.y() <= y + SLOT_SIZE) {
                if (i < installed.size()) {
                    selectedImplant = installed.get(i);
                } else {
                    selectedImplant = null;
                }
                return true;
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
