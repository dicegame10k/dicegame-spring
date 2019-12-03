package com.cb.dicegame.service;

import com.cb.dicegame.model.DiceGame;
import com.cb.dicegame.model.LobbyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class DiceGameService {

	private final SimpMessagingTemplate socket;

	private LobbyRepository lobbyRepository;
	private DiceGame dg;

	@Autowired
	public DiceGameService(LobbyRepository lobbyRepository, SimpMessagingTemplate socket) {
		this.lobbyRepository = lobbyRepository;
		this.socket = socket;
	}

	public DiceGame getDiceGame() {
		return dg;
	}

	public void lightUp() {
		//dg.lightUp();
		this.socket.convertAndSend("/topic/gameState", "The service gameState");
	}

}
