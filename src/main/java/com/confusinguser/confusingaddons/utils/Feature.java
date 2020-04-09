package com.confusinguser.confusingaddons.utils;

import com.confusinguser.confusingaddons.ConfusingAddons;

public enum Feature {
	HIDE_LOBBY_SPAM(0),
	SHOW_CLICK_COMMANDS(1),
	HIDE_JOIN_LEAVE_MESSAGES(2),
	CLEANER_KICK_ERROR_MESSAGES(3),
	SHOW_FORMATTING_CODES(4);
	
	private boolean status;
	private long id;
	
	private Feature(long id) {
		this.id = id;
	}
	
	public void setStatus(boolean status) {
		this.status = status;
		ConfusingAddons.getInstance().getConfigValues().saveConfig();
	}

	public boolean getStatus() {
		return status;
	}

	public long getIdLong() {
		return id;
	}
	
	public String getId() {
		return String.valueOf(id);
	}
	
	public static Feature getFeatureById(long id) {
		for (Feature feature : values()) {
			if (feature.getIdLong() == id) return feature;
		}
		return null;
	}
	
	public static Feature getFeatureById(String id) {
		for (Feature feature : values()) {
			if (feature.getId().contentEquals(id)) return feature;
		}
		return null;
	}
}
