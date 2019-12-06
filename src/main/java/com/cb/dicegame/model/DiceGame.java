package com.cb.dicegame.model;

import com.cb.dicegame.util.Log;
import com.cb.dicegame.util.SocketUtil;

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
	private SocketUtil socketUtil;
	private DiceGameStats stats;
	private Random random;
	private int dkpWon;
	private boolean isGameOver;

	public DiceGame(Player startingPlayer, List<Player> lobby, SocketUtil socketUtil) {
		this.players = lobby;
		this.graveyard = new ArrayList<>();
		this.currentlyRollingPlayer = startingPlayer;
		this.currentRoll = 100;
		random = new Random();

		this.socketUtil = socketUtil;
		stats = new DiceGameStats(this);
	}

	public boolean roll(Player p, boolean force) {
		if (!p.equals(currentlyRollingPlayer) && !force) {
			Log.info(String.format("'%s' tried to roll out of turn", p.getName()));
			return false;
		}

		stats.incrementRoll();
		currentRoll = random.nextInt(currentRoll) + 1;
		if (currentRoll == 1)
			handleRollOne();
		else
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

	public boolean isPlayerInGame(Player p) {
		return this.players.contains(p) || this.graveyard.contains(p);
	}

	public boolean isGameOver() {
		return isGameOver;
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
			Player winningPlayer = getNextRollingPlayer();
			if (winningPlayer != null)
				stats.recordDkpWon(winningPlayer, dkpWon);

			currentlyRollingPlayer = null;
		} else
			currentRoll = 100;
	}

	private boolean checkGameOver() {
		return currentRoll == 1 && players.size() < 3;
	}
}
