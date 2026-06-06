package com.uhc.plugin.managers;

import java.util.*;

public class PlayerStats {

    private static final int STARTING_ELO = 1000;
    private static final int K_FACTOR = 32; // ELO K-factor

    private String playerName;
    private int elo;
    private int kills;
    private int deaths;
    private int wins;
    private int losses;
    private int totalGames;

    public PlayerStats(String playerName) {
        this.playerName = playerName;
        this.elo = STARTING_ELO;
        this.kills = 0;
        this.deaths = 0;
        this.wins = 0;
        this.losses = 0;
        this.totalGames = 0;
    }

    public void addKill() {
        this.kills++;
    }

    public void addDeath() {
        this.deaths++;
    }

    public void addWin() {
        this.wins++;
        this.totalGames++;
    }

    public void addLoss() {
        this.losses++;
        this.totalGames++;
    }

    public void updateElo(int opponentElo, boolean won) {
        double expectedScore = 1 / (1 + Math.pow(10, (opponentElo - this.elo) / 400.0));
        int eloChange = (int) Math.round(K_FACTOR * (((won ? 1 : 0) - expectedScore)));
        this.elo = Math.max(0, this.elo + eloChange);
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = Math.max(0, elo);
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getTotalGames() {
        return totalGames;
    }

    public double getKDR() {
        if (deaths == 0) {
            return kills;
        }
        return Math.round((double) kills / deaths * 100.0) / 100.0;
    }

    public double getWinRate() {
        if (totalGames == 0) {
            return 0.0;
        }
        return Math.round((double) wins / totalGames * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return "PlayerStats{" +
                "playerName='" + playerName + '\'' +
                ", elo=" + elo +
                ", kills=" + kills +
                ", deaths=" + deaths +
                ", wins=" + wins +
                ", losses=" + losses +
                ", totalGames=" + totalGames +
                '}';
    }
}
