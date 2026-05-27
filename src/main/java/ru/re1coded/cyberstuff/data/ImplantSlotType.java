package ru.re1coded.cyberstuff.data;

public enum ImplantSlotType {
    SYSTEM,
    BRAIN,
    EYES,
    ARMS,
    LEGS,
    BLOOD_SYSTEM,
    NERVE_SYSTEM,
    SKELETON,
    SKIN;

    public String translationKey() {
        return "implant.slot." + name().toLowerCase();
    }
}
