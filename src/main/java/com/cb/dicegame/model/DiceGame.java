package com.cb.dicegame.model;

import java.util.ArrayList;
import java.util.List;

public class DiceGame {

	private List<Player> players;
	private List<Player> graveyard;
	private Player currentlyRollingPlayer;
	private int currentRoll;

	public DiceGame(Player startingPlayer, List<Player> lobby) {
		this.players = lobby;
		this.graveyard = new ArrayList<Player>();
		this.currentlyRollingPlayer = startingPlayer;
		this.currentRoll = 100;
	}

	public void roll(Player p, boolean force) {

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

}
