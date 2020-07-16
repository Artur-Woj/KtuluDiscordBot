package kmaput.discordktulu.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import discord4j.common.util.Snowflake;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Role;
import discord4j.rest.util.PermissionSet;
import kmaput.discordktulu.game.Player;

public class PermissionUtils {
	public static final PermissionSet MANITOU_PERMISSIONS = PermissionSet.of(522304L);
	public static final PermissionSet ALIVE_PERMISSIONS = PermissionSet.of(100416L);
	public static final PermissionSet DEAD_PERMISSIONS = PermissionSet.of(65536L);
	public static final PermissionSet DISALLOWED_PERMISSIONS = PermissionSet.none();
	
	public static class Builder {
		Set<PermissionOverwrite> permissionOverwrites;
		
		public Builder() {
			permissionOverwrites = new HashSet<>();
		}
		
		public Builder and(Set<PermissionOverwrite> overwrites) {
			permissionOverwrites.addAll(overwrites);
			return this;
		}
		
		public Builder and(PermissionOverwrite overwrite) {
			permissionOverwrites.add(overwrite);
			return this;
		}
		
		public Set<PermissionOverwrite> build() {
			return permissionOverwrites;
		}
	}
	
	public static boolean areEqual(PermissionOverwrite p1, PermissionOverwrite p2) {
		if(p1 == null && p2 == null) return true;
		if(p1 == null || p2 == null) return false;
		return	p1.getType().equals(p2.getType()) &&
				p1.getTargetId().equals(p2.getTargetId()) &&
				p1.getAllowed().equals(p2.getAllowed()) &&
				p1.getDenied().equals(p2.getDenied());
	}
	
	public static int compare(PermissionOverwrite p1, PermissionOverwrite p2) {
		if(p1 == null && p2 == null) return 0;
		if(p1 == null) return 1;
		if(p2 == null) return -1;
		int comp = p1.getTargetId().compareTo(p2.getTargetId());
		if(comp == 0) {
			comp = p1.getType().compareTo(p2.getType());
			if(comp == 0) {
				comp = Long.compare(p1.getAllowed().getRawValue(), p2.getAllowed().getRawValue());
				if(comp == 0) {
					comp = Long.compare(p1.getDenied().getRawValue(), p2.getDenied().getRawValue());
				}
			}
		}
		return comp;
	}
	
	public static boolean areEqual(Set<? extends PermissionOverwrite> p1, Set<? extends PermissionOverwrite> p2) {
		if(p1 == null && p2 == null) return true;
		if(p1 == null || p2 == null) return false;
		if(p1.size() != p2.size()) return false;
		ArrayList<PermissionOverwrite> list1 = new ArrayList<>(p1), list2 = new ArrayList<>(p2);
		Collections.sort(list1, PermissionUtils::compare);
		Collections.sort(list2, PermissionUtils::compare);
		for(int i = 0; i < list1.size(); i++) {
			if(!areEqual(list1.get(i), list2.get(i))) return false;
		}
		return true;
	}
	
	public static PermissionOverwrite simpleForMember(Snowflake id, PermissionSet permissions) {
		return PermissionOverwrite.forMember(id, permissions, permissions.not());
	}
	
	public static PermissionOverwrite simpleForRole(Snowflake id, PermissionSet permissions) {
		return PermissionOverwrite.forRole(id, permissions, permissions.not());
	}
	
	public static <T> Set<PermissionOverwrite> forMatching(Collection<T> collection, Function<T, Snowflake> userIdMapper, Predicate<T> predicate, PermissionSet allowed, PermissionSet denied) {
		return collection.stream()
				.filter(predicate)
				.map(userIdMapper)
				.map(id -> PermissionOverwrite.forMember(id, allowed, denied))
				.collect(Collectors.toSet());
	}
	
	public static <T> Set<PermissionOverwrite> forMatchingPlayers(Collection<Player> collection, Predicate<Player> predicate, PermissionSet allowed, PermissionSet denied) {
		return collection.stream()
				.filter(predicate)
				.map(Player::getId)
				.map(id -> PermissionOverwrite.forMember(id, allowed, denied))
				.collect(Collectors.toSet());
	}
	
	public static Set<PermissionOverwrite> forMatchingPlayers(Collection<Player> collection, Predicate<Player> predicate) {
		return collection.stream()
				.filter(predicate)
				.map(p -> simpleForMember(p.getId(), p.isAlive() ? ALIVE_PERMISSIONS : DEAD_PERMISSIONS))
				.collect(Collectors.toSet());
	}
	
	public static Set<PermissionOverwrite> forPlayers(Role aliveRole, Role deadRole) {
		Set<PermissionOverwrite> ret = new HashSet<>();
		ret.add(simpleForRole(aliveRole.getId(), ALIVE_PERMISSIONS));
		ret.add(simpleForRole(deadRole.getId(), DEAD_PERMISSIONS));
		return ret;
	}
	
	public static PermissionOverwrite forEveryone(Role everyoneRole) {
		return simpleForRole(everyoneRole.getId(), PermissionSet.none());
	}
	
	public static PermissionOverwrite forManitou(Role manitouRole) {
		return simpleForRole(manitouRole.getId(), MANITOU_PERMISSIONS);
	}
}
