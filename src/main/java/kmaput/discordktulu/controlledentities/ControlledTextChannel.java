package kmaput.discordktulu.controlledentities;

import java.util.Set;

import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.TextChannelCreateSpec;
import discord4j.core.spec.TextChannelEditSpec;
import kmaput.discordktulu.util.PermissionUtils;
import kmaput.discordktulu.util.Utils;
import reactor.core.publisher.Mono;

public class ControlledTextChannel {
	private Guild guild;
	private TextChannel channel;
	private Properties properties;
	
	public ControlledTextChannel(Guild guild) {
		this(guild, new Properties());
	}
	
	public ControlledTextChannel(Guild guild, Properties properties) {
		this.guild = guild;
		this.properties = properties;
	}
	
	public Mono<TextChannel> get() {
		return Mono.defer(
				() -> Mono.just(channel)
						.filter(c -> c != null)
						.map(Channel::getId)
						.flatMap(guild::getChannelById)
						.filter(c -> c.getType() == Channel.Type.GUILD_TEXT)
						.cast(TextChannel.class)
						.flatMap(c -> properties.match(c) ? Mono.just(c) : c.edit(properties::editSpec))
						.switchIfEmpty(guild.createTextChannel(properties::createSpec))
		);
	}
	
	public ControlledTextChannel setProperties(Properties properties) {
		if(properties == null) this.properties = new Properties();
		else this.properties = properties;
		return this;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public static class Properties {
		private String name;
		private Boolean nsfw;
		private ControlledCategory parentCategory;
		private Set<PermissionOverwrite> permissionOverwrites;
		private Integer position;
		private Integer rateLimitPerUser;
		private String topic;
		
		public Properties setName(String name) {
			this.name = name;
			return this;
		}
		
		public Properties setNsfw(boolean nsfw) {
			this.nsfw = nsfw;
			return this;
		}
		
		public Properties setParentCategory(ControlledCategory parentCategory) {
			this.parentCategory = parentCategory;
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
		
		public Properties setRateLimitPerUser(int rateLimitPerUser) {
			this.rateLimitPerUser = rateLimitPerUser;
			return this;
		}
		
		public Properties setTopic(String topic) {
			this.topic = topic;
			return this;
		}
		
		public void createSpec(TextChannelCreateSpec spec) {
			if(name					!= null) spec.setName(name);
			if(nsfw					!= null) spec.setNsfw(nsfw);
			if(parentCategory		!= null) spec.setParentId(parentCategory.get().map(Channel::getId).block());
			if(permissionOverwrites	!= null) spec.setPermissionOverwrites(permissionOverwrites);
			if(position				!= null) spec.setPosition(position);
			if(rateLimitPerUser		!= null) spec.setRateLimitPerUser(rateLimitPerUser);
			if(topic				!= null) spec.setTopic(topic);
		}
		
		public void editSpec(TextChannelEditSpec spec) {
			if(name					!= null) spec.setName(name);
			if(nsfw					!= null) spec.setNsfw(nsfw);
			if(parentCategory		!= null) spec.setParentId(parentCategory.get().map(Channel::getId).block());
			if(permissionOverwrites	!= null) spec.setPermissionOverwrites(permissionOverwrites);
			if(position				!= null) spec.setPosition(position);
			if(rateLimitPerUser		!= null) spec.setRateLimitPerUser(rateLimitPerUser);
			if(topic				!= null) spec.setTopic(topic);
		}
		
		public boolean match(TextChannel channel) {
			return 
				Utils.testProperty(channel.getName(), name) &&
				Utils.testProperty(channel.isNsfw(), nsfw) &&
				Utils.testProperty(channel.getCategoryId().orElse(null), parentCategory.get().map(Channel::getId).block()) &&
				PermissionUtils.areEqual(channel.getPermissionOverwrites(), permissionOverwrites) &&
				Utils.testProperty(channel.getPosition(), position) &&
				Utils.testProperty(channel.getRateLimitPerUser(), rateLimitPerUser) &&
				Utils.testProperty(channel.getTopic(), topic);
		}
	}
}
