package ru.re1coded.cyberstuff.data;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
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

    public static ItemStack createStack(Item implant, Identifier id, Rarity rarity, boolean hasBonus) {
        getOrThrow(id);
        ItemStack stack = new ItemStack(implant);
        stack.set(ModDataComponent.IMPLANT_BASIC.get(), new ImplantData(id, rarity, hasBonus));
        return stack;
    }

    private ImplantRegistry() {}
}