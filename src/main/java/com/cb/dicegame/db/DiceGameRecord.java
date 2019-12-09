package com.cb.dicegame.db;

import javax.persistence.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity
public class DiceGameRecord {

	private @Id @GeneratedValue Long id;
	private @ManyToMany List<Player> players;
	private @ManyToOne Player winningPlayer;
	private Date gameTime;
	private String gameTimeStr;
	private int numPlayers;
	private int numRolls;

	public DiceGameRecord() {
	}

	public DiceGameRecord(List<Player> players, Player winningPlayer, int numPlayers, int numRolls, Date gameTime) {
		this.players = players;
		this.winningPlayer = winningPlayer;
		this.numPlayers = numPlayers;
		this.numRolls = numRolls;
		this.gameTime = gameTime;
		this.gameTimeStr = new SimpleDateFormat().format(gameTime);
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Player getWinningPlayer() {
		return winningPlayer;
	}

	public void setWinningPlayer(Player winningPlayer) {
		this.winningPlayer = winningPlayer;
	}

	public int getNumPlayers() {
		return numPlayers;
	}

	public void setNumPlayers(int numPlayers) {
		this.numPlayers = numPlayers;
	}

	public int getNumRolls() {
		return numRolls;
	}

	public void setNumRolls(int numRolls) {
		this.numRolls = numRolls;
	}

	public Date getGameTime() {
		return gameTime;
	}

	public void setGameTime(Date gameTime) {
		this.gameTime = gameTime;
	}

	public String getGameTimeStr() {
		return gameTimeStr;
	}

	public void setGameTimeStr(String gameTimeStr) {
		this.gameTimeStr = gameTimeStr;
	}
}
