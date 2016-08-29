package org.imaginecraft.apocalypse.events;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import org.imaginecraft.apocalypse.Apocalypse;
import org.imaginecraft.apocalypse.teams.ApocTeam;
import org.imaginecraft.apocalypse.tools.ApocTools;

public class ApocBoss implements ConfigurationSerializable, Listener {
	
	private static Apocalypse plugin = JavaPlugin.getPlugin(Apocalypse.class);
	
	private Map<EquipmentSlot, ItemStack> equipment = new EnumMap<EquipmentSlot, ItemStack>(EquipmentSlot.class);
	private Map<DamageCause, Double> resistances = new EnumMap<DamageCause, Double>(DamageCause.class);

	private boolean isBaby = false;
	private BossBar bossBar;
	private LivingEntity entity;
	private double health = 0.0D;
	private String name;
	private int points = 0, xp = 0;
	private Horse.Variant horse = null;
	private ApocTeam team = null;
	private EntityType type;
	private SkeletonType sType = SkeletonType.NORMAL;
	private Villager.Profession zType = Villager.Profession.NORMAL;
	
	public ApocBoss(String name) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.name = name;
	}
	
	public static ApocBoss getBoss(String name) {
		for (ApocBoss boss : plugin.getApocConfig().getBosses()) {
			if (boss.getName().equalsIgnoreCase(name)) return boss;
		}
		return null;
	}
	
	public String getName() {
		return name;
	}
	
	public int getPoints() {
		return points;
	}
	
	public void setBaby(boolean baby) {
		this.isBaby = baby;
	}
	
	public void setDroppedExp(int amount) {
		xp = amount;
	}
	
	public void setEquipment(EquipmentSlot slot, ItemStack item) {
		equipment.put(slot, item);
	}
	
	public void setHealth(double health) {
		this.health = health;
	}
	
	public void setHorse(Horse.Variant horse) {
		this.horse = horse;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
	
	public void setResistance(DamageCause cause, double amount) {
		resistances.put(cause, amount);
	}
	
	public void setSkeletonType(SkeletonType type) {
		sType = type;
	}
	
	public void setType(EntityType type) {
		this.type = type;
	}
	
	public void setZombieType(Villager.Profession type) {
		zType = type;
	}
	
	public LivingEntity spawn(ApocTeam team, World world) {
		this.team = team;
		Location loc = ApocTools.findCenterLocation(team, world);
		entity = ApocTools.spawnMob(type.toString(), ApocTools.findSpawnLocation(loc));
		if (horse != null) {
			Horse mount = (Horse) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.HORSE);
			mount.setAdult();
			mount.setTamed(true);
			mount.setVariant(horse);
			mount.setPassenger(entity);
		}
		bossBar = plugin.getServer().createBossBar(name, BarColor.PURPLE, BarStyle.SOLID);
		for (Player player : entity.getWorld().getPlayers()) {
			if (team.hasPlayer(player.getUniqueId())) {
				bossBar.addPlayer(player);
			}
		}
		bossBar.setVisible(true);
		entity.setCustomName(ChatColor.LIGHT_PURPLE + name);
		entity.setCustomNameVisible(true);
		if (entity instanceof PigZombie) {
			((PigZombie) entity).setAnger(Integer.MAX_VALUE);
		}
		if (entity instanceof Skeleton) {
			((Skeleton) entity).setSkeletonType(sType);
		}
		if (entity instanceof Wolf) {
			((Wolf) entity).setAngry(true);
		}
		if (entity instanceof Zombie) {
			((Zombie) entity).setBaby(isBaby);
			((Zombie) entity).setVillagerProfession(zType);
		}
		if (health > 0.0D) {
			entity.setMaxHealth(health);
			entity.setHealth(health);
		}
		for (EquipmentSlot slot : equipment.keySet()) {
			switch(slot) {
				case CHEST: entity.getEquipment().setChestplate(equipment.get(slot)); break;
				case FEET: entity.getEquipment().setBoots(equipment.get(slot)); break;
				case HAND: entity.getEquipment().setItemInMainHand(equipment.get(slot)); break;
				case HEAD: entity.getEquipment().setHelmet(equipment.get(slot)); break;
				case LEGS: entity.getEquipment().setLeggings(equipment.get(slot)); break;
				case OFF_HAND: entity.getEquipment().setItemInOffHand(equipment.get(slot)); break;
			}
		}
		return entity;
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageEvent event) {
		if (entity != null
				&& event.getEntity() == entity) {
			if (resistances.containsKey(event.getCause())) {
				if (resistances.get(event.getCause()) > 0.0D) {
					event.setDamage(event.getDamage() * resistances.get(event.getCause()));
				}
				else {
					event.setCancelled(true);
				}
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					bossBar.setProgress(entity.getHealth() / entity.getMaxHealth());
				}
			}.runTask(plugin);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		if (entity != null
				&& event.getEntity() == entity) {
			if (event.getEntity().getKiller() != null) {
				Player player = event.getEntity().getKiller();
				if (ApocTeam.getPlayerTeam(player) != null) {
					ApocTeam.getPlayerTeam(player).addScore(player.getUniqueId(), points);
				}
			}
			if (xp > 0
					&& event.getDroppedExp() > 0) {
				event.setDroppedExp(xp);
			}
			bossBar.removeAll();
			for (OfflinePlayer oPlayer : team.getPlayers()) {
				if (oPlayer.isOnline()) {
					Player player = (Player) oPlayer;
					player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
					player.sendTitle(ChatColor.GREEN + this.name + " has been slain!", null);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ApocBoss deserialize(Map<String, Object> args) {
		String name = (String) args.get("name");
		ApocBoss boss = new ApocBoss(name);
		boss.setHealth((double) args.get("health"));
		boss.setType(EntityType.valueOf((String) args.get("type")));
		boss.setPoints((int) args.get("points"));
		if (args.containsKey("xp")) boss.setDroppedExp((int) args.get("xp"));
		if (args.containsKey("baby")) boss.setBaby((boolean) args.get("baby"));
		if (args.containsKey("horse")) boss.setHorse(Horse.Variant.valueOf((String) args.get("horse")));
		if (args.containsKey("skeleton-type")) boss.setSkeletonType(SkeletonType.valueOf((String) args.get("skeleton-type")));
		if (args.containsKey("zombie-type")) boss.setZombieType(Villager.Profession.valueOf((String) args.get("zombie-type")));
		if (args.containsKey("equipment")) {
			Map<String, ItemStack> equipMap = (LinkedHashMap<String, ItemStack>) args.get("equipment");
			for (String slot : equipMap.keySet()) {
				boss.setEquipment(EquipmentSlot.valueOf(slot), equipMap.get(slot));
			}
		}
		if (args.containsKey("resistances")) {
			Map<String, Double> resistMap = (LinkedHashMap<String, Double>) args.get("resistances");
			for (String resist : resistMap.keySet()) {
				boss.setResistance(DamageCause.valueOf(resist), resistMap.get(resist));
			}
		}
		// TODO
		return boss;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("name", name);
		result.put("health", health);
		result.put("type", type.toString());
		result.put("points", points);
		if (xp > 0) result.put("xp", xp);
		if (isBaby) result.put("baby", isBaby);
		if (horse != null) result.put("horse", horse.toString());
		if (type == EntityType.SKELETON) result.put("skeleton-type", sType.toString());
		if (type == EntityType.ZOMBIE) result.put("zombie-type", zType.toString());
		if (!equipment.isEmpty()) {
			Map<String, ItemStack> equipMap = new LinkedHashMap<String, ItemStack>();
			for (EquipmentSlot slot : equipment.keySet()) {
				if (equipment.get(slot) != null) {
					equipMap.put(slot.toString(), equipment.get(slot));
				}
			}
			result.put("equipment", equipMap);
		}
		if (!resistances.isEmpty()) {
			Map<String, Double> resistMap = new LinkedHashMap<String, Double>();
			for (DamageCause cause : resistances.keySet()) {
				if (resistances.get(cause) != 1.0D) {
					resistMap.put(cause.toString(), resistances.get(cause));
				}
			}
		}
		// TODO
		return result;
	}
	
}
