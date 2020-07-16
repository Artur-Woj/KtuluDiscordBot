package kmaput.discordktulu.game;

import discord4j.common.util.Snowflake;

public class Player {
	private Snowflake id;
	private KtuluRole role;
	private boolean alive;
	
	public Player(Snowflake id, KtuluRole role) {
		this.id = id;
		this.role = role;
		this.alive = true;
	}
	
	public Snowflake getId() {
		return id;
	}
	
	public KtuluRole getRole() {
		return role;
	}
	
	public boolean isAlive() {
		return alive;
	}
}
