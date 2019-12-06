package com.cb.dicegame.model;

import java.util.HashMap;

public class DiceGameStats {

	private DiceGame dg;
	private int numPlayers;
	private Player winningPlayer;
	private int numRolls;
	private HashMap<Player, Integer> dkpWonMap;

	public DiceGameStats(DiceGame dg) {
		this.dg = dg;
		this.numPlayers = dg.getPlayers().size();
		dkpWonMap = new HashMap<>();
	}

	public void incrementRoll() {
		numRolls += 1;
	}

	public void recordDkpWon(Player p, int dkpWon) {
		dkpWonMap.put(p, dkpWon);
	}

}
