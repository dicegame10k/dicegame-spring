package com.cb.dicegame.model;

import com.cb.dicegame.db.Player;

public class Recount {

	private Player player;
	private String numGamesPlayed;
	private String numGamesWon;
	private String winPercentage;
	private String avgDkpPerGame;
	private String avgNumPlayersPerGame;

	public Recount() {
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public String getNumGamesPlayed() {
		return numGamesPlayed;
	}

	public void setNumGamesPlayed(String numGamesPlayed) {
		this.numGamesPlayed = numGamesPlayed;
	}

	public String getNumGamesWon() {
		return numGamesWon;
	}

	public void setNumGamesWon(String numGamesWon) {
		this.numGamesWon = numGamesWon;
	}

	public String getWinPercentage() {
		return winPercentage;
	}

	public void setWinPercentage(String winPercentage) {
		this.winPercentage = winPercentage;
	}

	public String getAvgDkpPerGame() {
		return avgDkpPerGame;
	}

	public void setAvgDkpPerGame(String avgDkpPerGame) {
		this.avgDkpPerGame = avgDkpPerGame;
	}

	public String getAvgNumPlayersPerGame() {
		return avgNumPlayersPerGame;
	}

	public void setAvgNumPlayersPerGame(String avgPlayersPerGame) {
		this.avgNumPlayersPerGame = avgPlayersPerGame;
	}

}
