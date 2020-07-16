package kmaput.discordktulu;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import kmaput.discordktulu.game.Game;
import kmaput.discordktulu.util.NonblockingScanner;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/* TODO
 * 3. komendy (brigadier?) i ich implementacja
 *  - kana� b�dzie inicjalizowany gdy manitou zmieni faz� gry na dan� frakcj�
 *  - pozosta�e akcje
 * 4. SYNCHRONIZACJA!
 * 5. wczytywanie roli i frakcji z plik�w (jaki� json?)
 *  - struktura:
 *    - frakcja: folder z rolami, plik z w�a�ciwo�ciami
 *    - rola: plik z w�a�ciwo�ciami, obrazek
 * 6. Konstruktor Game: wczytywanie z pliku
 * 7. wyczytywanie plik�w j�zykowych i lepsze klucze j�zykowe
 * 8. Cachowanie kana��w w wrapperach(je�li nie pojawi si� event, to u�ywa poprzedniego)
 * 9. Usun��/wyczy�ci� GuildUtils
 */
public class KtuluDiscordBot {
	public static KtuluDiscordBot INSTANCE;
	
	public final GatewayDiscordClient client;
	private boolean connected;
	
	private Map<Snowflake, Game> games;
	
	public static void main(String[] args) {
		if(args.length < 1) {
			System.out.println("Bot token not specified!");
			System.exit(0);
		}
		String token = args[0];
		INSTANCE = new KtuluDiscordBot(token);
		INSTANCE.start();
	}
	
	public KtuluDiscordBot(String token) {
		Mono<GatewayDiscordClient> login = Mono.fromFuture(CompletableFuture.supplyAsync(DiscordClient.create(token).login()::block));
		
		games = new HashMap<>();
		//TODO �adowanie r�l, frakcji, translacji i gier
		
		client = login.block();
		connected = true;
		
		client.getEventDispatcher().on(ReadyEvent.class)
			.subscribe(event -> System.out.println("Ready!"));
		
		client.getEventDispatcher().on(MessageCreateEvent.class)
			.map(MessageCreateEvent::getMessage)
			.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
			.filter(message -> message.getContent().equalsIgnoreCase("hello"))
			.subscribe(message -> message.getChannel().block().createMessage("Hello " + message.getAuthor().get().getMention()).block());
		
		client.onDisconnect()
			.subscribe(none -> {
				System.out.println("Disconnected");
				connected = false;
			});
	}
	
	public void start() {
		try(NonblockingScanner scanner = new NonblockingScanner(new Scanner(System.in))) {
			String input;
			while(connected) {
				input = scanner.nextLine().block(Duration.ofMillis(20));
				if(input == null) continue;
				if(input.equalsIgnoreCase("exit")) {
					client.logout().block();
					connected = false;
				}
			}
			//TODO save games
		}
	}
	
	private synchronized Game getOrCreate(Snowflake guild) {
		Game game = games.get(guild);
		if(game == null) {
			game = new Game();
			games.put(guild, game);
		}
		return game;
	}
}
