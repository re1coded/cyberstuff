package ru.re1coded.cyberstuff.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class ImplantSlots {
    public static final int MAX_SLOTS = 6;

    private final ImplantData[] slots = new ImplantData[MAX_SLOTS];
    private final Map<Identifier, Integer> cooldowns = new HashMap<>();
    private final Set<Identifier> activeToggles = new HashSet<>();

    public Optional<ImplantData> get(int slot) {
        return Optional.ofNullable(slots[slot]);
    }

    public boolean install(int slot, ImplantData implant) {
        if (slot < 0 || slot >= MAX_SLOTS) return false;
        if (slots[slot] != null) return false;
        slots[slot] = implant;
        return true;
    }

    public Optional<ImplantData> remove(int slot, ServerPlayer player) {
        if (slot < 0 || slot >= MAX_SLOTS) return Optional.empty();
        ImplantData old = slots[slot];
        slots[slot] = null;

        if (old != null) {
            ImplantRegistry.get(old.id()).ifPresent(def -> def.removeAll(player));

            for (ImplantData remaining : getInstalled()) {
                ImplantRegistry.get(remaining.id()).ifPresent(def ->
                        def.applyAll(player, remaining.rarity(), ImplantRegistry.getOrThrow(remaining.id()).isUnique()));
            }
        }

        return Optional.ofNullable(old);
    }

    public boolean isToggled(Identifier implantId) {
        return activeToggles.contains(implantId);
    }

    public void toggle(Identifier implantId) {
        if (activeToggles.contains(implantId)) {
            activeToggles.remove(implantId);
        } else {
            activeToggles.add(implantId);
        }
    }

    public void deactivateToggle(Identifier implantId) {
        activeToggles.remove(implantId);
    }

    public boolean hasImplant(Identifier id) {
        return Arrays.stream(slots)
                .filter(Objects::nonNull)
                .anyMatch(data -> data.id().equals(id));
    }

    public List<ImplantData> getInstalled() {
        return Arrays.stream(slots).filter(Objects::nonNull).toList();
    }

    public void setCooldown(Identifier implantId, int ticks) {
        cooldowns.put(implantId, ticks);
    }

    public int getCooldown(Identifier implantId) {
        return cooldowns.getOrDefault(implantId, 0);
    }

    public boolean isOnCooldown(Identifier implantId) {
        return getCooldown(implantId) > 0;
    }

    // Вызывается каждый тик для уменьшения кулдаунов
    public void tickCooldowns() {
        cooldowns.replaceAll((id, ticks) -> Math.max(0, ticks - 1));
    }

    public static final Codec<ImplantSlots> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.list(ImplantData.CODEC.optionalFieldOf("data")
                                    .codec()
                                    .xmap(opt -> opt.orElse(null), Optional::ofNullable))
                            .optionalFieldOf("slots", Collections.nCopies(MAX_SLOTS, null))
                            .forGetter(s -> Arrays.asList(s.slots)),

                    Codec.unboundedMap(Identifier.CODEC, Codec.INT)
                            .optionalFieldOf("cooldowns", Map.of())
                            .forGetter(s -> Map.copyOf(s.cooldowns)),

                    Codec.list(Identifier.CODEC)
                            .optionalFieldOf("active_toggles", List.of())
                            .forGetter(s -> List.copyOf(s.activeToggles))

            ).apply(instance, (slotList, cdMap, toggleList) -> {
                ImplantSlots result = new ImplantSlots();
                for (int i = 0; i < Math.min(slotList.size(), MAX_SLOTS); i++) {
                    result.slots[i] = slotList.get(i);
                }
                result.cooldowns.putAll(cdMap);
                result.activeToggles.addAll(toggleList);
                return result;
            })
    );
}
