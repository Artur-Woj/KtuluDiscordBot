package kmaput.discordktulu.game;

import java.nio.file.Path;

public class KtuluRole {
	private KtuluFraction fraction;
	private String name;
	private String description;
	private Path image;
	
	public KtuluFraction getFraction() {
		return fraction;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Path getImage() {
		return image;
	}
	
	
}
