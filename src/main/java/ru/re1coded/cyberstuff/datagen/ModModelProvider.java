package ru.re1coded.cyberstuff.datagen;

import jdk.dynalink.Operation;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.resources.model.cuboid.ItemModelGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import ru.re1coded.cyberstuff.CyberStuff;
import ru.re1coded.cyberstuff.items.ModItems;

import java.util.Optional;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, CyberStuff.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        //itemModels.generateFlatItem(ModItems.SYRINGE.get(), ModelTemplates.FLAT_ITEM);


    }
}
