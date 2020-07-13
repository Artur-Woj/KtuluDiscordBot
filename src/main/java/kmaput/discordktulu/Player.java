package kmaput.discordktulu;

import discord4j.common.util.Snowflake;

public class Player {
	private Snowflake user;
	private Role role;
	private boolean isAlive;
	
	public Player(Snowflake user, Role role) {
		this.user = user;
		this.role = role;
		this.isAlive = true;
	}
}
