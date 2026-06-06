package com.uhc.plugin.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class TeamManager {

    private final JavaPlugin plugin;
    private final Map<String, UHCTeam> teams = new HashMap<>();
    private final Map<Player, UHCTeam> playerTeams = new HashMap<>();
    private final List<ChatColor> teamColors = Arrays.asList(
            ChatColor.RED, ChatColor.BLUE, ChatColor.GREEN, ChatColor.YELLOW,
            ChatColor.LIGHT_PURPLE, ChatColor.DARK_AQUA, ChatColor.DARK_GREEN,
            ChatColor.DARK_RED, ChatColor.DARK_BLUE, ChatColor.DARK_PURPLE
    );
    private int teamColorIndex = 0;

    public TeamManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public UHCTeam createTeam(String teamName) {
        if (teams.containsKey(teamName)) {
            return null;
        }

        ChatColor color = teamColors.get(teamColorIndex % teamColors.size());
        teamColorIndex++;

        UHCTeam team = new UHCTeam(teamName, color);
        teams.put(teamName, team);
        return team;
    }

    public boolean addPlayerToTeam(Player player, String teamName) {
        if (!teams.containsKey(teamName)) {
            return false;
        }

        // Remove from old team if exists
        if (playerTeams.containsKey(player)) {
            playerTeams.get(player).removePlayer(player);
        }

        UHCTeam team = teams.get(teamName);
        if (team.addPlayer(player)) {
            playerTeams.put(player, team);
            return true;
        }

        return false;
    }

    public boolean removePlayerFromTeam(Player player) {
        if (playerTeams.containsKey(player)) {
            UHCTeam team = playerTeams.get(player);
            team.removePlayer(player);
            playerTeams.remove(player);

            if (team.getPlayers().isEmpty()) {
                teams.remove(team.getName());
            }

            return true;
        }
        return false;
    }

    public UHCTeam getTeam(String teamName) {
        return teams.get(teamName);
    }

    public UHCTeam getPlayerTeam(Player player) {
        return playerTeams.get(player);
    }

    public boolean isInTeam(Player player) {
        return playerTeams.containsKey(player);
    }

    public void updateTabList() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        // Clear old teams
        for (Team team : scoreboard.getTeams()) {
            team.unregister();
        }

        // Create new teams for scoreboard display
        for (UHCTeam uhcTeam : teams.values()) {
            Team scoreboardTeam = scoreboard.registerNewTeam(uhcTeam.getName());
            scoreboardTeam.setPrefix(uhcTeam.getColor() + "[" + uhcTeam.getName() + "] " + ChatColor.RESET);
            scoreboardTeam.setColor(uhcTeam.getColor());

            for (Player player : uhcTeam.getPlayers()) {
                scoreboardTeam.addPlayer(player);
            }
        }
    }

    public void disbandAllTeams() {
        teams.clear();
        playerTeams.clear();
        teamColorIndex = 0;

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        for (Team team : scoreboard.getTeams()) {
            team.unregister();
        }
    }

    public Collection<UHCTeam> getAllTeams() {
        return teams.values();
    }

    public int getTeamCount() {
        return teams.size();
    }

    public static class UHCTeam {
        private final String name;
        private final ChatColor color;
        private final Set<Player> players = new HashSet<>();

        public UHCTeam(String name, ChatColor color) {
            this.name = name;
            this.color = color;
        }

        public boolean addPlayer(Player player) {
            return players.add(player);
        }

        public boolean removePlayer(Player player) {
            return players.remove(player);
        }

        public Set<Player> getPlayers() {
            return new HashSet<>(players);
        }

        public String getName() {
            return name;
        }

        public ChatColor getColor() {
            return color;
        }

        public int getPlayerCount() {
            return players.size();
        }
    }
}
