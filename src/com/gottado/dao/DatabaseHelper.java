package com.gottado.dao;

/**
 * DAO helper class
 * @author drakuwa
 *
 */
public final class DatabaseHelper {

	// Tasks Table Columns names
	public static final String KEY_ID = "id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_DATE = "dueDate";
	public static final String KEY_PRIORITY = "proirity";
	public static final String KEY_COMPLETED = "isCompleted";
	public static final String KEY_CATEGORY = "category";

	public static enum COLUMN_ORDER {
		KEY_ID, KEY_TITLE, KEY_DESCRIPTION, KEY_DATE, KEY_PRIORITY, 
		KEY_COMPLETED, KEY_CATEGORY;
		public int getItemOrdinal(COLUMN_ORDER item) {
			return item.getItemOrdinal(item);
		}
	}

	//
	public static final String[] TASK_COLUMNS = { KEY_ID, KEY_TITLE,
			KEY_DESCRIPTION, KEY_DATE, KEY_PRIORITY, KEY_COMPLETED,
			KEY_CATEGORY };

	private DatabaseHelper() {
	}
}
