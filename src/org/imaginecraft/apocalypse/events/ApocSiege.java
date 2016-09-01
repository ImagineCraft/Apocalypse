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
import org.bukkit.Chunk;
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
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Villager;
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

public class ApocSiege implements ConfigurationSerializable, Listener {
	
	private static Apocalypse plugin = JavaPlugin.getPlugin(Apocalypse.class);

	private Map<Block, Biome> biomes = new HashMap<Block, Biome>();
	private Map<EntityType, Integer> mobs = new EnumMap<EntityType, Integer>(EntityType.class);
	private Map<LivingEntity, Integer> spawned = new HashMap<LivingEntity, Integer>();
	private Set<ApocBoss> bosses = new HashSet<ApocBoss>();
	private BossBar bar;
	private Biome biome;
	private Location dest;
	private int husks = 0, strays = 0, witherSkellies = 0;
	private boolean storm = false, thunder = false;
	private ApocTeam team;
	
	private String name;
	
	public ApocSiege(String name) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.name = name;
	}
	
	private BukkitRunnable setDestination = new BukkitRunnable() {
		@Override
		public void run() {
			if (!spawned.isEmpty()) {
				for (LivingEntity entity : spawned.keySet()) {
					if (entity != null
							&& !entity.isDead()
							&& entity instanceof Creature
							&& ((Creature)entity).getTarget() == null) {
						ApocTools.setDestination(entity, dest);
					}
				}
			}
		}
	};
	
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
	
	private int getPointValue(LivingEntity entity) {
		if (entity.getType() == EntityType.SKELETON) {
			if (((Skeleton)entity).getSkeletonType() == SkeletonType.STRAY) {
				return ConfigOption.PLAYERS_POINTS_PER_KILL_STRAY;
			}
			else if (((Skeleton)entity).getSkeletonType() == SkeletonType.WITHER) {
				return ConfigOption.PLAYERS_POINTS_PER_KILL_WITHER_SKELETON;
			}
			else return ConfigOption.PLAYERS_POINTS_PER_KILL_SKELETON;
		}
		else if (entity.getType() == EntityType.ZOMBIE) {
			if (((Zombie)entity).getVillagerProfession() == Villager.Profession.HUSK) {
				return ConfigOption.PLAYERS_POINTS_PER_KILL_HUSK;
			}
			else return ConfigOption.PLAYERS_POINTS_PER_KILL_ZOMBIE;
		}
		else {
			return ConfigOption.intValue("PLAYERS_POINTS_PER_KILL_" + entity.getType().toString());
		}
	}
	
	private double getProgress() {
		int i = spawned.size();
		for (LivingEntity entity : spawned.keySet()) {
			if (entity == null
					|| entity.isDead()) {
				i --;
			}
		}
		return (double) i / (double) spawned.size();
	}

	public static ApocSiege getSiege(String siegeName) {
		for (ApocSiege siege : plugin.getApocConfig().getSieges()) {
			if (siege.getName().equalsIgnoreCase(siegeName)) return siege;
		}
		return null;
	}
	
	public void purge() {
		for (LivingEntity entity : spawned.keySet()) {
			entity.damage(Double.MAX_VALUE);
		}
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
		List<Object> spawnList = new ArrayList<Object>();
		for (EntityType type : mobs.keySet()) {
			for (int i = 0; i < mobs.get(type); i ++) {
				spawnList.add(type);
			}
		}
		for (int i = 0; i < husks; i ++) {
			spawnList.add("HUSK");
		}
		for (int i = 0; i < strays; i ++) {
			spawnList.add("STRAY");
		}
		for (int i = 0; i < witherSkellies; i ++) {
			spawnList.add("WITHER_SKELETON");
		}
		spawnList.addAll(bosses);
		bar = plugin.getServer().createBossBar(name, BarColor.RED, getBarStyle());
		bar.setProgress(1.0D);
		for (OfflinePlayer player : team.getPlayers()) {
			if (player.isOnline()) {
				bar.addPlayer((Player) player);
			}
		}
		dest = ApocTools.findCenterLocation(team, world);
		spawnMobs(spawnList);
		updateBiome();
		dest.getWorld().setStorm(storm);
		dest.getWorld().setThundering(thunder);
		setDestination.runTaskTimer(plugin, ApocTools.getTicks(ConfigOption.SIEGES_SPAWN_INTERVAL), 20L);
	}
	
	private void spawnMobs(List<Object> spawnList) {
		for (int i = 0; i < spawnList.size(); i ++) {
			final Object spawn = spawnList.get(i);
			new BukkitRunnable() {
				@Override
				public void run() {
					LivingEntity entity = null;
					if (spawn instanceof ApocBoss) {
						entity = ((ApocBoss)spawn).spawn(team, dest.getWorld());
						spawned.put(entity, ((ApocBoss)spawn).getPoints());
					}
					else {
						entity = ApocTools.spawnMob(spawn.toString(), ApocTools.findSpawnLocation(dest));
						spawned.put(entity, getPointValue(entity));
					}
					if (entity != null) {
						entity.setGlowing(true);
						entity.setRemoveWhenFarAway(false);
						ApocTools.setAggressive(entity);
						ApocTools.setDestination(entity, dest);
						bar.setStyle(getBarStyle());
					}
				}
			}.runTaskLater(plugin, ApocTools.getTicks(ConfigOption.SIEGES_SPAWN_INTERVAL) * i);
		}
	}
	
	private void updateBiome() {
		if (biome != null) {
			Set<Chunk> chunks = new HashSet<Chunk>();
			for (int x = dest.getBlockX() - (int)ConfigOption.SIEGES_SPAWN_DISTANCE; x < dest.getBlockX() + ConfigOption.SIEGES_SPAWN_DISTANCE; x ++) {
				for (int z = dest.getBlockZ() - (int)ConfigOption.SIEGES_SPAWN_DISTANCE; z < dest.getBlockZ() + ConfigOption.SIEGES_SPAWN_DISTANCE; z ++) {
					Block block = dest.getWorld().getBlockAt(x, dest.getBlockY(), z);
					if (dest.distanceSquared(block.getLocation()) <= ConfigOption.SIEGES_SPAWN_DISTANCE * ConfigOption.SIEGES_SPAWN_DISTANCE) {
						biomes.put(block, block.getBiome());
						block.setBiome(biome);
						chunks.add(block.getChunk());
					}
				}
			}
			for (Chunk chunk : chunks) {
				ApocTools.updateChunk(chunk);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onKill(EntityDeathEvent event) {
		if (spawned.containsKey(event.getEntity())) {
			if (event.getEntity().getKiller() != null) {
				Player killer = event.getEntity().getKiller();
				int points = spawned.get(event.getEntity());
				
				if (ApocTeam.getPlayerTeam(killer) != null) {
					ApocTeam.getPlayerTeam(killer).addScore(killer.getUniqueId(), points);
				}
			}
			if (getProgress() > 0.0D) {
				bar.setProgress(getProgress());
			}
			else {
				bar.removeAll();;
				for (OfflinePlayer oPlayer : team.getPlayers()) {
					if (oPlayer.isOnline()) {
						Player player = (Player) oPlayer;
						player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
						player.sendTitle(ChatColor.GREEN + "Siege has been repelled!", null);
					}
				}
				Set<Chunk> chunks = new HashSet<Chunk>();
				for (Block block : biomes.keySet()) {
					block.setBiome(biomes.get(block));
					chunks.add(block.getChunk());
				}
				for (Chunk chunk : chunks) {
					ApocTools.updateChunk(chunk);
				}
				setDestination.cancel();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ApocSiege deserialize(Map<String, Object> args) {
		String name = (String) args.get("name");
		ApocSiege siege = new ApocSiege(name);
		if (args.containsKey("biome")) siege.setBiome(Biome.valueOf((String) args.get("biome")));
		if (args.containsKey("storm")) siege.setStorm((boolean) args.get("storm"));
		if (args.containsKey("thunder")) siege.setThunder((boolean) args.get("thunder"));
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
		if (biome != null) result.put("biome", biome.toString());
		if (storm) result.put("storm", storm);
		if (thunder) result.put("thunder", thunder);
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
