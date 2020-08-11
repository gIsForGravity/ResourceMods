package net.almostmc.ResourceMods;

public class ResourceMaterial {
    public final String pluginName;
    public final String id;
    public final Model itemModel;
    public final Model blockModel;

    protected ResourceMaterial(String pluginName, String id, Model itemModel, Model blockModel) {
        this.pluginName = pluginName;
        this.id = id;
        this.itemModel = itemModel;
        this.blockModel = blockModel;
    }
}
