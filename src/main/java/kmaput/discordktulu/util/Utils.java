package kmaput.discordktulu.util;

import java.util.HashSet;
import java.util.Set;

public class Utils {
	@SafeVarargs
	public static <T> Set<T> union(Set<T>... sets) {
		HashSet<T> ret = new HashSet<T>();
		for(Set<T> set : sets) ret.addAll(set);
		return ret;
	}
	
	public static <T> boolean testProperty(T value, T expected) {
		if(expected == null) return true;
		return expected.equals(value);
	}
}
