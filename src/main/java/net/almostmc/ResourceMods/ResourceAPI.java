package net.almostmc.ResourceMods;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

public abstract class ResourceAPI {
    public static ResourceAPI singleton;

    public void setup() {
        singleton = this;
    }

    public ResourceMaterial registerBlock(Plugin plugin, String blockID, Material baseResource, int customModelData) {
        return registerBlock(plugin, blockID, new Model(baseResource, customModelData));
    }

    public abstract ResourceMaterial registerBlock(Plugin plugin, String blockID, Model itemModel, Model blockModel);

    public abstract ResourceMaterial registerBlock(Plugin plugin, String blockID, Model itemModel);

    public abstract ResourceMaterial checkBlock(Location location);

    /*\/**
     * @deprecated
     * Gets registered material.
     * @param plugin Plugin that registered resource. The plugin's name is the first part of fullID.
     * @param id ID of registered resource. Second part of fullID.
     * @return Returns the {@link ResourceMaterial} connected to the id. Returns {@link ResourceMaterial#nullMaterial} if there is no registered material with this id and plugin.
     *\/
    public abstract ResourceMaterial getMaterialFromId(Plugin plugin, String id);*/
    /**
     * Gets registered material.
     * @param pluginName Name of plugin that registered resource. First part of fullID.
     * @param id ID of registered resource. Second part of fullID.
     * @return Returns the {@link ResourceMaterial} connected to the id. Returns null if there is no registered material with this id and plugin.
     */
    public abstract ResourceMaterial getMaterialFromId(String pluginName, String id);
    /**
     * Gets material using input pluginName:materialID
     * @param fullID pluginName:materialID
     * @return Returns the {@link ResourceMaterial} connected to the id. Returns null if there is no registered material with this fullID.
     */
    public abstract ResourceMaterial getMaterialFromId(String fullID);

    public abstract void setMaterialAtLocation(ResourceMaterial mat, Location location);

    public abstract void setMaterialAtLocation(String fullID, Location location);
}
