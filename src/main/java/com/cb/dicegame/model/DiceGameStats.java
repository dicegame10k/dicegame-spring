package com.cb.dicegame.model;

import com.cb.dicegame.db.DiceGameRecord;
import com.cb.dicegame.db.DiceGameRecordRepository;
import com.cb.dicegame.db.Player;
import com.cb.dicegame.db.PlayerRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiceGameStats {

	private DiceGame dg;
	private int numPlayers;
	private Player winningPlayer;
	private int numRolls;
	private HashMap<Player, Integer> dkpWonMap;

	public DiceGameStats(DiceGame dg) {
		this.dg = dg;
		dkpWonMap = new HashMap<>();
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
			Player p = entry.getKey();
			int dkpWon = entry.getValue();
			p.setDkp(p.getDkp() + dkpWon);
			playerRepository.save(p);

			players.add(p);
		}

		int numPlayers = players.size();
		DiceGameRecord dgr = new DiceGameRecord(players, winningPlayer, numPlayers, numRolls);
		diceGameRecordRepository.save(dgr);
		return dkpWonMap;
	}

}
