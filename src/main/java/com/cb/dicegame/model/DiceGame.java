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

	public DiceGame(Player startingPlayer, List<Player> lobby) {
		this.players = lobby;
		this.graveyard = new ArrayList<>();
		this.currentlyRollingPlayer = startingPlayer;
		this.currentRoll = 100;
		random = new Random();

		stats = new DiceGameStats(this);
	}

	public boolean roll(Player p, boolean force) {
		if (!p.equals(currentlyRollingPlayer) && !force) {
			Log.info(String.format("'%s' tried to roll out of turn", p.getName()));
			return false;
		}

		currentRoll = random.nextInt(currentRoll) + 1;
		if (currentRoll == 1) {
			if (isGameOver()) {
				// TODO handle it
			} else {
				dkpWon += 1;
				currentlyRollingPlayer = getNextRollingPlayer();
			}
		}

		//TODO: sleep 1 second then broadcast chat message
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

	private Player getNextRollingPlayer() {
		int nextIndex = (players.indexOf(currentlyRollingPlayer) + 1) % players.size();
		return players.get(nextIndex);
	}

	private boolean isGameOver() {
		return players.size() < 3;
	}

}
