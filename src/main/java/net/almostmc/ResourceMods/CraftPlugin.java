package net.almostmc.ResourceMods;

import net.almostmc.ResourceMods.Database.DBManagerH2;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Main class
 */
public class CraftPlugin extends JavaPlugin {
    public static final String NMS_VERSION = Bukkit.getServer().getClass().getPackage().getName().substring(23);

    public static CraftPlugin getInstance() {return instance;}
    private static CraftPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        rImplementation.setup();
    }

    private ResourceAPI rImplementation = new ResourceAPI() {
        @Override
        public void setup() {
            super.setup();
            resourceMapById = new HashMap<>();
        }

        // Hashmap of all custom materials located by pluginName.blockID similarly to Minecraft's builtin namespace:blockID
        private HashMap<String, HashMap<String, ResourceMaterial>> resourceMapById;

        // Hashmap of all custom materials located by base material that is replaced and the CustomModelData integer. Used
        // for getting custom materials by position.
        private HashMap<Material, HashMap<Integer, ResourceMaterial>> resourceMapByModel;

        @Override
        public ResourceMaterial registerBlock(Plugin plugin, String blockID, Model itemModel, Model blockModel) {
            ResourceMaterial mat = new ResourceMaterial(plugin.getName(), blockID, itemModel, blockModel);

            HashMap<String, ResourceMaterial> idMap = new HashMap<>();
            idMap.put(blockID, mat);
            resourceMapById.put(mat.pluginName, idMap);

            HashMap<Integer, ResourceMaterial> modelMap = new HashMap<>();
            modelMap.put(itemModel.CustomModelData, mat);
            resourceMapByModel.put(itemModel.customModelDataMaterial, modelMap);

            return mat;
        }

        @Override
        public ResourceMaterial registerBlock(Plugin plugin, String blockID, Model itemModel) {
            return registerBlock(plugin, blockID, itemModel, null);
        }

        @Override
        public ResourceMaterial getMaterialFromId(String pluginName, String id) {
            return resourceMapById.get(pluginName).get(id);
        }

        @Override
        public ResourceMaterial getMaterialFromId(String fullID) throws IllegalArgumentException {
            if (fullID.contains(":")) {
                String[] idParts = fullID.split(Pattern.quote(":"));
                return getMaterialFromId(idParts[0], idParts[1]);
            } else {
                throw new IllegalArgumentException("String \"" + fullID + "\" does not contain \":\"");
            }
        }

        @Override
        public ResourceMaterial checkBlock(Location location) {
            return DBManagerH2.materialAtLocation(location);
        }

        @Override
        public void setMaterialAtLocation(ResourceMaterial mat, Location loc) {
            setArmorStandAtLocation(mat, loc);

            DBManagerH2.setDBMaterialAtLocation(mat, loc);
        }

        @Override
        public void setMaterialAtLocation(String fullID, Location loc) {
            setArmorStandAtLocation(getMaterialFromId(fullID), loc);

            DBManagerH2.setDBMaterialAtLocation(fullID, loc);
        }

        private void setArmorStandAtLocation(ResourceMaterial mat, Location loc) {
            var currentBlockMat = checkBlock(loc);
            if (currentBlockMat == null) {
                // Create armor stand holding custom block for model
                ArmorStand blockModelStand = (ArmorStand) Objects.requireNonNull(loc.getWorld()).spawnEntity(loc.subtract(0, 1, 0), EntityType.ARMOR_STAND);

                // Create custom block for armor stand to hold
                var customBlock = new ItemStack(mat.blockModel.customModelDataMaterial);
                var itemMeta = customBlock.getItemMeta();
                assert itemMeta != null;
                itemMeta.setCustomModelData(mat.blockModel.CustomModelData);
                customBlock.setItemMeta(itemMeta);

                // Change armor stand settings
                blockModelStand.setGravity(false);
                blockModelStand.setVisible(false);
                blockModelStand.setAI(false);
                blockModelStand.setArms(false);
                Objects.requireNonNull(blockModelStand.getEquipment()).setHelmet(customBlock);

                // Add scoreboard tag to easily find armor stand
                blockModelStand.addScoreboardTag("ResourceBlock");
                blockModelStand.addScoreboardTag(mat.pluginName + ":" + mat.id);
            } else {
                // Create armor stand holding custom block for model
                ArmorStand blockModelStand;
                var stands = Objects.requireNonNull(loc.getWorld()).getNearbyEntities(loc, 0, 1, 0);

                boolean noStand = false;
                if (stands.size() > 0)
                for (Entity stand : stands) {
                    if (stand.getScoreboardTags().contains("ResourceBlock")) {
                        for (String s : stand.getScoreboardTags()) stand.removeScoreboardTag(s);

                        blockModelStand = (ArmorStand) stand;

                        // Create custom block for armor stand to hold
                        var customBlock = new ItemStack(mat.blockModel.customModelDataMaterial);
                        var itemMeta = customBlock.getItemMeta();
                        assert itemMeta != null;
                        itemMeta.setCustomModelData(mat.blockModel.CustomModelData);
                        customBlock.setItemMeta(itemMeta);

                        // Change armor stand settings
                        blockModelStand.setGravity(false);
                        blockModelStand.setVisible(false);
                        blockModelStand.setAI(false);
                        blockModelStand.setArms(false);
                        Objects.requireNonNull(blockModelStand.getEquipment()).setHelmet(customBlock);

                        // Add scoreboard tag to easily find armor stand
                        blockModelStand.addScoreboardTag("ResourceBlock");
                        blockModelStand.addScoreboardTag(mat.pluginName + ":" + mat.id);

                        noStand = true;
                        break;
                    }
                }
                if (noStand) {
                    // Create armor stand holding custom block for model
                    blockModelStand = (ArmorStand) Objects.requireNonNull(loc.getWorld()).spawnEntity(loc.subtract(0, 1, 0), EntityType.ARMOR_STAND);

                    // Create custom block for armor stand to hold
                    var customBlock = new ItemStack(mat.blockModel.customModelDataMaterial);
                    var itemMeta = customBlock.getItemMeta();
                    assert itemMeta != null;
                    itemMeta.setCustomModelData(mat.blockModel.CustomModelData);
                    customBlock.setItemMeta(itemMeta);

                    // Change armor stand settings
                    blockModelStand.setGravity(false);
                    blockModelStand.setVisible(false);
                    blockModelStand.setAI(false);
                    blockModelStand.setArms(false);
                    Objects.requireNonNull(blockModelStand.getEquipment()).setHelmet(customBlock);

                    // Add scoreboard tag to easily find armor stand
                    blockModelStand.addScoreboardTag("ResourceBlock");
                    blockModelStand.addScoreboardTag(mat.pluginName + ":" + mat.id);
                }
            }
        }
    };
}
