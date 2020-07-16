package kmaput.discordktulu.game;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.role.RoleUpdateEvent;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.AllowedMentions;
import discord4j.core.object.entity.channel.Category;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.Reaction;
import discord4j.core.spec.TextChannelCreateSpec;
import discord4j.core.spec.TextChannelEditSpec;
import discord4j.core.util.PermissionUtil;
import discord4j.rest.util.Image.Format;
import discord4j.rest.util.PermissionSet;
import kmaput.discordktulu.controlledentities.ControlledCategory;
import kmaput.discordktulu.controlledentities.ControlledRole;
import kmaput.discordktulu.controlledentities.ControlledTextChannel;
import kmaput.discordktulu.util.GuildUtils;
import kmaput.discordktulu.util.PermissionUtils;
import kmaput.discordktulu.util.Translator;
import kmaput.discordktulu.util.Utils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class Game {
	private Guild guild;
	private Locale language;
	private ControlledCategory category;
	private ControlledRole manitouRole, aliveRole, deadRole;
	
	private Message applicationsMessage;
	private Set<Snowflake> waitingPlayers;
	private Set<KtuluRole> waitingRoles;
	
	private boolean gameRunning;
	private Map<Snowflake, Player> players;
	private Set<KtuluFraction> fractions;
	private ControlledTextChannel dayChannel, manitouChannel;
	private Map<KtuluFraction, ControlledTextChannel> fractionChannels;
	
	public Game() {
		fractionChannels = new HashMap<>();
		waitingPlayers = new HashSet<>();
		waitingRoles = new HashSet<>();
	
		category = new ControlledCategory(guild, new ControlledCategory.Properties().setName("Ktulu"));
		
		manitouRole = new ControlledRole(guild, new ControlledRole.Properties().setName("Manitou").setMentionable(true).setHoist(true));
		aliveRole = new ControlledRole(guild, new ControlledRole.Properties().setName("Alive").setMentionable(true).setHoist(true));
		deadRole = new ControlledRole(guild, new ControlledRole.Properties().setName("Dead").setMentionable(true).setHoist(true));
	
		//Przesun¹æ do pocz¹tku gry?
		
		manitouChannel = new ControlledTextChannel(guild, new ControlledTextChannel.Properties()
				.setName("Manitou control panel")
				.setPermissionOverwrites(new PermissionUtils.Builder()
						.and(PermissionUtils.forEveryone(guild.getEveryoneRole().block()))
						.and(PermissionUtils.forManitou(manitouRole.get().block()))
						.build())
				.setParentCategory(category));
		
		dayChannel = new ControlledTextChannel(guild, new ControlledTextChannel.Properties()
				.setName("Day")
				.setPermissionOverwrites(new PermissionUtils.Builder()
							.and(PermissionUtils.forEveryone(guild.getEveryoneRole().block()))
							.and(PermissionUtils.forManitou(manitouRole.get().block()))
							.and(PermissionUtils.forPlayers(aliveRole.get().block(), deadRole.get().block()))
							.build())
				.setParentCategory(category));
	}
	
	public String startApplications(MessageChannel channel) {
		if(applicationsMessage != null) {
			applicationsMessage.delete().block();
		}
		applicationsMessage = channel.createMessage(Translator.translate("applications", language)).block();
		return null;
	}
	
	public String endApplications() {
		if(applicationsMessage == null) {
			return Translator.translate("applications_do_not_exist", language);
		}
		waitingPlayers = Flux.fromStream(applicationsMessage.getReactions().stream())
			.map(Reaction::getEmoji)
			.flatMap(applicationsMessage::getReactors)
			.map(User::getId)
			.collect(Collectors.toSet())
			.block();
		applicationsMessage.delete().block();
		applicationsMessage = null;
		return null;
	}
	
	public String addPlayer(Snowflake player) {
		if(!Mono.just(guild)
			.flatMapMany(Guild::getMembers)
			.any(member -> member.getId().equals(player))
			.block()) {
			return Translator.translate("user_not_on_server", language);
		}
		waitingPlayers.add(player);
		return null;
	}
	
	public String removePlayer(Snowflake player) {
		if(!waitingPlayers.remove(player)) {
			return Translator.translate("user_not_applied", language);
		}
		return null;
	}
	
	public String addRole(KtuluRole role) {
		if(!waitingRoles.add(role)) {
			return Translator.translate("role_already_added", language);
		}
		return null;
	}
	
	public String removeRole(KtuluRole role) {
		if(!waitingRoles.remove(role)) {
			return Translator.translate("role_not_added", language);
		}
		return null;
	}
	
	public String startGame() {
		if(gameRunning) return Translator.translate("another_game_running", language);
		if(waitingPlayers.size() != waitingRoles.size()) return Translator.translate("players_and_roles_do_not_match", language);
		players = new HashMap<>();
		List<KtuluRole> shuffledRoles = new ArrayList<>(waitingRoles);
		Collections.shuffle(shuffledRoles);
		int i = 0;
		for(Snowflake user : waitingPlayers) {
			players.put(user, new Player(user, shuffledRoles.get(i++)));
		}
		fractions = shuffledRoles.stream().map(KtuluRole::getFraction).collect(Collectors.toSet());
		gameRunning = true;
		return Translator.translate("game_started", language);
	}
	
	private ControlledTextChannel makeFractionChannel(KtuluFraction fraction) {
		ControlledTextChannel channel = new ControlledTextChannel(guild, new ControlledTextChannel.Properties()
				.setName(fraction.getName())
				.setPermissionOverwrites(new PermissionUtils.Builder()
						.and(PermissionUtils.forEveryone(guild.getEveryoneRole().block()))
						.and(PermissionUtils.forManitou(manitouRole.get().block()))
						.build())
				.setParentCategory(category));
		fractionChannels.put(fraction, channel);
		return channel;
	}
	
	//TODO zipWith chyba nie dzia³a dla pustych
	
	private Mono<Void> fixAliveRoles() {
		Snowflake aliveId = aliveRole.get().map(Role::getId).block();
		Snowflake deadId = deadRole.get().map(Role::getId).block();
		return Flux.fromIterable(players.values()).parallel().runOn(Schedulers.parallel())
			.flatMap(p -> guild.getMemberById(p.getId()).zipWith(Mono.just(p.isAlive())))
			.flatMap(d -> d.getT1().addRole(d.getT2() ? aliveId : deadId).and(d.getT1().removeRole(d.getT2() ? deadId : aliveId)))
			.then();
	}
	
	private Mono<Void> sendAllRoles() {
		return Flux.fromIterable(players.values()).parallel().runOn(Schedulers.parallel())
				.flatMap(p -> guild.getMemberById(p.getId()).zipWith(Mono.just(p.getRole())))
				.flatMap(d -> sendRole(d.getT1(), d.getT2()))
				.then();
	}
	
	private Mono<Message> sendRole(Member member, KtuluRole role) {
		return member.getPrivateChannel()
			.flatMap(channel -> channel.createMessage(spec -> {
				spec.setEmbed(embedspec -> {
					embedspec.setAuthor(guild.getName(), null, guild.getIconUrl(Format.JPEG).orElse(null));
					embedspec.setDescription(String.format(Translator.translate("message.your_role", language), Translator.translate(role.getName(), language)));
				});
				if(role.getImage() != null) {
					try {
						spec.addFile(Translator.translate(role.getName(), language), Files.newInputStream(role.getImage()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}));
	}
}
