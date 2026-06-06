package com.uhc.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import com.uhc.plugin.commands.UHCCommand;
import com.uhc.plugin.listeners.PlayerListener;
import com.uhc.plugin.listeners.WorldListener;
import com.uhc.plugin.managers.GameManager;
import com.uhc.plugin.managers.ConfigManager;

public class PaperUHCPlugin extends JavaPlugin {

    private GameManager gameManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        getLogger().info("========================================");
        getLogger().info("Paper UHC Plugin v" + getDescription().getVersion() + " Enabling...");
        getLogger().info("========================================");

        // Initialize config
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        // Initialize game manager
        gameManager = new GameManager(this);

        // Register commands
        registerCommands();

        // Register listeners
        registerListeners();

        getLogger().info("Paper UHC Plugin has been enabled!");
        getLogger().info("========================================");
    }

    @Override
    public void onDisable() {
        getLogger().info("========================================");
        getLogger().info("Paper UHC Plugin Disabling...");

        // Stop any active games
        if (gameManager != null && gameManager.isGameRunning()) {
            gameManager.stopGame();
        }

        getLogger().info("Paper UHC Plugin has been disabled!");
        getLogger().info("========================================");
    }

    private void registerCommands() {
        getCommand("uhc").setExecutor(new UHCCommand(this, gameManager));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(gameManager), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(gameManager), this);
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}