package com.cb.dicegame.model;

import com.cb.dicegame.util.Log;
import com.cb.dicegame.util.StringUtil;

public enum WowClass {
	DEATH_KNIGHT,
	DEMON_HUNTER,
	DRUID,
	HUNTER,
	MAGE,
	MONK,
	PALADIN,
	PRIEST,
	ROGUE,
	SHAMAN,
	WARLOCK,
	WARRIOR;

	public static WowClass fromString(String wowClass) {
		wowClass = wowClass.replace("-", "_").toUpperCase();
		for (WowClass wc : WowClass.values())
			if (StringUtil.areEqual(wc.toString(), wowClass))
				return wc;

		Log.info(String.format("WowClass fromString did not find class '%s'", wowClass));
		return MONK;
	}

}
