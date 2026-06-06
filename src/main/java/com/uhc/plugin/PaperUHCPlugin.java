package com.uhc.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import com.uhc.plugin.commands.UHCCommand;
import com.uhc.plugin.listeners.PlayerListener;
import com.uhc.plugin.listeners.WorldListener;
import com.uhc.plugin.managers.GameManager;
import com.uhc.plugin.managers.ConfigManager;
import com.uhc.plugin.managers.TeamManager;
import com.uhc.plugin.stats.StatsManager;
import com.uhc.plugin.stats.ScenarioManager;

public class PaperUHCPlugin extends JavaPlugin {

    private GameManager gameManager;
    private ConfigManager configManager;
    private TeamManager teamManager;
    private StatsManager statsManager;
    private ScenarioManager scenarioManager;

    @Override
    public void onEnable() {
        getLogger().info("========================================");
        getLogger().info("Paper UHC Plugin v" + getDescription().getVersion() + " Enabling...");
        getLogger().info("========================================");

        // Initialize config
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        // Initialize managers
        statsManager = new StatsManager(this);
        teamManager = new TeamManager(this);
        scenarioManager = new ScenarioManager(this);
        gameManager = new GameManager(this, teamManager, statsManager);

        // Register commands
        registerCommands();

        // Register listeners
        registerListeners();

        // Register shutdown hook to save stats
        Runtime.getRuntime().addShutdownHook(new Thread(statsManager::saveStats));

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

        // Save stats
        if (statsManager != null) {
            statsManager.saveStats();
        }

        getLogger().info("Paper UHC Plugin has been disabled!");
        getLogger().info("========================================");
    }

    private void registerCommands() {
        getCommand("uhc").setExecutor(new UHCCommand(this, gameManager, teamManager, statsManager, scenarioManager));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(gameManager, statsManager), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(gameManager), this);
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public StatsManager getStatsManager() {
        return statsManager;
    }

    public ScenarioManager getScenarioManager() {
        return scenarioManager;
    }
}