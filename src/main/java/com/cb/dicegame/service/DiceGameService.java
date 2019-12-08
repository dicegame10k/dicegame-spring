package com.cb.dicegame.service;

import com.cb.dicegame.IDiceGameConstants;
import com.cb.dicegame.db.DiceGameRecordRepository;
import com.cb.dicegame.db.Player;
import com.cb.dicegame.db.PlayerRepository;
import com.cb.dicegame.model.*;
import com.cb.dicegame.util.DiceGameUtil;
import com.cb.dicegame.util.Log;
import com.cb.dicegame.util.SocketUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiceGameService implements IDiceGameConstants {

	private SocketUtil socketUtil;
	private List<Player> lobby;
	private PlayerRepository playerRepository;
	private DiceGameRecordRepository diceGameRecordRepository;

	private boolean gameInProgress;
	private DiceGame dg;

	@Autowired
	public DiceGameService(SocketUtil socketUtil, List<Player> lobby, PlayerRepository playerRepository,
			DiceGameRecordRepository diceGameRecordRepository) {
		this.socketUtil = socketUtil;
		this.lobby = lobby;
		this.playerRepository = playerRepository;
		this.diceGameRecordRepository = diceGameRecordRepository;
	}

	public synchronized void enterLobby(Player p) {
		// player already in lobby or game
		if (isPlayerInLobbyOrGame(p))
			sendAppState(p);
		else {
			lobby.add(p);
			String chatMsg = String.format("%s entered the lobby", p.getName());
			Log.info(chatMsg);
			socketUtil.broadcastSystemChat(chatMsg);
			socketUtil.broadcastLobby(lobby);
			socketUtil.sendGameState(p, getGameState());
		}
	}

	public synchronized void lightUp(Player p) {
		if (gameInProgress) {
			socketUtil.sendSystemChat(p, "Game already in progress");
			sendAppState(p);
			return;
		} else if (lobby.size() == 0) {
			socketUtil.sendSystemChat(p, "Lobby is empty");
			sendAppState(p);
			return;
		}

		gameInProgress = true;
		dg = new DiceGame(p, lobby);
		lobby = new ArrayList<>();
		String gameStartMsg = String.format("%s started DiceGame", p.getName());
		Log.info(gameStartMsg);
		socketUtil.broadcastSystemChat(gameStartMsg);
		broadcastAppState();
	}

	public synchronized void roll(Player p, boolean force) {
		if (dg == null) {
			Log.info(String.format("%s tried to roll when no game was in progress", p.getName()));
			socketUtil.sendSystemChat(p, "Cannot roll. Game is not in progress");
			sendAppState(p);
			return;
		} else {
			// force roll as the person who is supposed to be rolling (unless game is over)
			if (force && dg.getCurrentlyRollingPlayer() != null)
				p = dg.getCurrentlyRollingPlayer();

			if (!dg.roll(p, force)) {
				socketUtil.sendSystemChat(p, "It is not your turn to roll");
				sendAppState(p);
				return;
			}
		}

		// roll was successful, broadcast the state and then sleep 1.1 seconds so the client can build suspense
		socketUtil.broadcastGameState(getGameState(true));
		DiceGameUtil.sleep(1100);

		int lastRoll = dg.isPrevRollOne() ? 1 : dg.getCurrentRoll();
		String rollMsg = String.format("%s rolled %d", p.getName(), lastRoll);
		if (lastRoll == 1)
			rollMsg += " (RIP)";

		Log.info(rollMsg);
		socketUtil.broadcastSystemChat(rollMsg);

		if (dg.isGameOver()) {
			// TODO persist DG stats
			// no winning player means they were playing alone
			String winMsg = (dg.getWinningPlayer() != null) ? String.format("%s wins!", dg.getWinningPlayer().getName())
				: String.format("%s loses. You were playing alone, what did you expect?", p.getName());
			Log.info(winMsg);
			socketUtil.broadcastSystemChat(winMsg);

			HashMap<Player, Integer> dkpWonMap = dg.saveStats(playerRepository, diceGameRecordRepository);
			for (Map.Entry<Player, Integer> entry : dkpWonMap.entrySet())
				socketUtil.broadcastSystemChat(String.format("%s won %d DKP", entry.getKey().getName(), entry.getValue()));

			// sleep for a few seconds to let the winner bask in their glory
			DiceGameUtil.sleep(5000);
			resetGame();
		}
	}

	public synchronized void resetGame() {
		if (dg != null) {
			lobby.addAll(dg.getGraveyard());
			lobby.addAll(dg.getPlayers());
			dg = null;
		}

		gameInProgress = false;
		broadcastAppState();
	}

	// called when player logs out or gets kicked
	public synchronized void removePlayer(Player p) {
		lobby.remove(p);

		if (dg != null) {
			dg.removePlayer(p);
			if (dg.isGameOver()) {
				String resetMsg = String.format("%s left and the game is over. Nobody wins", p.getName());
				Log.info(resetMsg);
				socketUtil.broadcastSystemChat(resetMsg);
				resetGame();
			}
		}

		// tell the user to logout
		socketUtil.sendLogout(p);
		broadcastAppState();
	}

	public synchronized void updatePlayerInfo(Player p, WowClass newWowClass) {
		int lobbyPos = lobby.indexOf(p);
		if (lobbyPos > -1)
			lobby.get(lobbyPos).setWowClass(newWowClass);

		if (dg != null) {
			int gamePos = dg.getPlayers().indexOf(p);
			if (gamePos > -1)
				dg.getPlayers().get(gamePos).setWowClass(newWowClass);

			int graveyardPos = dg.getGraveyard().indexOf(p);
			if (graveyardPos > -1)
				dg.getGraveyard().get(graveyardPos).setWowClass(newWowClass);
		}

		Log.info(String.format("%s changed class to %s", p.getName(), newWowClass));
		broadcastAppState();
	}

	public boolean isPlayerInLobbyOrGame(Player p) {
		return lobby.contains(p) || (dg != null && dg.isPlayerInGame(p));
	}

	private HashMap<String, Object> getGameState() {
		return getGameState(false);
	}

	private HashMap<String, Object> getGameState(boolean isARoll) {
		HashMap<String, Object> gameState = new HashMap<>();
		gameState.put(GAME_IN_PROGRESS, gameInProgress);

		List<Player> dgPlayers = (dg != null) ? dg.getPlayers() : new ArrayList<>();
		List<Player> graveyard = (dg != null) ? dg.getGraveyard() : new ArrayList<>();
		Player currentlyRollingPlayer = (dg != null) ? dg.getCurrentlyRollingPlayer() : null;
		int currentRoll = (dg != null) ? dg.getCurrentRoll() : 100;

		gameState.put(DG_PLAYERS, dgPlayers);
		gameState.put(GRAVEYARD, graveyard);
		gameState.put(CURRENTLY_ROLLING_PLAYER, currentlyRollingPlayer);
		gameState.put(IS_A_ROLL, isARoll);
		gameState.put(CURRENT_ROLL, currentRoll);
		return gameState;
	}

	public void broadcastAppState() {
		socketUtil.broadcastLobby(lobby);
		socketUtil.broadcastGameState(getGameState());
	}

	/**
	 * Sends an individual player the state of the entire app
	 */
	private void sendAppState(Player p) {
		socketUtil.sendLobby(p, lobby);
		socketUtil.sendGameState(p, getGameState());
	}

}
