package com.uhc.plugin.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        plugin.getLogger().info("Configuration loaded successfully!");
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        plugin.getLogger().info("Configuration reloaded!");
    }

    public int getMaxPlayers() {
        return config.getInt("max-players", 100);
    }

    public int getWorldBorderSize() {
        return config.getInt("world-border-size", 5000);
    }

    public int getSpawnRadius() {
        return config.getInt("spawn-radius", 200);
    }

    public boolean isPvPEnabled() {
        return config.getBoolean("pvp-enabled", true);
    }

    public int getHealAmount() {
        return config.getInt("heal-amount", 4);
    }
}