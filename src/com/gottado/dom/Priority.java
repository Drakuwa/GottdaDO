package com.gottado.dom;

/**
 * a priority ENUM for the tasks
 * @author drakuwa
 *
 */
public enum Priority {
	LOW, NORMAL, HIGH;

	public int getCustomOrdinal() {
		switch (this) {
		case LOW:
			return 0;
		case NORMAL:
			return 1;
		case HIGH:
			return 2;
		default:
			return 0;
		}
	}
}
