package ru.re1coded.cyberstuff.attachments;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import ru.re1coded.cyberstuff.CyberStuff;
import ru.re1coded.cyberstuff.data.ImplantSlots;

public class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CyberStuff.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ImplantSlots>> IMPLANT_SLOTS = ATTACHMENT_TYPES.register("implant_slot", () ->
            AttachmentType.<ImplantSlots>builder(ImplantSlots::new).serialize(ImplantSlots.CODEC.fieldOf("implant_slots")).copyOnDeath().build());

    private ModAttachments() {}
}
