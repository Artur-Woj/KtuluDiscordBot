package kmaput.discordktulu.util;

import java.util.function.Consumer;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.channel.Category;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.CategoryCreateSpec;
import discord4j.core.spec.RoleCreateSpec;
import discord4j.core.spec.TextChannelCreateSpec;

public class GuildUtils {
	public static boolean memberExists(Guild guild, Snowflake id) {
		return id != null && guild.getMemberById(id).block() != null;
	}
	
	public static boolean memberExists(Guild guild, Member member) {
		return member != null && guild.getMemberById(member.getId()).block() != null;
	}
	
	public static boolean channelExists(Guild guild, Snowflake id) {
		return id != null && guild.getChannelById(id).block() != null;
	}
	
	public static boolean channelExists(Guild guild, GuildChannel channel) {
		return channel != null && guild.getChannelById(channel.getId()).block() != null;
	}
	
	public static boolean roleExists(Guild guild, Snowflake id) {
		return id != null && guild.getRoleById(id).block() != null;
	}
	
	public static boolean roleExists(Guild guild, Role role) {
		return role != null && guild.getRoleById(role.getId()).block() != null;
	}
	
	public static TextChannel getTextChannelOrCreate(Guild guild, Snowflake id, Consumer<? super TextChannelCreateSpec> spec) {
		if(id != null) {
			GuildChannel channel = guild.getChannelById(id).block();
			if(channel != null && channel.getType() == Channel.Type.GUILD_TEXT) {
				return (TextChannel) channel;
			}
		}
		return guild.createTextChannel(spec).block();
	}
	
	public static TextChannel getTextChannelOrCreate(Guild guild, TextChannel textChannel, Consumer<? super TextChannelCreateSpec> spec) {
		if(textChannel != null) {
			GuildChannel channel = guild.getChannelById(textChannel.getId()).block();
			if(channel != null && channel.getType() == Channel.Type.GUILD_TEXT) {
				return textChannel;
			}
		}
		return guild.createTextChannel(spec).block();
	}
	
	public static Category getCategoryOrCreate(Guild guild, Snowflake id, Consumer<? super CategoryCreateSpec> spec) {
		if(id != null) {
			GuildChannel channel = guild.getChannelById(id).block();
			if(channel != null && channel.getType() == Channel.Type.GUILD_CATEGORY) {
				return (Category) channel;
			}
		}
		return guild.createCategory(spec).block();
	}
	
	public static Category getCategoryOrCreate(Guild guild, Category category, Consumer<? super CategoryCreateSpec> spec) {
		if(category != null) {
			GuildChannel channel = guild.getChannelById(category.getId()).block();
			if(channel != null && channel.getType() == Channel.Type.GUILD_CATEGORY) {
				return category;
			}
		}
		return guild.createCategory(spec).block();
	}
	
	public static Role getRoleOrCreate(Guild guild, Snowflake id, Consumer<? super RoleCreateSpec> spec) {
		if(id != null) {
			Role role = guild.getRoleById(id).block();
			if(role != null) {
				return role;
			}
		}
		return guild.createRole(spec).block();
	}
	
	public static Role getRoleOrCreate(Guild guild, Role role, Consumer<? super RoleCreateSpec> spec) {
		if(role != null) {
			if(guild.getRoleById(role.getId()).block() != null) {
				return role;
			}
		}
		return guild.createRole(spec).block();
	}
}
