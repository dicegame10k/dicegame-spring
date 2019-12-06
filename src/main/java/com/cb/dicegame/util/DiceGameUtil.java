package com.cb.dicegame.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;

public class DiceGameUtil {

	private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

	public static PasswordEncoder getPasswordEncoder() {
		return PASSWORD_ENCODER;
	}

	public static String getPlayerName(Principal principal) {
		return principal.getName();
	}

	/**
	 * Returns the username of the user making the request
	 */
	public static String getPlayerName() {
		try {
			return SecurityContextHolder.getContext().getAuthentication().getName();
		} catch (Exception e) {
			Log.error("Error in DiceGameUtil.getPlayerName");
			Log.error(e.getMessage());
			return null;
		}
	}

}
