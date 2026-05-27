package ru.re1coded.cyberstuff.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ImplantSlots {
    public static final int MAX_SLOTS = 6;

    private final ImplantData[] slots = new ImplantData[MAX_SLOTS];

    public Optional<ImplantData> get(int slot) {
        return Optional.ofNullable(slots[slot]);
    }

    public boolean install(int slot, ImplantData implant) {
        if (slot < 0 || slot >= MAX_SLOTS) return false;
        if (slots[slot] != null) return false;
        slots[slot] = implant;
        return true;
    }

    public Optional<ImplantData> remove(int slot) {
        if (slot < 0 || slot >= MAX_SLOTS) return Optional.empty();
        ImplantData old = slots[slot];
        slots[slot] = null;
        return Optional.ofNullable(old);
    }

    public List<ImplantData> getInstalled() {
        return Arrays.stream(slots).filter(Objects::nonNull).toList();
    }

    public static final Codec<ImplantSlots> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.list(ImplantData.CODEC.optionalFieldOf("data", null).codec())
                            .fieldOf("slots")
                            .forGetter(s -> Arrays.asList(s.slots))
            ).apply(instance, list -> {
                ImplantSlots result = new ImplantSlots();
                for (int i = 0; i < Math.min(list.size(), MAX_SLOTS); i++) {
                    result.slots[i] = list.get(i);
                }
                return result;
            })
    );
}
