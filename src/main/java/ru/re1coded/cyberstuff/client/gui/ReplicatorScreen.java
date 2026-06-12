package ru.re1coded.cyberstuff.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import ru.re1coded.cyberstuff.CyberStuff;
import ru.re1coded.cyberstuff.menu.ReplicatorMenu;
import ru.re1coded.cyberstuff.register.ModMenuTypes;

public class ReplicatorScreen extends AbstractContainerScreen<ReplicatorMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(CyberStuff.MODID, "textures/gui/replicator_gui.png");

    public ReplicatorScreen(ReplicatorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 175, 175);

    }

    @Override
    protected void init() {
        super.init();

        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = 6;
    }

    public static void register(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.REPLICATOR_MENU.get(), ReplicatorScreen::new);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                this.leftPos, this.topPos,
                0, 0,
                this.imageWidth, this.imageHeight,
                256, 256
        );
    }
}
