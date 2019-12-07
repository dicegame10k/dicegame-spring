package com.cb.dicegame;

public interface IDiceGameConstants {

	String SOCKET_URL_ENDPOINT = "/10k";
	String SOCKET_MESSAGE_PREFIX = "/app";
	String LOBBY_TOPIC = "/topic/lobby";
	String LOBBY_QUEUE = "/queue/lobby";
	String GAMESTATE_TOPIC = "/topic/gameState";
	String GAMESTATE_QUEUE = "/queue/gameState";
	String CHAT_TOPIC = "/topic/chat";
	String CHAT_QUEUE = "/queue/chat";
	String LOGOUT_QUEUE = "/queue/logout";

	// JSON keys
	String GAME_IN_PROGRESS = "gameInProgress";
	String DG_PLAYERS = "dgPlayers";
	String GRAVEYARD = "graveyard";
	String CURRENTLY_ROLLING_PLAYER = "currentlyRollingPlayer";
	String CURRENT_ROLL = "currentRoll";

}
