package com.cb.dicegame.util;

public class StringUtil {

	public static boolean areEqual(String a, String b) {
		a = (a == null) ? "" : a;
		b = (b == null) ? "" : b;
		return a.equals(b);
	}

	public static boolean areNotEqual(String a, String b) {
		return !areEqual(a, b);
	}

	public static boolean nil(String s) {
		return s == null || s.equals("");
	}

	public static boolean notNil(String s) {
		return !nil(s);
	}

}
