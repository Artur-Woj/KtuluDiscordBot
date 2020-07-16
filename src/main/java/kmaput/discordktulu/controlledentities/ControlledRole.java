package kmaput.discordktulu.controlledentities;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Role;
import discord4j.core.spec.RoleCreateSpec;
import discord4j.core.spec.RoleEditSpec;
import discord4j.rest.util.Color;
import discord4j.rest.util.PermissionSet;
import kmaput.discordktulu.util.Utils;
import reactor.core.publisher.Mono;

public class ControlledRole {
	private Guild guild;
	private Role role;
	private Properties properties;
	
	public ControlledRole(Guild guild) {
		this(guild, new Properties());
	}
	
	public ControlledRole(Guild guild, Properties properties) {
		this.guild = guild;
		this.properties = properties;
	}
	
	public Mono<Role> get() {
		return Mono.defer(
				() -> Mono.just(role)
						.filter(r -> r != null)
						.map(Role::getId)
						.flatMap(guild::getRoleById)
						.flatMap(r -> properties.match(r) ? Mono.just(r) : r.edit(properties::editSpec))
						.switchIfEmpty(guild.createRole(properties::createSpec))
		);
	}
	
	public ControlledRole setProperties(Properties properties) {
		if(properties == null) this.properties = new Properties();
		else this.properties = properties;
		return this;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public static class Properties {
		private Color color;
		private Boolean hoist;
		private Boolean mentionable;
		private String name;
		private PermissionSet permissions;
		
		public Properties setColor(Color color) {
			this.color = color;
			return this;
		}

		public Properties setHoist(Boolean hoist) {
			this.hoist = hoist;
			return this;
		}

		public Properties setMentionable(Boolean mentionable) {
			this.mentionable = mentionable;
			return this;
		}

		public Properties setName(String name) {
			this.name = name;
			return this;
		}

		public Properties setPermissions(PermissionSet permissions) {
			this.permissions = permissions;
			return this;
		}

		public void createSpec(RoleCreateSpec spec) {
			if(color		!= null) spec.setColor(color);
			if(hoist		!= null) spec.setHoist(hoist);
			if(mentionable	!= null) spec.setMentionable(mentionable);
			if(name			!= null) spec.setName(name);
			if(permissions	!= null) spec.setPermissions(permissions);
		}
		
		public void editSpec(RoleEditSpec spec) {
			if(color		!= null) spec.setColor(color);
			if(hoist		!= null) spec.setHoist(hoist);
			if(mentionable	!= null) spec.setMentionable(mentionable);
			if(name			!= null) spec.setName(name);
			if(permissions	!= null) spec.setPermissions(permissions);
		}
		
		public boolean match(Role role) {
			return 
				Utils.testProperty(role.getColor(), color) &&
				Utils.testProperty(role.isHoisted(), hoist) && 
				Utils.testProperty(role.isMentionable(), mentionable) &&
				Utils.testProperty(role.getName(), name) &&
				Utils.testProperty(role.getPermissions(), permissions);
		}
	}
}
