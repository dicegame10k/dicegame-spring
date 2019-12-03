package com.cb.dicegame.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class DiceGameUtil {

	private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

	public static PasswordEncoder getPasswordEncoder() {
		return PASSWORD_ENCODER;
	}

}
