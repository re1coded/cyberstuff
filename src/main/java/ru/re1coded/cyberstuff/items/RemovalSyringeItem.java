package ru.re1coded.cyberstuff.items;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import ru.re1coded.cyberstuff.client.gui.ImplantScreen;
import ru.re1coded.cyberstuff.client.gui.ImplantScreenMode;
import ru.re1coded.cyberstuff.network.RequestSyncImplantSlotsPacket;

public class RemovalSyringeItem extends Item {

    public RemovalSyringeItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            ClientPacketDistributor.sendToServer(new RequestSyncImplantSlotsPacket());
            Minecraft.getInstance().setScreen(new ImplantScreen(ImplantScreenMode.REMOVAL, hand));
        }
        return InteractionResult.SUCCESS;
    }
}
