package net.almostmc.ResourceMods.Database;

import net.almostmc.ResourceMods.CraftPlugin;
import net.almostmc.ResourceMods.ResourceMaterial;
import org.bukkit.Location;

public final class DBManagerH2 {
    private static final String h2FileExtension = ".h2";

    /**
     * Gets the material at the Location provided
     * @param location block to check
     * @return Returns a ResourceMaterial if there is one at a block, otherwise returns null.
     */
    public static ResourceMaterial materialAtLocation(Location location) {
        var db = new H2DB(CraftPlugin.getInstance().getDataFolder().getAbsolutePath() + location.getChunk().getX() + location.getChunk().getZ() + h2FileExtension);
        return db.checkEntry(location.getX(), location.getY(), location.getZ());
    }

    /**
     * Sets the material at the Location provided in the database. If you want to normally set the material, just use {@link net.almostmc.ResourceMods.ResourceAPI#setMaterialAtLocation(ResourceMaterial, Location)}.
     * @param material The material the block should be set to
     * @param location The location of the block that will be set
     */
    public static void setDBMaterialAtLocation(ResourceMaterial material, Location location) {
        setDBMaterialAtLocation(material.pluginName + ":" + material.id, location);
    }

    /**
     * Sets the material at the Location provided in the database. If you want to normally set the material, just use {@link net.almostmc.ResourceMods.ResourceAPI#setMaterialAtLocation(ResourceMaterial, Location)}.
     * @param fullID The fullID of the material the block should be set to
     * @param location The location of the block that will be set
     */
    public static void setDBMaterialAtLocation(String fullID, Location location) {
        var db = new H2DB(CraftPlugin.getInstance().getDataFolder().getAbsolutePath() + location.getChunk().getX() + location.getChunk().getZ() + h2FileExtension);
        return db.setMaterial(fullID, location.getX(), location.getY(), location.getZ());
    }

    private DBManagerH2() {throw new UnsupportedOperationException();} // Do NOT create an instance of this class
}
