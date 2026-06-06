package com.uhc.plugin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import com.uhc.plugin.managers.GameManager;
import com.uhc.plugin.stats.StatsManager;

public class PlayerListener implements Listener {

    private final GameManager gameManager;
    private final StatsManager statsManager;

    public PlayerListener(GameManager gameManager, StatsManager statsManager) {
        this.gameManager = gameManager;
        this.statsManager = statsManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (gameManager.isGameRunning()) {
            Player victim = event.getEntity();
            Player killer = victim.getKiller();

            // Record kill if there was a killer
            if (killer != null && gameManager.getPlayers().contains(killer)) {
                statsManager.recordKill(killer, victim);
                killer.sendMessage("§a§l[UHC] §r§aYou killed " + victim.getName() + "! +ELO");
            }

            // Remove player from game on death
            gameManager.removePlayer(victim);
            event.setDeathMessage("§c" + victim.getName() + "§r§c died!");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Remove player from game if they disconnect
        gameManager.removePlayer(event.getPlayer());
    }
}