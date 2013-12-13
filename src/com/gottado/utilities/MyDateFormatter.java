package com.gottado.utilities;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * a simple date formatter used in the application
 * @author drakuwa
 *
 */
public class MyDateFormatter {
	private static final MyDateFormatter instance = new MyDateFormatter();
	private SimpleDateFormat sdf;
	
	private MyDateFormatter() {
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	}

	public SimpleDateFormat getDateFormat() {
		return sdf;
	}
	
	public static MyDateFormatter getInstance() {
		return instance;
	}
}
