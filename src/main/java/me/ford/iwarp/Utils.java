package me.ford.iwarp;

import java.text.NumberFormat;

import org.bukkit.ChatColor;

public final class Utils {
	
	private Utils() { throw new UnsupportedOperationException();}
	
	public static String color(String c) {
		if (c == null) c = ""; // avoid NPEs
		return ChatColor.translateAlternateColorCodes('&', c);
	}
	
	public static String doubleFormat(double nr) {
		return doubleFormat(nr, 2);
	}
	
	public static String doubleFormat(double nr, int digits) {
        final NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(digits);
        nf.setMinimumFractionDigits(digits);
        return nf.format(nr);
	}

}
