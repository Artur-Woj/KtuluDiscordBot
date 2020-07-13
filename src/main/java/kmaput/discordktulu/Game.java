package kmaput.discordktulu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.Reaction;
import reactor.core.publisher.Flux;

public class Game {
	private Snowflake guild;
	private Locale language;
	private Map<Fraction, Snowflake> fractionChannels;
	
	private Snowflake applicationsMessageChannel;
	private Snowflake applicationsMessage;
	private Set<Snowflake> lobby;
	private Set<Role> lobbyRoles;
	
	private boolean gameRunning;
	private Map<Snowflake, Player> players;
	
	public Game() {
		fractionChannels = new HashMap<>();
		lobby = new HashSet<>();
		lobbyRoles = new HashSet<>();
		//TODO
	}
	
	public String startApplications(MessageChannel channel) {
		if(applicationsMessageChannel != null && applicationsMessage != null) {
			Message message = KtuluDiscordBot.INSTANCE.client.getMessageById(applicationsMessageChannel, applicationsMessage).block();
			message.delete().block();
		}
		applicationsMessageChannel = channel.getId();
		applicationsMessage = channel.createMessage(Translator.translate("applications", language)).block().getId();
		return null;
	}
	
	public String endApplications() {
		if(applicationsMessageChannel == null || applicationsMessage == null) {
			return Translator.translate("applications_do_not_exist", language);
		}
		//TODO co siê dzieje, gdy wiadomoœæ jest usuniêta?
		Message message = KtuluDiscordBot.INSTANCE.client.getMessageById(applicationsMessageChannel, applicationsMessage).block();
		lobby = Flux.fromStream(message.getReactions().stream())
			.map(Reaction::getEmoji)
			.flatMap(message::getReactors)
			.map(User::getId)
			.collect(Collectors.toSet())
			.block();
		message.delete().block();
		applicationsMessage = applicationsMessageChannel = null;
		return null;
	}
	
	public String addPlayer(Snowflake player) {
		if(!KtuluDiscordBot.INSTANCE.client.getGuildById(guild)
			.flatMapMany(Guild::getMembers)
			.any(member -> member.getId().equals(player))
			.block()) {
			return Translator.translate("user_not_on_server", language);
		}
		lobby.add(player);
		return null;
	}
	
	public String removePlayer(Snowflake player) {
		if(!lobby.remove(player)) {
			return Translator.translate("user_not_applied", language);
		}
		return null;
	}
	
	public String startGame() {
		if(gameRunning) return Translator.translate("another_game_running", language);
		if(lobby.size() != lobbyRoles.size()) return Translator.translate("players_and_roles_do_not_match", language);
		players = new HashMap<>();
		List<Role> shuffledRoles = new ArrayList<>(lobbyRoles);
		Collections.shuffle(shuffledRoles);
		int i = 0;
		for(Snowflake user : lobby) {
			players.put(user, new Player(user, shuffledRoles.get(i++)));
		}
		gameRunning = true;
		return Translator.translate("game_started", language);
	}
}
