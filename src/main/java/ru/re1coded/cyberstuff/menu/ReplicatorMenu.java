package ru.re1coded.cyberstuff.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import ru.re1coded.cyberstuff.blocks.ReplicatorBlockEntity;
import ru.re1coded.cyberstuff.items.ImplantItem;
import ru.re1coded.cyberstuff.register.ModItems;
import ru.re1coded.cyberstuff.register.ModMenuTypes;

public class ReplicatorMenu extends AbstractContainerMenu {
    private final Container container;

    public ReplicatorMenu(int containerID, Inventory playerInventory, Container container) {
        super(ModMenuTypes.REPLICATOR_MENU.get(), containerID);
        this.container = container;
        checkContainerSize(container, 5);
        addSlot(new Slot(container, ReplicatorBlockEntity.SLOT_INPUT, 80, 22) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof ImplantItem
                        || stack.is(Items.GOLDEN_APPLE)
                        || stack.is(Items.ENCHANTED_GOLDEN_APPLE)
                        || stack.is(Items.ECHO_SHARD)
                        || stack.is(Items.POTION);
            }
        });

        addSlot(new Slot(container, ReplicatorBlockEntity.SLOT_SYRINGE, 31, 34) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ModItems.SYRINGE_USED.get())
                        || stack.is(ModItems.NANOBOT_VIAL_USED.get());
            }
        });

        addSlot(new Slot(container, ReplicatorBlockEntity.SLOT_NANOBOTS, 31, 59) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ModItems.NANOBOT_VIAL.get());
            }
        });

        addSlot(new Slot(container, ReplicatorBlockEntity.SLOT_OUTPUT, 132, 34) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);
            }
        });

        addSlot(new Slot(container, ReplicatorBlockEntity.SLOT_REMAINDER, 132, 59) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        this.addStandardInventorySlots(
                playerInventory,
                8,
                94
        );
    }

    public ReplicatorMenu(int containerId, Inventory playerInventory,
                          FriendlyByteBuf buf) {
        this(containerId, playerInventory, new SimpleContainer(5));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(i);

        if (!slot.hasItem()) return result;

        ItemStack stack = slot.getItem();
        result = stack.copy();

        if (i < 5) {
            if (!moveItemStackTo(stack, 5, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // Из инвентаря — в подходящий слот блока
            if (stack.getItem() instanceof ImplantItem
                    || stack.is(Items.GOLDEN_APPLE)
                    || stack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
                if (!moveItemStackTo(stack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (stack.is(ModItems.SYRINGE_USED.get())) {
                if (!moveItemStackTo(stack, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (stack.is(ModItems.NANOBOT_VIAL.get())) {
                if (!moveItemStackTo(stack, 2, 3, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stack.getCount() == result.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stack);
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }
}
