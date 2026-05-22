package ru.re1coded.cyberstuff.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import ru.re1coded.cyberstuff.component.CustomDataComponent;

import java.util.Objects;


public class SyringeItem extends Item {

    private String type;

    private String implant_name;

    public SyringeItem(Item.Properties properties, String type, @Nullable String contains_implant) {
        super(properties);

        this.type = type;
        this.implant_name = contains_implant;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.implant_name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else {
            return obj instanceof SyringeItem item
                    && Objects.equals(this.type, item.type)
                    && Objects.equals(this.implant_name, item.implant_name);
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        SyringeData syringeData = itemStack.get(CustomDataComponent.SYRINGE_BASIC.get());

        if (syringeData != null) {
            String typeData = syringeData.type();
            String implantNameData = syringeData.implantName();

            // do something
        }

        // TODO: Play sound
        // TODO: Code and apply effect
        itemStack.shrink(1);
        return InteractionResult.CONSUME;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }
 }
