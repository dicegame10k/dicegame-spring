package com.cb.dicegame.db;

import org.springframework.data.repository.Repository;

public interface PlayerPreferenceRepository<T, ID> extends Repository<PlayerPreference, Long> {

	PlayerPreference save(PlayerPreference playerPreference);

	PlayerPreference findByPlayer(Player player);

}
