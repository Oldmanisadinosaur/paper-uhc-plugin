package com.uhc.plugin.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.uhc.plugin.stats.StatsManager;

import java.util.HashSet;
import java.util.Set;

public class GameManager {

    private final JavaPlugin plugin;
    private final TeamManager teamManager;
    private final StatsManager statsManager;
    private boolean gameRunning = false;
    private Set<Player> players = new HashSet<>();
    private int gameTime = 0;
    private final int MAX_PLAYERS = 100;
    private final int WORLD_BORDER_SIZE = 5000;

    public GameManager(JavaPlugin plugin, TeamManager teamManager, StatsManager statsManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.statsManager = statsManager;
    }

    public boolean startGame() {
        if (gameRunning) {
            return false;
        }

        if (players.size() < 2) {
            plugin.getLogger().warning("Not enough players to start the game!");
            return false;
        }

        gameRunning = true;
        gameTime = 0;
        plugin.getLogger().info("UHC Game started with " + players.size() + " players!");

        // Notify all players
        String message = "§6§l[UHC] §rGame started! Good luck!";
        for (Player player : players) {
            player.sendMessage(message);
        }

        // Start game timer
        startGameTimer();

        return true;
    }

    public boolean stopGame() {
        if (!gameRunning) {
            return false;
        }

        gameRunning = false;
        String message = "§c§l[UHC] §rGame ended!";
        for (Player player : players) {
            player.sendMessage(message);
        }

        players.clear();
        teamManager.disbandAllTeams();
        plugin.getLogger().info("UHC Game stopped!");
        return true;
    }

    public boolean addPlayer(Player player) {
        if (gameRunning) {
            player.sendMessage("§c§l[UHC] §rCannot join: Game is already running!");
            return false;
        }

        if (players.size() >= MAX_PLAYERS) {
            player.sendMessage("§c§l[UHC] §rCannot join: Game is full!");
            return false;
        }

        players.add(player);
        statsManager.getOrCreateStats(player);
        player.sendMessage("§a§l[UHC] §rYou have joined the UHC game! (" + players.size() + "/" + MAX_PLAYERS + ")");
        broadcastMessage("§e" + player.getName() + "§r§6 has joined the game! (" + players.size() + "/" + MAX_PLAYERS + ")");
        return true;
    }

    public boolean removePlayer(Player player) {
        if (players.remove(player)) {
            teamManager.removePlayerFromTeam(player);
            if (gameRunning) {
                player.sendMessage("§a§l[UHC] §rYou have left the UHC game!");
                broadcastMessage("§e" + player.getName() + "§r§6 has left the game!");
            }
            return true;
        }
        return false;
    }

    private void startGameTimer() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (!gameRunning) return;

            gameTime++;

            // Broadcast time every 30 seconds
            if (gameTime % 600 == 0) {
                int minutes = gameTime / 20 / 60;
                broadcastMessage("§6§l[UHC] §r§6Game time: " + minutes + " minutes");
            }
        }, 0L, 1L);
    }

    public void broadcastMessage(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public Set<Player> getPlayers() {
        return new HashSet<>(players);
    }

    public int getPlayerCount() {
        return players.size();
    }

    public int getGameTime() {
        return gameTime;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public StatsManager getStatsManager() {
        return statsManager;
    }
}