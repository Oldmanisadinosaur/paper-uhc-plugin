package com.uhc.plugin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import com.uhc.plugin.managers.GameManager;

public class WorldListener implements Listener {

    private final GameManager gameManager;

    public WorldListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // You can add custom block break logic here
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // You can add custom block place logic here
    }
}