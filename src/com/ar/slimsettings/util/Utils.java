package com.ar.slimsettings.util;

import java.net.URL;

public class Utils {
    
	public static Integer TryParse(String text) {
		text = getOnlyNumerics(text);
		try {
			return new Integer(text);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	public static <T> T last(T[] array) {
	    return array[array.length - 1];
	}
	public static String getFilenameFromURL (URL url) {
		return last(url.getFile().split("/")).substring(0);
	}
	public static String getOnlyNumerics(String str) {

		if (str == null) {
			return null;
		}

		StringBuffer strBuff = new StringBuffer();
		char c;

		for (int i = 0; i < str.length(); i++) {
			c = str.charAt(i);

			if (Character.isDigit(c)) {
				strBuff.append(c);
			}
		}
		return strBuff.toString();
	}
    
}
