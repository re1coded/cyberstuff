package ru.re1coded.cyberstuff.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.re1coded.cyberstuff.CyberStuff;
import ru.re1coded.cyberstuff.blocks.ReplicatorBlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CyberStuff.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReplicatorBlockEntity>> REPLICATOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("replicator_block_entity",
            () -> new BlockEntityType<>(
                    ReplicatorBlockEntity::new,
                    false,
                    ModBlocks.REPLICATOR.get()
    ));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
