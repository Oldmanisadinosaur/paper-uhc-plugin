package com.uhc.plugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.uhc.plugin.managers.GameManager;

public class UHCCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final GameManager gameManager;

    public UHCCommand(JavaPlugin plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "start":
                return handleStart(sender);
            case "stop":
                return handleStop(sender);
            case "join":
                return handleJoin(sender);
            case "leave":
                return handleLeave(sender);
            case "info":
                return handleInfo(sender);
            default:
                sendHelp(sender);
                return true;
        }
    }

    private boolean handleStart(CommandSender sender) {
        if (!sender.hasPermission("uhc.admin")) {
            sender.sendMessage("§c§l[UHC] §r§cYou don't have permission to use this command!");
            return true;
        }

        if (gameManager.startGame()) {
            sender.sendMessage("§a§l[UHC] §r§aGame started!");
            return true;
        } else {
            sender.sendMessage("§c§l[UHC] §r§cFailed to start game. Is a game already running?");
            return true;
        }
    }

    private boolean handleStop(CommandSender sender) {
        if (!sender.hasPermission("uhc.admin")) {
            sender.sendMessage("§c§l[UHC] §r§cYou don't have permission to use this command!");
            return true;
        }

        if (gameManager.stopGame()) {
            sender.sendMessage("§a§l[UHC] §r§aGame stopped!");
            return true;
        } else {
            sender.sendMessage("§c§l[UHC] §r§cNo game is running!");
            return true;
        }
    }

    private boolean handleJoin(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c§l[UHC] §r§cOnly players can join UHC games!");
            return true;
        }

        if (!sender.hasPermission("uhc.join")) {
            sender.sendMessage("§c§l[UHC] §r§cYou don't have permission to join!");
            return true;
        }

        Player player = (Player) sender;
        gameManager.addPlayer(player);
        return true;
    }

    private boolean handleLeave(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c§l[UHC] §r§cOnly players can leave UHC games!");
            return true;
        }

        if (!sender.hasPermission("uhc.leave")) {
            sender.sendMessage("§c§l[UHC] §r§cYou don't have permission to leave!");
            return true;
        }

        Player player = (Player) sender;
        if (gameManager.removePlayer(player)) {
            player.sendMessage("§a§l[UHC] §r§aYou have left the game!");
        } else {
            player.sendMessage("§c§l[UHC] §r§cYou were not in a game!");
        }
        return true;
    }

    private boolean handleInfo(CommandSender sender) {
        sender.sendMessage("§6§l====== UHC Game Info ======§r");
        sender.sendMessage("§6Running: §r" + (gameManager.isGameRunning() ? "§aYes" : "§cNo"));
        sender.sendMessage("§6Players: §r" + gameManager.getPlayerCount());
        sender.sendMessage("§6Game Time: §r" + (gameManager.getGameTime() / 20) + "s");
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§l====== UHC Commands ======§r");
        sender.sendMessage("§6/uhc join §r- Join the UHC game");
        sender.sendMessage("§6/uhc leave §r- Leave the UHC game");
        sender.sendMessage("§6/uhc info §r- View game information");
        if (sender.hasPermission("uhc.admin")) {
            sender.sendMessage("§6/uhc start §r- Start the game (Admin)");
            sender.sendMessage("§6/uhc stop §r- Stop the game (Admin)");
        }
    }
}