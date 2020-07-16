package kmaput.discordktulu.init;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import com.google.gson.Gson;

import kmaput.discordktulu.game.KtuluFraction;
import kmaput.discordktulu.game.KtuluRole;

//TODO
public class Ktulu {
	private static Map<String, KtuluFraction> fractions;
	private static Map<String, KtuluRole> roles;
	
	public static KtuluRole getRole(String name) {
		return roles.get(name);
	}
	
	public static KtuluFraction getFraction(String name) {
		return fractions.get(name);
	}
	
	private static KtuluFraction loadFraction(Path path) {
		String fractionName = path.getFileName().toString();
		Path fractionProperties = path.resolve(Paths.get("fraction.json"));
		Gson gson = new Gson();
		
		//TODO
		return null;
	}
	
	private static KtuluRole loadRole(Path path) {
		//TODO
		return null;
	}
}
