package ru.re1coded.cyberstuff.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import ru.re1coded.cyberstuff.component.ModDataComponent;
import ru.re1coded.cyberstuff.data.ImplantData;
import ru.re1coded.cyberstuff.data.ImplantRegistry;

import java.util.function.Consumer;

public class ImplantItem extends Item {
    public ImplantItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        ImplantData implantData = itemStack.get(ModDataComponent.IMPLANT_BASIC.get());
        if (implantData == null) return;
        //description
        builder.accept(Component.translatable("tooltip.cyberstuff.implant.desc." + implantData.id().getPath()).withStyle(ChatFormatting.GRAY));
        //rarity
        builder.accept(Component.translatable("tooltip.cyberstuff.implant.rarity." + implantData.rarity().name().toLowerCase()).withStyle(implantData.rarity().getStyleModifier()));
        //bonuses i guess?
        ImplantRegistry.get(implantData.id()).ifPresent(def -> {
            if (def.isUnique()) {
                builder.accept(Component.translatable("tooltip.cyberstuff.implant.unique").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD));
            }
        });
    }
}
