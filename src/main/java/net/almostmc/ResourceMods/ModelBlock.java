package net.almostmc.ResourceMods;

import org.bukkit.Material;

public class ModelBlock extends Model {
    public final Material hiddenBlockMaterial;

    public ModelBlock(Material customModelDataMaterial, int CustomModelData, Material hiddenBlockMaterial) {
        super(customModelDataMaterial, CustomModelData);
        this.hiddenBlockMaterial = hiddenBlockMaterial;
    }
}
