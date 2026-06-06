package com.uhc.plugin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import com.uhc.plugin.managers.GameManager;

public class PlayerListener implements Listener {

    private final GameManager gameManager;

    public PlayerListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (gameManager.isGameRunning()) {
            // Remove player from game on death
            gameManager.removePlayer(event.getEntity());
            event.setDeathMessage("§c" + event.getEntity().getName() + "§r§c died!");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Remove player from game if they disconnect
        gameManager.removePlayer(event.getPlayer());
    }
}