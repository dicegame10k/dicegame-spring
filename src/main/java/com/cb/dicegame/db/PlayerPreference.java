package com.cb.dicegame.db;

import com.cb.dicegame.model.Particles;

import javax.persistence.*;

@Entity
public class PlayerPreference {

	private @Id @GeneratedValue Long id;
	private @OneToOne Player player;

	private Particles particles;

	public PlayerPreference() {
	}

	public PlayerPreference(Player player, Particles particles) {
		this.player = player;
		this.particles = particles;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Particles getParticles() {
		return particles;
	}

	public void setParticles(Particles particles) {
		this.particles = particles;
	}
}
