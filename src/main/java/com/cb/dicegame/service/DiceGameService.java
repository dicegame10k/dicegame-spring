package com.cb.dicegame.service;

import com.cb.dicegame.model.DiceGame;
import com.cb.dicegame.model.Player;
import com.cb.dicegame.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class DiceGameService {

	private final SimpMessagingTemplate socket;

	private List<Player> lobby;
	private boolean gameInProgress;
	private DiceGame dg;

	@Autowired
	public DiceGameService(SimpMessagingTemplate socket, List<Player> lobby) {
		this.socket = socket;
		this.lobby = lobby;
	}

	// TODO: should be synchronized?
	public void enterLobby(Player p) {
		if (lobby.contains(p))
			Log.info(String.format("Not adding '%s' to lobby, player already in lobby", p.getName()));
		else {
			Log.info(String.format("'%s' entered the lobby", p.getName()));
			lobby.add(p);
		}

		this.socket.convertAndSend("/topic/lobby", lobby);
	}

	// TODO synchronize on some shared obj
	public synchronized void lightUp(Player p) {
		if (gameInProgress) {
			Log.warn(String.format("'%s' tried to light up when game is already in progress", p.getName()));
			return;
		}

		gameInProgress = true;
		dg = new DiceGame(p, lobby);
		lobby = new ArrayList<Player>();
		sendGameState();
	}

	public synchronized void roll(Player p, boolean force) {
		if (dg == null) {
			Log.warn(String.format("'%s' tried to roll when a game was not in progress", p.getName()));
			return;
		}

		dg.roll(p, force);
		sendGameState();
	}

	private void sendGameState() {
		HashMap<String, Object> gameState = new HashMap<String, Object>();
		gameState.put("gameInProgress", gameInProgress);

		List<Player> dgPlayers = (dg != null) ? dg.getPlayers() : new ArrayList<Player>();
		List<Player> graveyard = (dg != null) ? dg.getGraveyard() : new ArrayList<Player>();
		Player currentlyRollingPlayer = (dg != null) ? dg.getCurrentlyRollingPlayer() : null;
		int currentRoll = (dg != null) ? dg.getCurrentRoll() : 100;

		gameState.put("dgPlayers", dgPlayers);
		gameState.put("graveyard", graveyard);
		gameState.put("currentlyRollingPlayer", currentlyRollingPlayer);
		gameState.put("currentRoll", currentRoll);

		this.socket.convertAndSend("/topic/gameState", gameState);
		this.socket.convertAndSend("/topic/lobby", lobby);
	}

}
