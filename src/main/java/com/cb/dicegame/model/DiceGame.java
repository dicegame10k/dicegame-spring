package com.cb.dicegame.model;

import com.cb.dicegame.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DiceGame {

	// part of the game state
	private List<Player> players;
	private List<Player> graveyard;
	private Player currentlyRollingPlayer;
	private int currentRoll;

	// internal
	private DiceGameStats stats;
	private Random random;
	private int dkpWon;
	private boolean isGameOver;
	private boolean isPrevRollOne;
	private Player winningPlayer;

	public DiceGame(Player startingPlayer, List<Player> lobby) {
		// place the starting player at the front of the list
		lobby.remove(startingPlayer);
		lobby.add(0, startingPlayer);
		this.players = lobby;
		this.graveyard = new ArrayList<>();
		this.currentlyRollingPlayer = startingPlayer;
		this.currentRoll = 100;
		random = new Random();

		stats = new DiceGameStats(this);
	}

	public boolean roll(Player p, boolean force) {
		if (isGameOver) {
			Log.info(String.format("%s tried to roll but the game was already over", p.getName()));
			return false;
		}

		if (!p.equals(currentlyRollingPlayer) && !force) {
			Log.info(String.format("'%s' tried to roll out of turn", p.getName()));
			return false;
		}

		stats.incrementRoll();
		currentRoll = random.nextInt(currentRoll) + 1;
		isPrevRollOne = currentRoll == 1;
		if (currentRoll == 1)
			handleRollOne();

		if (!isGameOver)
			currentlyRollingPlayer = getNextRollingPlayer();

		return true;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public List<Player> getGraveyard() {
		return graveyard;
	}

	public Player getCurrentlyRollingPlayer() {
		return currentlyRollingPlayer;
	}

	public int getCurrentRoll() {
		return currentRoll;
	}

	public boolean isPrevRollOne() {
		return isPrevRollOne;
	}

	public boolean isPlayerInGame(Player p) {
		return this.players.contains(p) || this.graveyard.contains(p);
	}

	public boolean isGameOver() {
		return isGameOver;
	}

	public Player getWinningPlayer() {
		return winningPlayer;
	}

	public void removePlayer(Player p) {
		graveyard.remove(p);
		players.remove(p);
		if (players.size() < 2) {
			isGameOver = true;
			currentlyRollingPlayer = null;
		} else if (p.equals(currentlyRollingPlayer))
			currentlyRollingPlayer = getNextRollingPlayer();
	}

	private Player getNextRollingPlayer() {
		if (players.size() == 0)
			return null;

		int nextIndex = (players.indexOf(currentlyRollingPlayer) + 1) % players.size();
		return players.get(nextIndex);
	}

	private void handleRollOne() {
		players.remove(currentlyRollingPlayer);
		graveyard.add(currentlyRollingPlayer);
		stats.recordDkpWon(currentlyRollingPlayer, dkpWon);
		dkpWon += 1;
		if (checkGameOver()) {
			isGameOver = true;
			winningPlayer = getNextRollingPlayer();
			// null winningPlayer means one person was playing alone
			if (winningPlayer != null) {
				stats.recordDkpWon(winningPlayer, dkpWon);
				stats.setWinningPlayer(winningPlayer);
			}

			currentlyRollingPlayer = null;
		} else
			currentRoll = 100;
	}

	private boolean checkGameOver() {
		return currentRoll == 1 && players.size() < 2;
	}

}
