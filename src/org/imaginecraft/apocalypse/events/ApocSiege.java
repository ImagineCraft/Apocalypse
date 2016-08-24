package org.imaginecraft.apocalypse.events;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import org.imaginecraft.apocalypse.Apocalypse;
import org.imaginecraft.apocalypse.config.ConfigOption;
import org.imaginecraft.apocalypse.teams.ApocTeam;
import org.imaginecraft.apocalypse.tools.ApocTools;

import com.google.common.collect.Lists;

public class ApocSiege implements ConfigurationSerializable, Listener {
	
	private static Apocalypse plugin = JavaPlugin.getPlugin(Apocalypse.class);

	private Map<Block, Biome> biomes = new HashMap<Block, Biome>();
	private Map<EntityType, Integer> mobs = new EnumMap<EntityType, Integer>(EntityType.class);
	private Set<Entity> spawned = new HashSet<Entity>();
	private Set<ApocBoss> bosses = new HashSet<ApocBoss>();
	private BossBar bar;
	private Biome biome;
	private int husks = 0, strays = 0, witherSkellies = 0,
			spawnCount = 0;
	private boolean storm = false, thunder = false;
	private ApocTeam team;
	
	private String name;
	
	public ApocSiege(String name) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.name = name;
	}
	
	private BarStyle getBarStyle() {
		if (spawned.size() >= 20) return BarStyle.SEGMENTED_20;
		else if (spawned.size() >= 12) return BarStyle.SEGMENTED_12;
		else if (spawned.size() >= 10) return BarStyle.SEGMENTED_10;
		else if (spawned.size() >= 6) return BarStyle.SEGMENTED_6;
		else return BarStyle.SOLID;
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
	
	private double getProgress() {
		System.out.println("Progress is " + ((double) spawned.size() / (double) spawnCount));
		return (double) spawned.size() / (double) spawnCount;
	}
	
	public boolean removeBoss(ApocBoss boss) {
		return bosses.remove(boss);
	}
	
	public void setBiome(Biome biome) {
		this.biome = biome;
	}
	
	public void setMobs(EntityType type, int amount) {
		setMobs(type.toString(), amount);
	}
	
	public void setMobs(String type, int amount) {
		if (type.equalsIgnoreCase("husk")) {
			husks = amount;
		}
		else if (type.equalsIgnoreCase("stray")) {
			strays = amount;
		}
		else if (type.equalsIgnoreCase("wither_skeleton")) {
			witherSkellies = amount;
		}
		else {
			EntityType eType = EntityType.valueOf(type.toUpperCase());
			mobs.put(eType, amount);
		}
	}
	
	public void setStorm(boolean storm) {
		this.storm = storm;
	}
	
	public void setThunder(boolean thunder) {
		this.thunder = thunder;
	}
	
	public void spawn(ApocTeam team) {
		spawn(team, plugin.getApocConfig().getEvent().getWorld());
	}
	
	public void spawn(ApocTeam team, World world) {
		this.team = team;
		List<String> mobList = new ArrayList<String>();
		for (EntityType type : mobs.keySet()) {
			for (int i = 0; i < mobs.get(type); i ++) {
				mobList.add(type.toString());
			}
		}
		for (int i = 0; i < husks; i ++) {
			mobList.add("HUSK");
		}
		for (int i = 0; i < strays; i ++) {
			mobList.add("STRAY");
		}
		for (int i = 0; i < witherSkellies; i ++) {
			mobList.add("WITHER_SKELETON");
		}
		bar = plugin.getServer().createBossBar(name, BarColor.RED, getBarStyle());
		bar.setProgress(1.0D);
		for (OfflinePlayer player : team.getPlayers()) {
			if (player.isOnline()) {
				bar.addPlayer((Player) player);
			}
		}
		Location loc = ApocTools.findCenterLocation(team, world);
		for (int x = loc.getBlockX() - (int)ConfigOption.SIEGES_SPAWN_DISTANCE; x < loc.getBlockX() + ConfigOption.SIEGES_SPAWN_DISTANCE; x ++) {
			for (int z = loc.getBlockZ() - (int)ConfigOption.SIEGES_SPAWN_DISTANCE; z < loc.getBlockZ() + ConfigOption.SIEGES_SPAWN_DISTANCE; z ++) {
				Block block = loc.getWorld().getBlockAt(x, 0, z);
				biomes.put(block, block.getBiome());
				block.setBiome(biome);
			}
		}
		loc.getWorld().setStorm(storm);
		loc.getWorld().setThundering(thunder);
		List<ApocBoss> bossList = Lists.newArrayList(bosses);
		new BukkitRunnable() {
			int i = 0, j = 0;
			@Override
			public void run() {
				if (i < mobList.size()) {
					LivingEntity entity = ApocTools.spawnMob(mobList.get(i), ApocTools.findSpawnLocation(loc));
					entity.setGlowing(true);
					entity.setRemoveWhenFarAway(false);
					spawned.add(entity);
					spawnCount ++;
					i ++;
				}
				else if (j < bossList.size()){
					bossList.get(j).spawn(team, world);
					j ++;
				}
				else {
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 0L, ApocTools.getTicks(ConfigOption.SIEGES_SPAWN_INTERVAL));
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onKill(EntityDeathEvent event) {
		if (spawned.contains(event.getEntity())) {
			if (event.getEntity().getKiller() != null) {
				Player killer = event.getEntity().getKiller();
				int points = 0;
				if (event.getEntity() instanceof Player) {
					points = ConfigOption.PLAYERS_POINTS_PER_KILL_PLAYER;
				}
				else {
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
				if (ApocTeam.getPlayerTeam(killer) != null) {
					ApocTeam.getPlayerTeam(killer).addScore(killer.getUniqueId(), points);
				}
			}
			spawned.remove(event.getEntity());
			if (getProgress() > 0.0D) {
				bar.setProgress(getProgress());
			}
			else {
				bar.removeAll();;
				for (OfflinePlayer oPlayer : team.getPlayers()) {
					if (oPlayer.isOnline()) {
						Player player = (Player) oPlayer;
						player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
						player.sendTitle(ChatColor.GREEN + this.name + " has been repelled!", null);
					}
				}
				for (Block block : biomes.keySet()) {
					block.setBiome(biomes.get(block));
				}
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

	public static ApocSiege getSiege(String siegeName) {
		for (ApocSiege siege : plugin.getApocConfig().getSieges()) {
			if (siege.getName().equalsIgnoreCase(siegeName)) return siege;
		}
		return null;
	}
	
}
