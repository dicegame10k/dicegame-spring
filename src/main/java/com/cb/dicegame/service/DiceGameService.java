package com.cb.dicegame.service;

import com.cb.dicegame.IDiceGameConstants;
import com.cb.dicegame.model.DiceGame;
import com.cb.dicegame.model.Player;
import com.cb.dicegame.util.Log;
import com.cb.dicegame.util.SocketUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class DiceGameService implements IDiceGameConstants {

	private SocketUtil socketUtil;
	private List<Player> lobby;

	private boolean gameInProgress;
	private DiceGame dg;

	@Autowired
	public DiceGameService(SocketUtil socketUtil, List<Player> lobby) {
		this.socketUtil = socketUtil;
		this.lobby = lobby;
	}

	public synchronized void enterLobby(Player p) {
		// player already in lobby or game
		if (lobby.contains(p) || (dg != null && dg.isPlayerInGame(p))) {
			socketUtil.sendLobby(p, lobby);
			socketUtil.sendGameState(p, getGameState());
		}  else {
			lobby.add(p);
			String chatMsg = String.format("%s entered the lobby", p.getName());
			Log.info(chatMsg);
			socketUtil.broadcastSystemChat(chatMsg);
			socketUtil.broadcastLobby(lobby);
		}
	}

	public synchronized void lightUp(Player p) {
		if (gameInProgress) {
			socketUtil.sendSystemChat(p, "Game already in progress");
			return;
		} else if (lobby.size() == 0) {
			socketUtil.sendSystemChat(p, "Lobby is empty");
			return;
		}

		gameInProgress = true;
		dg = new DiceGame(p, lobby);
		lobby = new ArrayList<>();
		socketUtil.broadcastGameState(getGameState());
		socketUtil.broadcastLobby(lobby);
	}

	public synchronized void roll(Player p, boolean force) {
		if (dg == null) {
			socketUtil.sendSystemChat(p, "Cannot roll. Game is not in progress");
			return;
		} else if (!dg.roll(p, force)) {
			socketUtil.sendSystemChat(p, "It is not your turn to roll");
			return;
		}

		socketUtil.broadcastGameState(getGameState());
	}

	private HashMap<String, Object> getGameState() {
		HashMap<String, Object> gameState = new HashMap<>();
		gameState.put(GAME_IN_PROGRESS, gameInProgress);

		List<Player> dgPlayers = (dg != null) ? dg.getPlayers() : new ArrayList<>();
		List<Player> graveyard = (dg != null) ? dg.getGraveyard() : new ArrayList<>();
		Player currentlyRollingPlayer = (dg != null) ? dg.getCurrentlyRollingPlayer() : null;
		int currentRoll = (dg != null) ? dg.getCurrentRoll() : 100;

		gameState.put(DG_PLAYERS, dgPlayers);
		gameState.put(GRAVEYARD, graveyard);
		gameState.put(CURRENTLY_ROLLING_PLAYER, currentlyRollingPlayer);
		gameState.put(CURRENT_ROLL, currentRoll);
		return gameState;
	}

}
