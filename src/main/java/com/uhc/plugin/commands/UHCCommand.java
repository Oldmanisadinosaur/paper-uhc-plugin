package com.uhc.plugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.uhc.plugin.managers.GameManager;
import com.uhc.plugin.managers.TeamManager;
import com.uhc.plugin.stats.StatsManager;
import com.uhc.plugin.stats.ScenarioManager;
import com.uhc.plugin.stats.PlayerStats;
import com.uhc.plugin.stats.Scenario;

import java.util.List;

public class UHCCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final GameManager gameManager;
    private final TeamManager teamManager;
    private final StatsManager statsManager;
    private final ScenarioManager scenarioManager;

    public UHCCommand(JavaPlugin plugin, GameManager gameManager, TeamManager teamManager, 
                      StatsManager statsManager, ScenarioManager scenarioManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.teamManager = teamManager;
        this.statsManager = statsManager;
        this.scenarioManager = scenarioManager;
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
            case "stats":
                return handleStats(sender, args);
            case "team":
                return handleTeam(sender, args);
            case "scenario":
                return handleScenario(sender, args);
            case "top":
                return handleTop(sender);
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
        sender.sendMessage("§6Teams: §r" + teamManager.getTeamCount());
        sender.sendMessage("§6Active Scenarios: §r" + scenarioManager.getEnabledScenariosString());
        return true;
    }

    private boolean handleStats(CommandSender sender, String[] args) {
        if (args.length < 2) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[UHC] §r§cPlease specify a player name!");
                return true;
            }
            Player player = (Player) sender;
            PlayerStats stats = statsManager.getStats(player.getName());
            displayStats(sender, stats);
        } else {
            PlayerStats stats = statsManager.getStats(args[1]);
            displayStats(sender, stats);
        }
        return true;
    }

    private void displayStats(CommandSender sender, PlayerStats stats) {
        sender.sendMessage("§6§l====== Stats for " + stats.getPlayerName() + " ======§r");
        sender.sendMessage("§6ELO: §r" + stats.getElo());
        sender.sendMessage("§6Kills: §r" + stats.getKills());
        sender.sendMessage("§6Deaths: §r" + stats.getDeaths());
        sender.sendMessage("§6K/D Ratio: §r" + stats.getKDR());
        sender.sendMessage("§6Wins: §r" + stats.getWins());
        sender.sendMessage("§6Losses: §r" + stats.getLosses());
        sender.sendMessage("§6Win Rate: §r" + stats.getWinRate() + "%");
        sender.sendMessage("§6Total Games: §r" + stats.getTotalGames());
    }

    private boolean handleTeam(CommandSender sender, String[] args) {
        if (!sender.hasPermission("uhc.admin")) {
            sender.sendMessage("§c§l[UHC] §r§cYou don't have permission!");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§c§l[UHC] §r§cUsage: /uhc team <create|add|list> [args]");
            return true;
        }

        String action = args[1].toLowerCase();
        switch (action) {
            case "create":
                if (args.length < 3) {
                    sender.sendMessage("§c§l[UHC] §r§cUsage: /uhc team create <teamName>");
                    return true;
                }
                teamManager.createTeam(args[2]);
                sender.sendMessage("§a§l[UHC] §r§aTeam " + args[2] + " created!");
                break;
            case "add":
                if (args.length < 4) {
                    sender.sendMessage("§c§l[UHC] §r§cUsage: /uhc team add <playerName> <teamName>");
                    return true;
                }
                Player player = plugin.getServer().getPlayer(args[2]);
                if (player == null) {
                    sender.sendMessage("§c§l[UHC] §r§cPlayer not found!");
                    return true;
                }
                if (teamManager.addPlayerToTeam(player, args[3])) {
                    sender.sendMessage("§a§l[UHC] §r§aAdded " + player.getName() + " to team " + args[3]);
                    teamManager.updateTabList();
                } else {
                    sender.sendMessage("§c§l[UHC] §r§cFailed to add player to team!");
                }
                break;
            case "list":
                sender.sendMessage("§6§l====== Teams ======§r");
                for (TeamManager.UHCTeam team : teamManager.getAllTeams()) {
                    sender.sendMessage(team.getColor() + "[" + team.getName() + "] §r- " + team.getPlayerCount() + " players");
                }
                break;
            default:
                sender.sendMessage("§c§l[UHC] §r§cUnknown team action!");
        }
        return true;
    }

    private boolean handleScenario(CommandSender sender, String[] args) {
        if (!sender.hasPermission("uhc.admin")) {
            sender.sendMessage("§c§l[UHC] §r§cYou don't have permission!");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§c§l[UHC] §r§cUsage: /uhc scenario <enable|disable|list> [scenarioName]");
            return true;
        }

        String action = args[1].toLowerCase();
        switch (action) {
            case "enable":
                if (args.length < 3) {
                    sender.sendMessage("§c§l[UHC] §r§cUsage: /uhc scenario enable <scenarioName>");
                    return true;
                }
                scenarioManager.enableScenario(args[2]);
                sender.sendMessage("§a§l[UHC] §r§aScenario " + args[2] + " enabled!");
                break;
            case "disable":
                if (args.length < 3) {
                    sender.sendMessage("§c§l[UHC] §r§cUsage: /uhc scenario disable <scenarioName>");
                    return true;
                }
                scenarioManager.disableScenario(args[2]);
                sender.sendMessage("§a§l[UHC] §r§aScenario " + args[2] + " disabled!");
                break;
            case "list":
                sender.sendMessage("§6§l====== Available Scenarios ======§r");
                List<Scenario> scenarios = scenarioManager.getAllScenarios();
                for (Scenario scenario : scenarios) {
                    String status = scenario.isEnabled() ? "§a✓" : "§c✗";
                    sender.sendMessage(status + " §r" + scenario.getName() + " - " + scenario.getDescription());
                }
                break;
            default:
                sender.sendMessage("§c§l[UHC] §r§cUnknown scenario action!");
        }
        return true;
    }

    private boolean handleTop(CommandSender sender) {
        sender.sendMessage("§6§l====== Top 10 Players ======§r");
        List<PlayerStats> topPlayers = statsManager.getTopPlayers(10);
        for (int i = 0; i < topPlayers.size(); i++) {
            PlayerStats stats = topPlayers.get(i);
            sender.sendMessage("§6#" + (i + 1) + " §r" + stats.getPlayerName() + " - ELO: " + stats.getElo());
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§l====== UHC Commands ======§r");
        sender.sendMessage("§6/uhc join §r- Join the UHC game");
        sender.sendMessage("§6/uhc leave §r- Leave the UHC game");
        sender.sendMessage("§6/uhc info §r- View game information");
        sender.sendMessage("§6/uhc stats [player] §r- View player stats");
        sender.sendMessage("§6/uhc top §r- View top 10 players");
        if (sender.hasPermission("uhc.admin")) {
            sender.sendMessage("§6/uhc start §r- Start the game (Admin)");
            sender.sendMessage("§6/uhc stop §r- Stop the game (Admin)");
            sender.sendMessage("§6/uhc team <create|add|list> §r- Manage teams (Admin)");
            sender.sendMessage("§6/uhc scenario <enable|disable|list> §r- Manage scenarios (Admin)");
        }
    }
}