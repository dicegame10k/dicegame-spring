package com.cb.dicegame.model;

import com.cb.dicegame.db.DiceGameRecord;
import com.cb.dicegame.db.DiceGameRecordRepository;
import com.cb.dicegame.db.Player;
import com.cb.dicegame.db.PlayerRepository;

import java.util.*;

public class DiceGameStats {

	private DiceGame dg;
	private int numPlayers;
	private Player winningPlayer;
	private int numRolls;
	private HashMap<Player, Integer> dkpWonMap;

	public DiceGameStats(DiceGame dg) {
		this.dg = dg;
		dkpWonMap = new LinkedHashMap<>();
	}

	public void incrementRoll() {
		numRolls += 1;
	}

	public void recordDkpWon(Player p, int dkpWon) {
		dkpWonMap.put(p, dkpWon);
	}

	public void setWinningPlayer(Player p) {
		winningPlayer = p;
	}

	public HashMap<Player, Integer> save(PlayerRepository playerRepository,
										 DiceGameRecordRepository diceGameRecordRepository) {
		List<Player> players = new ArrayList<>();
		for (Map.Entry<Player, Integer> entry : dkpWonMap.entrySet()) {
			// update the player's DKP
			Player p = entry.getKey();
			int dkpWon = entry.getValue();
			p.setDkp(p.getDkp() + dkpWon);
			playerRepository.save(p);

			players.add(p);
		}

		// write down the record of the game information
		int numPlayers = players.size();
		DiceGameRecord dgr = new DiceGameRecord(players, winningPlayer, numPlayers, numRolls, new Date());
		diceGameRecordRepository.save(dgr);
		return dkpWonMap;
	}

}
