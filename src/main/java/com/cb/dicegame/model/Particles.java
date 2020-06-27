package com.cb.dicegame.model;

import com.cb.dicegame.util.Log;
import com.cb.dicegame.util.StringUtil;

public enum Particles {
	OFF,
	LINES,
	SNOW;

	public static Particles fromString(String particles) {
		particles = particles.toUpperCase();
		for (Particles p : Particles.values()) {
			if (StringUtil.areEqual(p.toString(), particles))
				return p;
		}

		Log.info(String.format("Particles fromString did not find particles '%s'", particles));
		return LINES;
	}
}
