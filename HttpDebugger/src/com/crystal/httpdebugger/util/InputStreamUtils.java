package com.crystal.httpdebugger.util;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamUtils {
	public static String readLine(InputStream is) throws IOException {
		if (is == null) throw new NullPointerException("InputStream may not be null!");
		StringBuffer line = new StringBuffer();
		int i;
		char c = 0x00;
		i = is.read();
		if (i == -1) return null;
		while (i > -1 && i != 10 && i != 13) {
			c = (char) (i & 0xFF);
			line = line.append(c);
			i = is.read();
		}
		if (i == 13) {
			i = is.read();
		}
		return line.toString();
	}
}
