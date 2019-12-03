package com.cb.dicegame.model;

import java.util.List;

public class DiceGame {

	private List<Player> players;

	public DiceGame() {
	}

	public DiceGame(List<Player> players) {
		this.players = players;
	}

	public void lightUp() {
		// TODO: coming soon..
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}
}
