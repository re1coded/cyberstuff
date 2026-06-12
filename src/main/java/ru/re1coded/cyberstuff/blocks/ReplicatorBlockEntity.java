package ru.re1coded.cyberstuff.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import ru.re1coded.cyberstuff.CyberStuff;
import ru.re1coded.cyberstuff.component.ModDataComponent;
import ru.re1coded.cyberstuff.data.ImplantData;
import ru.re1coded.cyberstuff.data.SyringeData;
import ru.re1coded.cyberstuff.items.ImplantItem;
import ru.re1coded.cyberstuff.menu.ReplicatorMenu;
import ru.re1coded.cyberstuff.register.ModBlockEntities;
import ru.re1coded.cyberstuff.register.ModItems;

public class ReplicatorBlockEntity extends BaseContainerBlockEntity implements MenuProvider {

    private final SimpleContainer inventory = new SimpleContainer(5) {
        @Override
        public void setChanged() {
            super.setChanged();
            ReplicatorBlockEntity.this.setChanged();
            tryAssemble();
        }
    };

    // Слоты
    public static final int SLOT_INPUT      = 0; // имплант или золотое яблоко
    public static final int SLOT_SYRINGE    = 1; // пустой шприц
    public static final int SLOT_NANOBOTS   = 2; // колба с наноботами
    public static final int SLOT_OUTPUT     = 3; // результат
    public static final int SLOT_REMAINDER  = 4; // пустая колба



    public ReplicatorBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModBlockEntities.REPLICATOR_BLOCK_ENTITY.get(), worldPosition, blockState);
    }

    // Логика сборки — вызывается при каждом изменении инвентаря
    private void tryAssemble() {
        ItemStack input    = inventory.getItem(SLOT_INPUT);
        ItemStack syringe  = inventory.getItem(SLOT_SYRINGE);
        ItemStack nanobots = inventory.getItem(SLOT_NANOBOTS);
        ItemStack output   = inventory.getItem(SLOT_OUTPUT);
        ItemStack remainder = inventory.getItem(SLOT_REMAINDER);

        // Выходные слоты должны быть пустыми
        if (!output.isEmpty() || !remainder.isEmpty()) return;

        // --- Рецепт 1: Имплант + шприц + наноботы = шприц с имплантом ---
        if (isImplant(input) && isSyringe(syringe) && isNanobots(nanobots)) {
            ImplantData data = input.get(ModDataComponent.IMPLANT_BASIC.get());
            if (data == null) return;

            // Создаём шприц с данными импланта
            ItemStack result = new ItemStack(ModItems.SYRINGE.get());
            result.set(ModDataComponent.SYRINGE_BASIC.get(),
                    new SyringeData(data.id(), data.rarity())
            );

            inventory.setItem(SLOT_OUTPUT, result);
            inventory.setItem(SLOT_REMAINDER, new ItemStack(ModItems.NANOBOT_VIAL_USED.get()));

            input.shrink(1);
            syringe.shrink(1);
            nanobots.shrink(1);
            return;
        }

        // --- Рецепт 2: Золотое яблоко + шприц = лечащий шприц ---
        if (isGoldenApple(input) && isSyringe(syringe) && nanobots.isEmpty()) {
            ItemStack result = new ItemStack(ModItems.SYRINGE.get());
            result.set(ModDataComponent.SYRINGE_BASIC.get(),
                    new SyringeData(
                            Identifier.fromNamespaceAndPath(CyberStuff.MODID, "healing"),
                            Rarity.UNCOMMON
                    )
            );

            inventory.setItem(SLOT_OUTPUT, result);

            input.shrink(1);
            syringe.shrink(1);
        }
    }

    private boolean isImplant(ItemStack stack) {
        return !stack.isEmpty()
                && stack.getItem() instanceof ImplantItem
                && stack.has(ModDataComponent.IMPLANT_BASIC.get());
    }

    private boolean isSyringe(ItemStack stack) {
        return !stack.isEmpty() && stack.is(ModItems.SYRINGE_USED.get());
    }

    private boolean isNanobots(ItemStack stack) {
        return !stack.isEmpty() && stack.is(ModItems.NANOBOT_VIAL.get());
    }

    private boolean isGoldenApple(ItemStack stack) {
        return !stack.isEmpty()
                && (stack.is(Items.GOLDEN_APPLE)
                || stack.is(Items.ENCHANTED_GOLDEN_APPLE));
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.cyberstuff.replicator");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
        for (int i = 0; i < 5; i++) items.set(i, inventory.getItem(i));
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> nonNullList) {
        for (int i = 0; i < nonNullList.size(); i++) {
            inventory.setItem(i, nonNullList.get(i));
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory playerInventory) {
        return new ReplicatorMenu(i, playerInventory, inventory);
    }

    @Override
    public int getContainerSize() {
        return 5;
    }
}
