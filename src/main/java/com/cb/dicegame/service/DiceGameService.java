package com.cb.dicegame.service;

import com.cb.dicegame.IDiceGameConstants;
import com.cb.dicegame.model.DiceGame;
import com.cb.dicegame.model.Player;
import com.cb.dicegame.util.DiceGameUtil;
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
		String gameStartMsg = String.format("DiceGame started by %s", p.getName());
		Log.info(gameStartMsg);
		socketUtil.broadcastSystemChat(gameStartMsg);
		socketUtil.broadcastGameState(getGameState());
		socketUtil.broadcastLobby(lobby);
	}

	public synchronized void roll(Player p, boolean force) {
		if (dg == null) {
			Log.info(String.format("%s tried to roll when no game was in progress", p.getName()));
			socketUtil.sendSystemChat(p, "Cannot roll. Game is not in progress");
			sendAppState(p);
			return;
		} else if (!dg.roll(p, force)) {
			socketUtil.sendSystemChat(p, "It is not your turn to roll");
			sendAppState(p);
			return;
		}

		// roll was successful, sleep 1 second so the client can build suspense
		//DiceGameUtil.sleep(1000);
		int lastRoll = dg.isPrevRollOne() ? 1 : dg.getCurrentRoll();
		String rollMsg = String.format("%s rolled %d", p.getName(), lastRoll);
		if (lastRoll == 1)
			rollMsg += " (RIP)";

		Log.info(rollMsg);
		socketUtil.broadcastSystemChat(rollMsg);
		socketUtil.broadcastGameState(getGameState());

		if (dg.isGameOver()) {
			// TODO persist DG stats
			// no winning player means they were playing alone
			String winMsg = (dg.getWinningPlayer() != null) ? String.format("%s wins!", dg.getWinningPlayer().getName())
				: String.format("%s loses. You were playing alone, what did you expect?", p.getName());
			Log.info(winMsg);
			socketUtil.broadcastSystemChat(winMsg);

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
		socketUtil.broadcastGameState(getGameState());
		socketUtil.broadcastLobby(lobby);
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
		socketUtil.broadcastLobby(lobby);
		socketUtil.broadcastGameState(getGameState());
	}

	public boolean isPlayerInLobbyOrGame(Player p) {
		return lobby.contains(p) || (dg != null && dg.isPlayerInGame(p));
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

	/**
	 * Sends an individual player the state of the entire app
	 */
	private void sendAppState(Player p) {
		socketUtil.sendLobby(p, lobby);
		socketUtil.sendGameState(p, getGameState());
	}

}
