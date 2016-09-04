package org.imaginecraft.apocalypse.events;

public enum ApocChatType {
	/**
	 * Player will chat normally
	 */
	OFF,
	/**
	 * Player messages will only be sent to all event participants
	 */
	PUBLIC,
	/**
	 * Player messages will only be sent to teammates
	 */
	TEAM;
	
}
