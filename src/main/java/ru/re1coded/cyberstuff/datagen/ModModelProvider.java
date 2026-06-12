package ru.re1coded.cyberstuff.datagen;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.PackOutput;
import ru.re1coded.cyberstuff.CyberStuff;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, CyberStuff.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        //itemModels.generateFlatItem(ModItems.SYRINGE.get(), ModelTemplates.FLAT_ITEM);


    }
}
