package com.cb.dicegame.util;

import com.cb.dicegame.IDiceGameConstants;
import com.cb.dicegame.model.ChatMessage;
import com.cb.dicegame.model.Player;
import com.cb.dicegame.model.WowClass;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class SocketUtil implements IDiceGameConstants {

	private final SimpMessagingTemplate socket;

	private final Player SYSTEM = new Player("system", "10k", WowClass.PRIEST, 0);

	public SocketUtil(SimpMessagingTemplate socket) {
		this.socket = socket;
	}

	public void sendSystemChat(Player p, String msg) {
		this.socket.convertAndSendToUser(p.getName(), CHAT_QUEUE, new ChatMessage(SYSTEM, msg));
	}

	public void broadcastSystemChat(String msg) {
		this.socket.convertAndSend(CHAT_TOPIC, new ChatMessage(SYSTEM, msg));
	}

	public void broadcastChat(Player fromPlayer, String msg) {
		this.socket.convertAndSend(CHAT_TOPIC, new ChatMessage(fromPlayer, msg));
	}

	public void sendLobby(Player p, List<Player> lobby) {
		this.socket.convertAndSendToUser(p.getName(), LOBBY_QUEUE, lobby);
	}

	public void broadcastLobby(List<Player> lobby) {
		this.socket.convertAndSend(LOBBY_TOPIC, lobby);
	}

	public void sendGameState(Player p, HashMap<String, Object> gameState) {
		this.socket.convertAndSendToUser(p.getName(), GAMESTATE_QUEUE, gameState);
	}

	public void broadcastGameState(HashMap<String, Object> gameState) {
		this.socket.convertAndSend(GAMESTATE_TOPIC, gameState);
	}

	public void sendLogout(Player p) {
		this.socket.convertAndSendToUser(p.getName(), LOGOUT_QUEUE, "");
	}

}
