package ru.re1coded.cyberstuff.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.*;

public class ImplantSlots {
    public static final int MAX_SLOTS = 6;

    private final Map<Integer, ImplantData> slots = new HashMap<>();
    private final Map<Identifier, Integer> cooldowns = new HashMap<>();
    private final Set<Identifier> activeToggles = new HashSet<>();

    @Nullable
    private PendingImplant pendingImplant = null;

    public Optional<PendingImplant> getPendingImplant() {
        return Optional.ofNullable(pendingImplant);
    }

    public void setPendingImplant(@Nullable PendingImplant id) {
        pendingImplant = id;
    }

    public Optional<ImplantData> get(int slot) {
        return Optional.ofNullable(slots.get(slot));
    }

    public boolean install(int slot, ImplantData data) {
        if (slot < 0 || slot >= MAX_SLOTS) return false;
        if (slots.containsKey(slot)) return false;
        slots.put(slot, data);
        return true;
    }

    public Optional<ImplantData> remove(int slot, ServerPlayer player) {
        ImplantData old = slots.remove(slot);
        if (old != null) {
            ImplantRegistry.get(old.id()).ifPresent(def -> def.removeAll(player));
            for (ImplantData remaining : slots.values()) {
                ImplantRegistry.get(remaining.id()).ifPresent(def ->
                        def.applyAll(player, remaining.rarity(), def.isUnique())
                );
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
        return slots.values().stream()
                .anyMatch(data -> data.id().equals(id));
    }

    public List<ImplantData> getInstalled() {
        return slots.values().stream().toList();
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

    private boolean doubleJumpUsed = false;

    public boolean isDoubleJumpUsed() { return doubleJumpUsed; }
    public void setDoubleJumpUsed(boolean used) { doubleJumpUsed = used; }

    private boolean wasOnGround = true;

    public boolean wasOnGround() { return wasOnGround; }

    public void setWasOnGround(boolean value) { wasOnGround = value; }

    private static final Codec<Map<Integer, ImplantData>> SLOTS_CODEC =
            Codec.unboundedMap(
                    Codec.STRING.xmap(Integer::parseInt, String::valueOf),
                    ImplantData.CODEC
            );

    public static final Codec<ImplantSlots> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    SLOTS_CODEC
                            .optionalFieldOf("slots", Map.of())
                            .forGetter(s -> Map.copyOf(s.slots)),

                    Codec.unboundedMap(Identifier.CODEC, Codec.INT)
                            .optionalFieldOf("cooldowns", Map.of())
                            .forGetter(s -> Map.copyOf(s.cooldowns)),

                    Codec.list(Identifier.CODEC)
                            .optionalFieldOf("active_toggles", List.of())
                            .forGetter(s -> List.copyOf(s.activeToggles)),

                    Codec.BOOL
                            .optionalFieldOf("double_jump_used", false)
                            .forGetter(s -> s.doubleJumpUsed),

                    PendingImplant.CODEC
                            .optionalFieldOf("pending_implant")
                            .forGetter(s -> Optional.ofNullable(s.pendingImplant))

            ).apply(instance, (slotsMap, cdMap, toggleList, hasDoubleJump, pendingImplant) -> {
                ImplantSlots result = new ImplantSlots();
                result.slots.putAll(slotsMap);
                result.cooldowns.putAll(cdMap);
                result.activeToggles.addAll(toggleList);
                result.doubleJumpUsed = hasDoubleJump;
                result.pendingImplant = pendingImplant.orElse(null);
                return result;
            })
    );
}
