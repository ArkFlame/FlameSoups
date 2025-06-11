package com.arkflame.flamesoups;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.arkflame.flamesoups.utils.Materials;

import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;

public class FlameSoups extends JavaPlugin implements Listener {

    private double healAmount;
    private boolean convertToBowl;

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();

        // Load configuration values
        loadConfig();

        // Register event listener
        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("FlameSoups has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("FlameSoups has been disabled!");
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        healAmount = config.getDouble("heal-amount", 8.0); // Default 4 hearts (8 health points)
        convertToBowl = config.getBoolean("convert-to-bowl", false); // Default false (clear item)
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        // Check if right-clicking with mushroom stew
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                && item != null && item.getType() == Materials.get(
                        "MUSHROOM_STEW", "MUSHROOM_SOUP", "STEW", "SOUP")) {

            // Check if player needs healing
            double currentHealth = getPlayerHealth(player);
            double maxHealth = getPlayerMaxHealth(player);

            if (currentHealth >= maxHealth) {
                return; // Player is already at full health
            }

            // Calculate new health (don't exceed max health)
            double newHealth = Math.min(currentHealth + healAmount, maxHealth);

            // Set player health
            setPlayerHealth(player, newHealth);

            // Handle item conversion/removal
            if (convertToBowl) {
                // Convert to bowl
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                    // Try to give bowl to player
                    ItemStack bowl = new ItemStack(Material.BOWL, 1);
                    if (player.getInventory().firstEmpty() != -1) {
                        player.getInventory().addItem(bowl);
                    } else {
                        // Drop bowl if inventory is full
                        player.getWorld().dropItemNaturally(player.getLocation(), bowl);
                    }
                } else {
                    // Replace single mushroom stew with bowl
                    player.setItemInHand(new ItemStack(Material.BOWL, 1));
                }
            } else {
                // Clear item (reduce amount or remove)
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    player.setItemInHand(null);
                }
            }

            // Cancel the event to prevent default behavior
            event.setCancelled(true);
        }
    }

    // Compatibility methods for different Bukkit versions
    private double getPlayerHealth(Player player) {
        return player.getHealth();
    }

    private double getPlayerMaxHealth(Player player) {
        // Fallback for older versions
        return player.getMaxHealth();
    }

    private void setPlayerHealth(Player player, double health) {
        player.setHealth(health);
    }
}
