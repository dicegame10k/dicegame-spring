package com.cb.dicegame.model;

import com.cb.dicegame.util.DiceGameUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Player {

	private @Id @GeneratedValue Long id;
	private String name;
	private @JsonIgnore String password;

	private WowClass wowClass;
	private long dkp;

	public Player() {
	}

	public Player(String name, String password, WowClass wowClass, long dkp) {
		this.name = name;
		this.password = DiceGameUtil.getPasswordEncoder().encode(password);
		this.wowClass = wowClass;
		this.dkp = dkp;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	// Ensures that passwords are never stored in cleartext
	public void setPassword(String password) {
		this.password = DiceGameUtil.getPasswordEncoder().encode(password);
	}

	public WowClass getWowClass() {
		return wowClass;
	}

	public void setWowClass(WowClass wowClass) {
		this.wowClass = wowClass;
	}

	public long getDkp() {
		return dkp;
	}

	public void setDkp(long dkp) {
		this.dkp = dkp;
	}

	@Override
	public String toString() {
		return "Player{" +
				"id=" + id +
				", name='" + name + '\'' +
				", password='" + password + '\'' +
				", wowClass=" + wowClass +
				", dkp=" + dkp +
				'}';
	}
}
