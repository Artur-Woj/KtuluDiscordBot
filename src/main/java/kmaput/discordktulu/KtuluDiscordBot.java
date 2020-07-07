package kmaput.discordktulu;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class KtuluDiscordBot {
	public static void main(String[] args) {
		if(args.length < 1) {
			System.out.println("Bot token not specified!");
			System.exit(0);
		}
		String token = args[0];
		GatewayDiscordClient client = DiscordClient.create(token).login().block();
		
		client.getEventDispatcher().on(ReadyEvent.class)
			.subscribe(event -> System.out.println("Ready!"));
		
		client.getEventDispatcher().on(MessageCreateEvent.class)
			.map(MessageCreateEvent::getMessage)
			.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
			.filter(message -> message.getContent().equalsIgnoreCase("hello"))
			.subscribe(message -> message.getChannel().block().createMessage("Hello " + message.getAuthor().get().getMention()).block());
		
		client.onDisconnect().block();
	}
}
