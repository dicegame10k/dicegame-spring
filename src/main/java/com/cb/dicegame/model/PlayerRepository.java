package com.cb.dicegame.model;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

// do not expose this repository to REST operations (ex. /api/players endpoint)
@RepositoryRestResource(exported = false)
public interface PlayerRepository<T, ID> extends Repository<Player, Long> {

	Player save(Player player);

	Player findByName(String name);

	List<Player> findAll(Sort var1);

}
