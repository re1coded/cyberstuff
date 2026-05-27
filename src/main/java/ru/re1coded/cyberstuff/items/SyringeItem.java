package ru.re1coded.cyberstuff.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import ru.re1coded.cyberstuff.component.ModDataComponent;
import ru.re1coded.cyberstuff.data.SyringeData;

import java.util.Objects;
import java.util.function.Consumer;


public class SyringeItem extends Item {

    private String implant_name;

    public SyringeItem(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.implant_name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else {
            return obj instanceof SyringeItem item
                    && Objects.equals(this.implant_name, item.implant_name);
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        SyringeData syringeData = itemStack.get(ModDataComponent.SYRINGE_BASIC.get());


        if (!level.isClientSide()) {
            if (syringeData != null) {

                itemStack.shrink(1);


                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEE_STING, SoundSource.BLOCKS);
            }
        }

        ItemStack reward = new ItemStack(ModItems.SYRINGE_USED.get());
        player.addItem(reward);

        // TODO: Play sound
        // TODO: Code and apply effect

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        SyringeData syringeData = itemStack.get(ModDataComponent.SYRINGE_BASIC.get());
        if (syringeData == null) return;
        //description
        builder.accept(Component.translatable("tooltip.cyberstuff.implant.desc." + syringeData.implantName().getPath()).withStyle(ChatFormatting.GRAY));
    }
}
