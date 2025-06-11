package com.arkflame.flamesoups.utils;

import org.bukkit.Material;

public class Materials {

    /*
     * Returns the first material to actually exist
     * 
     * @param materialNames The names of the materials to get
     */
    public static Material get(String ...materialNames) {
        if (materialNames == null) {
            return null;
        }
        for (String materialName : materialNames) {
            Material material = Material.getMaterial(materialName);
            if (material != null && material != Material.AIR) {
                return material;
            }
        }
        return null;
    }
    
}
