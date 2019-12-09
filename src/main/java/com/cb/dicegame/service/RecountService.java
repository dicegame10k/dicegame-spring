package com.cb.dicegame.service;

import com.cb.dicegame.db.DiceGameRecord;
import com.cb.dicegame.db.DiceGameRecordRepository;
import com.cb.dicegame.db.Player;
import com.cb.dicegame.db.PlayerRepository;
import com.cb.dicegame.model.Recount;
import com.cb.dicegame.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecountService {

	private PlayerRepository playerRepository;
	private DiceGameRecordRepository diceGameRecordRepository;

	@Autowired
	public RecountService(PlayerRepository playerRepository, DiceGameRecordRepository diceGameRecordRepository) {
		this.playerRepository = playerRepository;
		this.diceGameRecordRepository = diceGameRecordRepository;
	}

	public List<Recount> recount() {
		Sort.Order one = Sort.Order.desc("dkp");
		Sort.Order two = Sort.Order.asc("name");
		List<Player> players = playerRepository.findAll(Sort.by(one, two));

		List<Recount> recountList = new ArrayList<>();
		for (Player p : players)
			recountList.add(calculateRecount(p));

		return recountList;
	}

	private Recount calculateRecount(Player p) {
		List<DiceGameRecord> gamesPlayed = diceGameRecordRepository.findByPlayers(p);
		int numGamesPlayed = gamesPlayed.size();
		int numWins = diceGameRecordRepository.findByWinningPlayer(p).size();
		double winPerc = ((double) numWins / (double) numGamesPlayed) * 100;
		String winPercentage = normalize(String.format("%.0f", winPerc));
		String avgDkpPerGame = normalize(String.format("%.2f", (double) p.getDkp() / numGamesPlayed));

		int totalNumPlayers = 0;
		for (DiceGameRecord gamePlayed : gamesPlayed)
			totalNumPlayers += gamePlayed.getNumPlayers();

		String avgNumPlayersPerGame = normalize(String.format("%.2f", (double) totalNumPlayers / numGamesPlayed));

		Recount r = new Recount();
		r.setPlayer(p);
		r.setNumGamesPlayed(String.valueOf(numGamesPlayed));
		r.setNumGamesWon(String.valueOf(numWins));
		r.setWinPercentage(winPercentage);
		r.setAvgDkpPerGame(avgDkpPerGame);
		r.setAvgNumPlayersPerGame(avgNumPlayersPerGame);

		return r;
	}

	private String normalize(String s) {
		if (StringUtil.areEqual(s, "NaN"))
			return "0";

		return s;
	}

}
