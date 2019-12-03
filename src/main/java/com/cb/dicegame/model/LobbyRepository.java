package com.cb.dicegame.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LobbyRepository<T, ID> extends JpaRepository<Player, Long> {

}
