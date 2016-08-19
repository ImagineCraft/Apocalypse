package org.imaginecraft.apocalypse.events;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import org.imaginecraft.apocalypse.Apocalypse;
import org.imaginecraft.apocalypse.config.ConfigOption;
import org.imaginecraft.apocalypse.teams.ApocTeam;
import org.imaginecraft.apocalypse.tools.ApocTools;

/**
 * Instance of the queued or currently active event.
 */
public class ApocEvent {
	
	private Apocalypse plugin = JavaPlugin.getPlugin(Apocalypse.class);
	
	private Set<LivingEntity> spawned = new HashSet<LivingEntity>();
	private Set<ApocTeam> teams = new HashSet<ApocTeam>();
	
	private final EnumSet<ChatColor> colors = EnumSet.of(ChatColor.AQUA, ChatColor.BLUE, ChatColor.DARK_AQUA, ChatColor.DARK_BLUE,
			ChatColor.DARK_GREEN, ChatColor.DARK_PURPLE, ChatColor.DARK_RED, ChatColor.GOLD, ChatColor.GREEN, ChatColor.LIGHT_PURPLE,
			ChatColor.RED, ChatColor.YELLOW);

	private final Random random = new Random();
	
	private boolean active = false;
	private long duration = 0L, endTime = 0L, warningTime = 0L;
	private int warnings = 0;
	private final Objective objective;
	private final Scoreboard scoreboard;
	private World world;
	
	public ApocEvent() {
		scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
		objective = scoreboard.registerNewObjective("Points", "dummy");
	}
	
	private Runnable warning = new BukkitRunnable() {
		int i = warnings;
		@Override
		public void run() {
			long wTime = (i / warnings) * warningTime;
			if (i > 0) {
				for (OfflinePlayer player : getAllPlayers()) {
					if (player.isOnline()) {
						((Player) player).sendMessage(ChatColor.GOLD + "The Apocalypse will begin in " + ApocTools.getTime(wTime) + ".");
					}
				}
				i --;
			}
			else {
				for (OfflinePlayer player : getAllPlayers()) {
					if (player.isOnline()) {
						((Player) player).sendMessage(ChatColor.GOLD + "The Apocalypse has begun!");
					}
				}
				startEvent();
				this.cancel();
			}
		}
	};
	
//	/**
//	 * Adds points to the specified player.
//	 */
//	@SuppressWarnings("deprecation")
//	public void addPoints(OfflinePlayer player, int amt) {
//		int i = objective.getScore(player).getScore();
//		objective.getScore(player).setScore(i + amt);
//	}
	
	public void addTeam(ApocTeam team) {
		teams.add(team);
	}
	
	public ApocTeam createTeam(String name) {
		ChatColor color = null;
		if (name.startsWith("&")) {
			String prefix = name.substring(0, 2);
			name = name.substring(2);
			if (ChatColor.getByChar(prefix) != null) {
				color = ChatColor.getByChar(prefix);
				if (color.isColor()
						&& colors.contains(color)) {
					colors.remove(color);
				}
			}
		}
		ApocTeam team = new ApocTeam(color != null ? color : getAvailableTeamColor(), name);
		teams.add(team);
		return team;
	}
	
	public Set<OfflinePlayer> getAllPlayers() {
		Set<OfflinePlayer> players = new HashSet<OfflinePlayer>();
		for (ApocTeam team : teams) {
			players.addAll(team.getPlayers());
		}
		return players;
	}
	
	/**
	 * Returns the smallest team that has enough room. Returns null if all teams are full or no teams exist.
	 */
	public ApocTeam getAvailableTeam() {
		ApocTeam newTeam = null;
		int i = Integer.MAX_VALUE;
		for (ApocTeam team : teams) {
			if (team.getSize() < i
					&& (team.getSize() < ConfigOption.TEAMS_MAXIMUM_MEMBERS
							|| !ConfigOption.TEAMS_ENFORCE_MAXIMUM_MEMBERS)) {
				i = team.getSize();
				newTeam = team;
			}
		}
		return newTeam;
	}
	
	private ChatColor getAvailableTeamColor() {
		int index = random.nextInt(colors.size());
		Iterator<ChatColor> iter = colors.iterator();
		for (int i = 0; i < index; i ++) {
			iter.next();
		}
		ChatColor color = iter.next();
		colors.remove(color);
		return color;
	}
	
	public long getEndTime() {
		return endTime;
	}
	
	public Objective getObjective() {
		return objective;
	}
	
	public Scoreboard getScoreboard() {
		return scoreboard;
	}
	
	public Set<LivingEntity> getSpawned() {
		return spawned;
	}
	
	public Set<ApocTeam> getTeams() {
		return teams;
	}
	
	public World getWorld() {
		return world;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void removeTeam(ApocTeam team) {
		team.remove();
		teams.remove(team);
	}
	
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	public void setWorld(World world) {
		this.world = world;
	}
	
	private void startEvent() {
		duration = ConfigOption.EVENT_DURATION;
		endTime = System.currentTimeMillis() + duration;
	}
	
	public void startWarning() {
		warnings = ConfigOption.EVENT_WARNING_NOTIFICATIONS;
		warningTime = ConfigOption.EVENT_WARNING_TIME;
		plugin.getServer().getScheduler().runTaskTimer(plugin, warning, 0L, ApocTools.getTicks(ConfigOption.EVENT_WARNING_TIME));
	}
	
}
