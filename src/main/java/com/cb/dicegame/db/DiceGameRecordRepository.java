package com.cb.dicegame.db;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

// do not expose this repository to REST operations (ex. /api/dicegamerecords endpoint)
@RepositoryRestResource(exported = false)
public interface DiceGameRecordRepository<T, ID> extends Repository<DiceGameRecord, Long> {

	DiceGameRecord save(DiceGameRecord diceGameRecord);

	List<DiceGameRecord> findAll(Sort var1);

	// all games won by Player p
	List<DiceGameRecord> findByWinningPlayer(Player p);

	// all games won by Player p (sorted)
	List<DiceGameRecord> findByWinningPlayer(Player p, Sort var1);

	// all games played by Player p
	List<DiceGameRecord> findByPlayers(Player p);

	// all games played by Player p (sorted)
	List<DiceGameRecord> findByPlayers(Player p, Sort var1);

}
