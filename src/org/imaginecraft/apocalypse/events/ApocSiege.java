package org.imaginecraft.apocalypse.events;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import org.imaginecraft.apocalypse.Apocalypse;
import org.imaginecraft.apocalypse.config.ConfigOption;
import org.imaginecraft.apocalypse.teams.ApocTeam;

public class ApocSiege implements ConfigurationSerializable, Listener {
	
	private Apocalypse plugin = JavaPlugin.getPlugin(Apocalypse.class);
	private final ApocEvent aEvent = plugin.getApocConfig().getEvent();

	private Map<EntityType, Integer> mobs = new EnumMap<EntityType, Integer>(EntityType.class);
	private Set<Entity> spawned = new HashSet<Entity>();
	private Set<ApocBoss> bosses = new HashSet<ApocBoss>();
	private int husks = 0, strays = 0, witherSkellies = 0;
	
	private String name;
	private ApocTeam target = null;
	
	public ApocSiege(String name) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.name = name;
	}
	
	public boolean addBoss(ApocBoss boss) {
		return bosses.add(boss);
	}
	
	public Set<ApocBoss> getBosses() {
		return bosses;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean removeBoss(ApocBoss boss) {
		return bosses.remove(boss);
	}
	
	public void setMobs(String type, int amount) {
		if (EntityType.valueOf(type.toUpperCase()) != null) {
			mobs.put(EntityType.valueOf(type.toUpperCase()), amount);
		}
		else if (type.equalsIgnoreCase("husk")) {
			husks = amount;
		}
		else if (type.equalsIgnoreCase("stray")) {
			strays = amount;
		}
		else if (type.equalsIgnoreCase("wither_skeleton")) {
			witherSkellies = amount;
		}
	}
	
	@EventHandler
	private void onKill(EntityDeathEvent event) {
		if (event.getEntity().getWorld() == aEvent.getWorld()
				&& event.getEntity().getKiller() != null) {
			Player player = event.getEntity().getKiller();
			int points = 0;
			if (event.getEntity() instanceof Player) {
				points = ConfigOption.PLAYERS_POINTS_PER_KILL_PLAYER;
			}
			else if (spawned.contains(event.getEntity())
					|| !ConfigOption.EVENT_CREDIT_SPAWNED_MOBS_ONLY) {
				switch(event.getEntity().getType()) {
					case BLAZE: points = ConfigOption.PLAYERS_POINTS_PER_KILL_BLAZE; break;
					case CAVE_SPIDER: points = ConfigOption.PLAYERS_POINTS_PER_KILL_CAVE_SPIDER; break;
					case CREEPER: points = ConfigOption.PLAYERS_POINTS_PER_KILL_CREEPER; break;
					case ENDERMAN: points = ConfigOption.PLAYERS_POINTS_PER_KILL_ENDERMAN; break;
					case ENDERMITE: points = ConfigOption.PLAYERS_POINTS_PER_KILL_ENDERMITE; break;
					case MAGMA_CUBE: points = ConfigOption.PLAYERS_POINTS_PER_KILL_MAGMA_CUBE; break;
					case PIG_ZOMBIE: points = ConfigOption.PLAYERS_POINTS_PER_KILL_PIG_ZOMBIE; break;
					case POLAR_BEAR: points = ConfigOption.PLAYERS_POINTS_PER_KILL_POLAR_BEAR; break;
					case SILVERFISH: points = ConfigOption.PLAYERS_POINTS_PER_KILL_SILVERFISH; break;
					case SKELETON:
						switch(((Skeleton)event.getEntity()).getSkeletonType()) {
							case STRAY: points = ConfigOption.PLAYERS_POINTS_PER_KILL_STRAY; break;
							case WITHER: points = ConfigOption.PLAYERS_POINTS_PER_KILL_WITHER_SKELETON; break;
							default: points = ConfigOption.PLAYERS_POINTS_PER_KILL_SKELETON; break;
						}
						break;
					case SLIME: points = ConfigOption.PLAYERS_POINTS_PER_KILL_SLIME; break;
					case SNOWMAN: points = ConfigOption.PLAYERS_POINTS_PER_KILL_SNOWMAN; break;
					case SPIDER: points = ConfigOption.PLAYERS_POINTS_PER_KILL_SPIDER; break;
					case WITCH: points = ConfigOption.PLAYERS_POINTS_PER_KILL_WITCH; break;
					case WOLF: points = ConfigOption.PLAYERS_POINTS_PER_KILL_WOLF; break;
					case ZOMBIE:
						switch(((Zombie)event.getEntity()).getVillagerProfession()) {
							case HUSK: points = ConfigOption.PLAYERS_POINTS_PER_KILL_HUSK; break;
							default: points = ConfigOption.PLAYERS_POINTS_PER_KILL_ZOMBIE; break;
						}
						break;
					default: break;
				}
			}
			if (ApocTeam.getPlayerTeam(player) != null) {
				ApocTeam.getPlayerTeam(player).addScore(player.getUniqueId(), points);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ApocSiege deserialize(Map<String, Object> args) {
		String name = (String) args.get("name");
		ApocSiege siege = new ApocSiege(name);
		if (args.containsKey("bosses")) {
			List<String> bossList = (List<String>) args.get("bosses");
			for (String bossName : bossList) {
				ApocBoss boss = ApocBoss.getBoss(bossName);
				if (boss != null) siege.addBoss(boss);
			}
		}
		if (args.containsKey("mobs")) {
			Map<String, Integer> mobMap = (LinkedHashMap<String, Integer>) args.get("mobs");
			for (String mob : mobMap.keySet()) {
				siege.setMobs(mob, mobMap.get(mob));
			}
		}
		return siege;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("name", name);
		if (!bosses.isEmpty()) {
			List<String> bossList = new ArrayList<String>();
			for (ApocBoss boss : bosses) {
				bossList.add(boss.getName());
			}
			result.put("bosses", bossList);
		}
		if (!mobs.isEmpty() || husks > 0 || strays > 0 || witherSkellies > 0) {
			Map<String, Integer> mobMap = new LinkedHashMap<String, Integer>();
			for (EntityType type : mobs.keySet()) {
				mobMap.put(type.toString(), mobs.get(type));
			}
			if (husks > 0) mobMap.put("HUSK", husks);
			if (strays > 0) mobMap.put("STRAY", strays);
			if (witherSkellies > 0) mobMap.put("WITHER_SKELETON", witherSkellies);
			result.put("mobs", mobMap);
		}
		return result;
	}
	
}
