package com.cb.dicegame.db;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

// do not expose this repository to REST operations (ex. /api/dicegamerecords endpoint)
@RepositoryRestResource(exported = false)
public interface DiceGameRecordRepository<T, ID> extends Repository<DiceGameRecord, Long> {

	DiceGameRecord save(DiceGameRecord diceGameRecord);

	List<DiceGameRecord> findAll();

}
