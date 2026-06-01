package ru.re1coded.cyberstuff.data;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomModelData;
import ru.re1coded.cyberstuff.component.ModDataComponent;

import java.util.*;

public class ImplantRegistry {

    private static final Map<Identifier, ImplantDefinition> REGISTRY = new HashMap<>();

    public static void register(ImplantDefinition definition) {
        REGISTRY.put(definition.id(), definition);
    }

    public static Optional<ImplantDefinition> get(Identifier id) {
        return Optional.ofNullable(REGISTRY.get(id));
    }

    public static ImplantDefinition getOrThrow(Identifier id) {
        return get(id).orElseThrow(() ->
                new IllegalStateException("Unknown implant: " + id));
    }

    public static Collection<ImplantDefinition> getAll() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }

    public static ItemStack createStack(Item implant, Identifier id, Rarity rarity) {
        getOrThrow(id);
        ItemStack stack = new ItemStack(implant);
        stack.set(ModDataComponent.IMPLANT_BASIC.get(), new ImplantData(id, rarity));
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(), List.of(id.getPath()), List.of()));
        return stack;
    }

    private ImplantRegistry() {}
}