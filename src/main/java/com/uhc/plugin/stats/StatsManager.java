package com.uhc.plugin.stats;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public class StatsManager {

    private final JavaPlugin plugin;
    private final Map<String, PlayerStats> playerStats = new HashMap<>();
    private final File statsFile;

    public StatsManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.statsFile = new File(plugin.getDataFolder(), "stats.dat");
        loadStats();
    }

    public PlayerStats getOrCreateStats(Player player) {
        String playerName = player.getName();
        if (!playerStats.containsKey(playerName)) {
            playerStats.put(playerName, new PlayerStats(playerName));
        }
        return playerStats.get(playerName);
    }

    public PlayerStats getStats(String playerName) {
        return playerStats.getOrDefault(playerName, new PlayerStats(playerName));
    }

    public void recordKill(Player killer, Player killed) {
        PlayerStats killerStats = getOrCreateStats(killer);
        PlayerStats killedStats = getOrCreateStats(killed);

        killerStats.addKill();
        killedStats.addDeath();

        // Update ELO based on kills
        int eloGain = calculateEloGain(killerStats.getElo(), killedStats.getElo());
        killerStats.setElo(killerStats.getElo() + eloGain);
        killedStats.setElo(Math.max(0, killedStats.getElo() - eloGain));

        plugin.getLogger().info(killer.getName() + " killed " + killed.getName() + 
                " | ELO: " + killer.getName() + " +" + eloGain);
    }

    public void recordWin(Player winner) {
        PlayerStats stats = getOrCreateStats(winner);
        stats.addWin();
    }

    public void recordLoss(Player loser) {
        PlayerStats stats = getOrCreateStats(loser);
        stats.addLoss();
    }

    private int calculateEloGain(int winnerElo, int loserElo) {
        double expectedScore = 1.0 / (1.0 + Math.pow(10.0, (loserElo - winnerElo) / 400.0));
        return Math.max(1, (int) Math.round(32 * (1 - expectedScore)));
    }

    public List<PlayerStats> getTopPlayers(int limit) {
        List<PlayerStats> topPlayers = new ArrayList<>(playerStats.values());
        topPlayers.sort((a, b) -> Integer.compare(b.getElo(), a.getElo()));
        return topPlayers.subList(0, Math.min(limit, topPlayers.size()));
    }

    public void saveStats() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(statsFile))) {
            oos.writeObject(playerStats);
            plugin.getLogger().info("Player stats saved!");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save player stats: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadStats() {
        if (!statsFile.exists()) {
            plugin.getLogger().info("No stats file found. Creating new stats.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(statsFile))) {
            Map<String, PlayerStats> loadedStats = (Map<String, PlayerStats>) ois.readObject();
            playerStats.putAll(loadedStats);
            plugin.getLogger().info("Player stats loaded! (" + playerStats.size() + " players)");
        } catch (IOException | ClassNotFoundException e) {
            plugin.getLogger().severe("Failed to load player stats: " + e.getMessage());
        }
    }

    public Map<String, PlayerStats> getAllStats() {
        return new HashMap<>(playerStats);
    }
}
