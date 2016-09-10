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
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import org.imaginecraft.apocalypse.Apocalypse;

public class ApocTeam implements ConfigurationSerializable {

	private final Apocalypse plugin = JavaPlugin.getPlugin(Apocalypse.class);
	private Objective objective;
	
	private final Map<UUID, Integer> scores = new HashMap<UUID, Integer>();
	
	private boolean canJoin = true;
	private ChatColor color;
	private UUID leader = null;
	private final String name;
	private Team sbTeam;
	private Location spawn, town;
	
	public ApocTeam(ChatColor color, String name) {
		this.color = color;
		this.name = name;
	}
	
	public void addPlayer(UUID uuid) {
		addPlayer(getPlayer(uuid), 0);
	}
	
	private void addPlayer(OfflinePlayer player, int score) {
		scores.put(player.getUniqueId(), score);
		if (player.getName() != null
				&& sbTeam != null) {
			sbTeam.addEntry(player.getName());
			objective.getScore(player.getName()).setScore(score);
		}
	}
	
	public void addPlayer(UUID uuid, int score) {
		addPlayer(getPlayer(uuid), score);
	}
	
	public int addScore(UUID uuid, int amount) {
		int score = scores.get(uuid);
		scores.put(uuid, score + amount);
		objective.getScore(getPlayer(uuid).getName()).setScore(score + amount);
		return score + amount;
	}
	
	public boolean canJoin() {
		return canJoin;
	}
	
	public ChatColor getColor() {
		return color;
	}
	
	public OfflinePlayer getLeader() {
		return leader != null ? plugin.getServer().getOfflinePlayer(leader) : null;
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
		return sbTeam;
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
	
	public boolean hasPlayer(UUID uuid) {
		return scores.containsKey(uuid);
	}
	
	public void remove() {
		sbTeam.unregister();
		scores.clear();
	}
	
	public void setCanJoin(boolean canJoin) {
		this.canJoin = canJoin;
	}
	
	public void setColor(ChatColor color) {
		this.color = color;
	}
	
	public void setLeader(UUID uuid) {
		leader = uuid;
	}
	
	public void setObjective(Objective objective) {
		this.objective = objective;
	}
	
	public void setScoreboardTeam(Team team) {
		sbTeam = team;
		sbTeam.setPrefix(color.toString());
	}
	
	public void setSpawn(Location loc) {
		spawn = loc;
	}
	
	public void setTown(Location loc) {
		town = loc;
	}
	
	public void updateScores() {
		for (UUID uuid : scores.keySet()) {
			OfflinePlayer player = plugin.getServer().getOfflinePlayer(uuid);
			if (player.getName() != null
					&& sbTeam != null
					&& !sbTeam.hasEntry(player.getName())) {
				sbTeam.addEntry(player.getName());
				objective.getScore(player.getName()).setScore(scores.get(uuid));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ApocTeam deserialize(Map<String, Object> args) {
		String name = (String) args.get("name");
		ChatColor color = ChatColor.getByChar((String)args.get("color"));
		ApocTeam team = new ApocTeam(color, name);
		if (args.containsKey("leader")) team.setLeader(UUID.fromString((String) args.get("leader")));
		if (args.containsKey("spawn")) team.setSpawn((Location) args.get("spawn"));
		if (args.containsKey("town")) team.setTown((Location) args.get("town"));
		if (args.containsKey("scores")) {
			Map<String, Integer> scoreMap = (LinkedHashMap<String, Integer>) args.get("scores");
			for (String uuid : scoreMap.keySet()) {
				team.addPlayer(UUID.fromString(uuid), scoreMap.get(uuid));
			}
		}
		return team;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("name", name);
		result.put("color", color.getChar());
		if (leader != null) result.put("leader", leader.toString());
		if (spawn != null) result.put("spawn", spawn);
		if (town != null) result.put("town", town);
		if (!scores.isEmpty()) {
			Map<String, Integer> scoreMap = new LinkedHashMap<String, Integer>();
			for (UUID uuid : scores.keySet()) {
				scoreMap.put(uuid.toString(), scores.get(uuid));
			}
			result.put("scores", scoreMap);
		}
		return result;
	}
	
}
