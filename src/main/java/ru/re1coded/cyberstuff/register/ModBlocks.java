package ru.re1coded.cyberstuff.register;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.re1coded.cyberstuff.CyberStuff;
import ru.re1coded.cyberstuff.blocks.ReplicatorBlock;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CyberStuff.MODID);

    public static final DeferredBlock<Block> REPLICATOR = BLOCKS.registerBlock("replicator", ReplicatorBlock::new, () -> BlockBehaviour.Properties.of().strength(3.5f).requiresCorrectToolForDrops().noOcclusion());

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
