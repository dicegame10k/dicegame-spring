package com.cb.dicegame.util;

import java.util.logging.Logger;

public class Log {

	public static void info(String message) {
		Logger.getGlobal().info(message);
	}

	public static void warn(String message) {
		Logger.getGlobal().warning(message);
	}

	public static void error(String message) {
		Logger.getGlobal().severe(message);
	}

}
