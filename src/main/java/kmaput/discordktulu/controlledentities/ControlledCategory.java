package kmaput.discordktulu.controlledentities;

import java.util.Set;

import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.Category;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.spec.CategoryCreateSpec;
import discord4j.core.spec.CategoryEditSpec;
import kmaput.discordktulu.util.PermissionUtils;
import kmaput.discordktulu.util.Utils;
import reactor.core.publisher.Mono;

public class ControlledCategory {
	private Guild guild;
	private Category category;
	private Properties properties;
	
	public ControlledCategory(Guild guild) {
		this(guild, new Properties());
	}
	
	public ControlledCategory(Guild guild, Properties properties) {
		this.guild = guild;
		this.properties = properties;
	}
	
	public Mono<Category> get() {
		return Mono.defer(
				() -> Mono.just(category)
						.filter(c -> c != null)
						.map(Channel::getId)
						.flatMap(guild::getChannelById)
						.filter(c -> c.getType() == Channel.Type.GUILD_CATEGORY)
						.cast(Category.class)
						.flatMap(c -> properties.match(c) ? Mono.just(c) : c.edit(properties::editSpec))
						.switchIfEmpty(guild.createCategory(properties::createSpec))
		);
	}
	
	public ControlledCategory setProperties(Properties properties) {
		if(properties == null) this.properties = new Properties();
		else this.properties = properties;
		return this;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public static class Properties {
		private String name;
		private Set<PermissionOverwrite> permissionOverwrites;
		private Integer position;
		
		public Properties setName(String name) {
			this.name = name;
			return this;
		}
		
		public Properties setPermissionOverwrites(Set<PermissionOverwrite> permissionOverwrites) {
			this.permissionOverwrites = permissionOverwrites;
			return this;
		}
		
		public Properties setPosition(int position) {
			this.position = position;
			return this;
		}
		
		public void createSpec(CategoryCreateSpec spec) {
			if(name					!= null) spec.setName(name);
			if(permissionOverwrites	!= null) spec.setPermissionOverwrites(permissionOverwrites);
			if(position				!= null) spec.setPosition(position);
		}
		
		public void editSpec(CategoryEditSpec spec) {
			if(name					!= null) spec.setName(name);
			if(permissionOverwrites	!= null) spec.setPermissionOverwrites(permissionOverwrites);
			if(position				!= null) spec.setPosition(position);
		}
		
		public boolean match(Category category) {
			return 
				Utils.testProperty(category.getName(), name) &&
				PermissionUtils.areEqual(category.getPermissionOverwrites(), permissionOverwrites) &&
				Utils.testProperty(category.getPosition(), position);
		}
	}
}
