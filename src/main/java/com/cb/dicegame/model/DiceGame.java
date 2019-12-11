package com.cb.dicegame.model;

import com.cb.dicegame.db.DiceGameRecordRepository;
import com.cb.dicegame.db.Player;
import com.cb.dicegame.db.PlayerRepository;
import com.cb.dicegame.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
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
		else if (!isGameOver)
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

	public HashMap<Player, Integer> saveStats(PlayerRepository playerRepository,
											  DiceGameRecordRepository diceGameRecordRepository) {
		return stats.save(playerRepository, diceGameRecordRepository);
	}

	public void removePlayer(Player p) {
		graveyard.remove(p);
		int playerPos = players.indexOf(p);
		players.remove(p);
		if (players.size() < 2) {
			isGameOver = true;
			currentlyRollingPlayer = null;
		} else if (p.equals(currentlyRollingPlayer)) {
			int nextIndex = playerPos % players.size();
			currentlyRollingPlayer = players.get(nextIndex);
		}
	}

	private Player getNextRollingPlayer() {
		int currPos = players.indexOf(currentlyRollingPlayer);
		return getNextRollingPlayer(currPos);
	}

	private Player getNextRollingPlayer(int currPos) {
		if (players.size() == 0)
			return null;

		int nextIndex = (currPos + 1) % players.size();
		return players.get(nextIndex);
	}

	private void handleRollOne() {
		int playerPos = players.indexOf(currentlyRollingPlayer);
		players.remove(currentlyRollingPlayer);
		graveyard.add(currentlyRollingPlayer);
		stats.recordDkpWon(currentlyRollingPlayer, dkpWon);
		dkpWon += 1;
		if (checkGameOver()) {
			isGameOver = true;
			winningPlayer = getNextRollingPlayer(playerPos);
			// null winningPlayer means one person was playing alone
			if (winningPlayer != null) {
				stats.recordDkpWon(winningPlayer, dkpWon);
				stats.setWinningPlayer(winningPlayer);
			}

			currentlyRollingPlayer = null;
		} else {
			currentRoll = 100;
			currentlyRollingPlayer = getNextRollingPlayer(playerPos - 1);
		}
	}

	private boolean checkGameOver() {
		return currentRoll == 1 && players.size() < 2;
	}

}
