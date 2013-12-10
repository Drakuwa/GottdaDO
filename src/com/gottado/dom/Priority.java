package com.gottado.dom;

public enum Priority {
	LOW, MEDIUM, HIGH;

	public int getCustomOrdinal() {
		switch (this) {
		case LOW:
			return 0;
		case MEDIUM:
			return 1;
		case HIGH:
			return 2;
		default:
			return 0;
		}
	}
}
