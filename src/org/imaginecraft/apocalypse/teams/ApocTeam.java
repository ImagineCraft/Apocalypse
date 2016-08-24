package org.imaginecraft.apocalypse.teams;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import org.imaginecraft.apocalypse.Apocalypse;
import org.imaginecraft.apocalypse.events.ApocEvent;

public class ApocTeam implements ConfigurationSerializable {

	private static final Apocalypse plugin = JavaPlugin.getPlugin(Apocalypse.class);
	
	private ApocEvent event = plugin.getApocConfig().getEvent();
	
	private Team team;
	private Location spawn, town;
	
	private final Map<UUID, Integer> scores = new HashMap<UUID, Integer>();
	private ChatColor color;
	private final String name;
	
	public ApocTeam(ChatColor color, String name) {
		this.name = name;
		team = event.getScoreboard().registerNewTeam(name);
		setColor(color);
	}
	
	public void addPlayer(UUID uuid) {
		addPlayer(getPlayer(uuid), 0);
	}
	
	private void addPlayer(OfflinePlayer player, int score) {
		scores.put(player.getUniqueId(), score);
		team.addEntry(player.getName());
		event.getObjective().getScore(player.getName()).setScore(score);
	}
	
	public void addPlayer(UUID uuid, int score) {
		addPlayer(getPlayer(uuid), score);
	}
	
	public int addScore(UUID uuid, int amount) {
		int score = scores.get(uuid);
		scores.put(uuid, score + amount);
		event.getObjective().getScore(getPlayer(uuid).getName()).setScore(score + amount);
		return score + amount;
	}
	
	public ChatColor getColor() {
		return color;
	}
	
	public String getName() {
		return name;
	}
	
	private OfflinePlayer getPlayer(UUID uuid) {
		return plugin.getServer().getOfflinePlayer(uuid);
	}
	
	public Set<OfflinePlayer> getPlayers() {
		Set<OfflinePlayer> players = new HashSet<OfflinePlayer>();
		for (UUID uuid : scores.keySet()) {
			players.add(plugin.getServer().getOfflinePlayer(uuid));
		}
		return players;
	}
	
	public int getScore(UUID uuid) {
		return scores.get(uuid);
	}
	
	public Team getScoreboardTeam() {
		return team;
	}
	
	public int getSize() {
		return scores.size();
	}
	
	public Location getSpawn() {
		return spawn;
	}
	
	public Location getTown() {
		return town;
	}
	
	public static ApocTeam getTeam(String name) {
		for (ApocTeam team : plugin.getApocConfig().getEvent().getTeams()) {
			if (team.getName().equalsIgnoreCase(name)) {
				return team;
			}
		}
		return null;
	}
	
	public static ApocTeam getPlayerTeam(OfflinePlayer player) {
		for (ApocTeam team : plugin.getApocConfig().getEvent().getTeams()) {
			if (team.hasPlayer(player.getUniqueId())) {
				return team;
			}
		}
		return null;
	}
	
	public boolean hasPlayer(UUID uuid) {
		return scores.containsKey(uuid);
	}
	
	public void remove() {
		team.unregister();
		scores.clear();
	}
	
	public void setColor(ChatColor color) {
		this.color = color;
		team.setPrefix(color.toString());
	}
	
	public void setSpawn(Location loc) {
		spawn = loc;
	}
	
	public void setTown(Location loc) {
		town = loc;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("name", name);
		result.put("color", color.toString());
		if (spawn != null) result.put("spawn", spawn);
		if (town != null) result.put("town", town);
		if (!scores.isEmpty()) {
			Map<String, Integer> scoreMap = new LinkedHashMap<String, Integer>();
			for (UUID uuid : scores.keySet()) {
				scoreMap.put(uuid.toString(), scores.get(uuid));
			}
			result.put("scores", scoreMap);
		}
		// TODO Auto-generated method stub
		return result;
	}
	
}
