package ru.re1coded.cyberstuff.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import ru.re1coded.cyberstuff.attachments.ModAttachments;
import ru.re1coded.cyberstuff.data.ImplantData;
import ru.re1coded.cyberstuff.data.ImplantRegistry;
import ru.re1coded.cyberstuff.data.ImplantSlots;
import ru.re1coded.cyberstuff.network.RemoveImplantPacket;
import ru.re1coded.cyberstuff.register.ModItems;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImplantScreen extends Screen {

    private static final int PANEL_WIDTH = 200;
    private static final int PANEL_HEIGHT = 220;
    private static final int SLOT_SIZE = 24;
    private static final int SLOT_PADDING = 4;
    private static final int SLOTS_PER_ROW = 3;

    private final ImplantScreenMode mode;
    @Nullable
    private final InteractionHand hand; // только для REMOVAL

    private int leftPos;
    private int topPos;

    private List<ItemStack> cachedStacks = new ArrayList<>();
    private List<ImplantData> installedImplants = new ArrayList<>();

    @Nullable private ImplantData selectedImplant = null; // VIEW
    private int hoveredSlotIndex = -1;                    // оба режима

    public ImplantScreen(ImplantScreenMode mode, @Nullable InteractionHand hand) {
        super(mode == ImplantScreenMode.VIEW
                ? Component.translatable("screen.cyberstuff.implants")
                : Component.translatable("screen.cyberstuff.implant_removal")
        );
        this.mode = mode;
        this.hand = hand;
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - PANEL_WIDTH) / 2;
        this.topPos  = (this.height - PANEL_HEIGHT) / 2;

        // Кэшируем данные и стаки — один раз при открытии
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());
        installedImplants = slots.getInstalled();
        cachedStacks = installedImplants.stream()
                .map(data -> ImplantRegistry.createStack(
                        ModItems.IMPLANT.get(), data.id(), data.rarity()
                ))
                .collect(Collectors.toList());
    }

    // --- Утилитные методы ---

    private int getSlotX(int index) {
        return leftPos + 8 + (index % SLOTS_PER_ROW) * (SLOT_SIZE + SLOT_PADDING);
    }

    private int getSlotY(int index) {
        return topPos + 30 + (index / SLOTS_PER_ROW) * (SLOT_SIZE + SLOT_PADDING);
    }

    private void drawBorder(GuiGraphicsExtractor graphics, int x, int y,
                            int w, int h, int color) {
        graphics.fill(x,         y,         x + w,     y + 1,     color);
        graphics.fill(x,         y + h - 1, x + w,     y + h,     color);
        graphics.fill(x,         y,         x + 1,     y + h,     color);
        graphics.fill(x + w - 1, y,         x + w,     y + h,     color);
    }

    private boolean isMouseOverSlot(int index, double mouseX, double mouseY) {
        int x = getSlotX(index);
        int y = getSlotY(index);
        return mouseX >= x && mouseX <= x + SLOT_SIZE
                && mouseY >= y && mouseY <= y + SLOT_SIZE;
    }

    // --- Рендер ---

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX,
                                   int mouseY, float partialTick) {
        // Обновляем hovered слот
        hoveredSlotIndex = -1;
        for (int i = 0; i < installedImplants.size(); i++) {
            if (isMouseOverSlot(i, mouseX, mouseY)) {
                hoveredSlotIndex = i;
                break;
            }
        }

        drawPanel(graphics);
        drawHeader(graphics);
        drawSlots(graphics);
        drawInfoSection(graphics, mouseX, mouseY);

        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    private void drawPanel(GuiGraphicsExtractor graphics) {
        graphics.fill(leftPos, topPos,
                leftPos + PANEL_WIDTH, topPos + PANEL_HEIGHT, 0xCC000000);
        drawBorder(graphics, leftPos, topPos, PANEL_WIDTH, PANEL_HEIGHT, 0xFF888888);
    }

    private void drawHeader(GuiGraphicsExtractor graphics) {
        // Заголовок
        graphics.centeredText(this.font, this.title,
                leftPos + PANEL_WIDTH / 2, topPos + 6, 0xFFFFFF);

        // Подзаголовок — отличается по режиму
        Component hint = mode == ImplantScreenMode.VIEW
                ? Component.translatable("screen.cyberstuff.implants.select_hint")
                .withStyle(ChatFormatting.GRAY)
                : Component.translatable("screen.cyberstuff.implant_removal.hint")
                .withStyle(ChatFormatting.RED);

        graphics.centeredText(this.font, hint,
                leftPos + PANEL_WIDTH / 2, topPos + 16, 0xFFFFFF);

        // Разделитель
        graphics.fill(leftPos + 4, topPos + 26,
                leftPos + PANEL_WIDTH - 4, topPos + 27, 0xFF888888);
    }

    private void drawSlots(GuiGraphicsExtractor graphics) {
        for (int i = 0; i < ImplantSlots.MAX_SLOTS; i++) {
            int x = getSlotX(i);
            int y = getSlotY(i);

            // Фон слота
            graphics.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0xFF333333);
            drawBorder(graphics, x, y, SLOT_SIZE, SLOT_SIZE, 0xFF666666);

            if (i >= installedImplants.size()) continue;

            ImplantData data = installedImplants.get(i);

            // Подсветка — белая для VIEW, красная для REMOVAL
            if (i == hoveredSlotIndex) {
                int highlightColor = mode == ImplantScreenMode.VIEW
                        ? 0x44FFFFFF
                        : 0x66FF4444;
                graphics.fill(x + 1, y + 1,
                        x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, highlightColor);
            }

            // Подсветка выбранного слота в режиме VIEW
            if (mode == ImplantScreenMode.VIEW
                    && data.equals(selectedImplant)) {
                graphics.fill(x + 1, y + 1,
                        x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0x44FFFF00);
            }

            // Иконка
            graphics.item(cachedStacks.get(i), x + 4, y + 4);
        }
    }

    private void drawInfoSection(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        // Разделитель перед описанием
        graphics.fill(leftPos + 4, topPos + 100,
                leftPos + PANEL_WIDTH - 4, topPos + 101, 0xFF888888);

        ImplantData target = mode == ImplantScreenMode.VIEW
                ? selectedImplant
                : (hoveredSlotIndex >= 0 ? installedImplants.get(hoveredSlotIndex) : null);

        if (target == null) {
            // Подсказка если ничего не выбрано/не наведено
            MutableComponent hint = mode == ImplantScreenMode.VIEW
                    ? Component.translatable("screen.cyberstuff.implants.select_hint")
                    : Component.translatable("screen.cyberstuff.implant_removal.hint");
            graphics.centeredText(this.font, hint.withStyle(ChatFormatting.GRAY),
                    leftPos + PANEL_WIDTH / 2, topPos + 115, 0xFFFFFF);
            return;
        }

        int textX = leftPos + 8;
        int textY = topPos + 106;

        // Название с цветом редкости
        graphics.text(this.font,
                Component.translatable(
                        "tooltip.cyberstuff.implant.desc." + target.id().getPath()
                ).withStyle(target.rarity().color()),
                textX, textY, 0xFFFFFF, false
        );

        ImplantRegistry.get(target.id()).ifPresent(def -> {
            // Слот
            graphics.text(this.font,
                    Component.translatable(def.slotType().translationKey())
                            .withStyle(ChatFormatting.GRAY),
                    textX, textY + 12, 0xFFFFFF, false
            );

            // Редкость
            graphics.text(this.font,
                    Component.translatable(
                            "tooltip.cyberstuff.implant.rarity."
                                    + target.rarity().name().toLowerCase()
                    ).withStyle(target.rarity().color()),
                    textX, textY + 22, 0xFFFFFF, false
            );

            // hasBonus
            if (def.isUnique()) {
                graphics.text(this.font,
                        Component.translatable("tooltip.cyberstuff.implant.bonus")
                                .withStyle(ChatFormatting.GOLD),
                        textX, textY + 32, 0xFFFFFF, false
                );
            }

            // Описание эффектов с переносом строк
            Component desc = Component.translatable(
                    "tooltip.cyberstuff.implant.effect." + target.id().getPath()
            ).withStyle(ChatFormatting.GRAY);
            List<FormattedCharSequence> lines =
                    this.font.split(desc, PANEL_WIDTH - 16);
            for (int i = 0; i < lines.size(); i++) {
                graphics.text(this.font, lines.get(i),
                        textX, textY + 44 + i * 10, 0xFFFFFF, false);
            }

            // Подсказка о снятии в режиме REMOVAL
            if (mode == ImplantScreenMode.REMOVAL) {
                graphics.text(this.font,
                        Component.translatable("screen.cyberstuff.implant_removal.confirm")
                                .withStyle(ChatFormatting.RED),
                        textX, topPos + PANEL_HEIGHT - 14, 0xFFFFFF, false
                );
            }
        });
    }

    // --- Ввод ---

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() != 0) return super.mouseClicked(event, doubleClick);

        if (hoveredSlotIndex >= 0 && hoveredSlotIndex < installedImplants.size()) {
            if (mode == ImplantScreenMode.VIEW) {
                // Выбираем имплант для просмотра
                ImplantData clicked = installedImplants.get(hoveredSlotIndex);
                selectedImplant = clicked.equals(selectedImplant) ? null : clicked;
            } else {
                // Снимаем имплант
                ClientPacketDistributor.sendToServer(
                        new RemoveImplantPacket(hoveredSlotIndex, hand)
                );
                this.onClose();
            }
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    public void refreshImplants() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ImplantSlots slots = player.getData(ModAttachments.IMPLANT_SLOTS.get());
        installedImplants = slots.getInstalled();
        cachedStacks = installedImplants.stream()
                .map(data -> ImplantRegistry.createStack(
                        ModItems.IMPLANT.get(), data.id(), data.rarity()
                ))
                .collect(Collectors.toList());

        // Сбрасываем выбранный слот если он больше не существует
        if (selectedImplant != null && !installedImplants.contains(selectedImplant)) {
            selectedImplant = null;
        }
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
