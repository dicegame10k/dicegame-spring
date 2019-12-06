package com.cb.dicegame.model;

public class DiceGameStats {

	private DiceGame dg;
	private int numPlayers;
	private Player winningPlayer;
	private int numRolls;

	public DiceGameStats(DiceGame dg) {
		this.dg = dg;
		this.numPlayers = dg.getPlayers().size();
	}

	public void incrementRoll() {
		numRolls += 1;
	}

}
