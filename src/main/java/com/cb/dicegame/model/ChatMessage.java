package com.cb.dicegame.model;

import com.cb.dicegame.db.Player;

public class ChatMessage {

	private Player player;
	private String msg;

	public ChatMessage(Player player, String msg) {
		this.player = player;
		this.msg = msg;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
