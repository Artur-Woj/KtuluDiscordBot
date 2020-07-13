package kmaput.discordktulu;

import java.util.Locale;

public class CommandResult {
	public final String message;
	
	public CommandResult() {
		this.message = null;
	}
	
	public CommandResult(String message) {
		this.message = message;
	}
	
	public CommandResult(String languageKey, Locale language) {
		this.message = Translator.translate(languageKey, language);
	}
}
