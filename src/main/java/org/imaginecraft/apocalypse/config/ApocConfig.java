package org.imaginecraft.apocalypse.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import org.imaginecraft.apocalypse.Apocalypse;
import org.imaginecraft.apocalypse.events.ApocBoss;
import org.imaginecraft.apocalypse.events.ApocEvent;
import org.imaginecraft.apocalypse.events.ApocSiege;
import org.imaginecraft.apocalypse.teams.ApocTeam;
import org.imaginecraft.apocalypse.tools.ApocTools;

/**
 * Class used to manage configuration options, as well as manage teams.
 * @author sirrus86
 */
public class ApocConfig {

	private final Apocalypse plugin = JavaPlugin.getPlugin(Apocalypse.class);
	private File configFile = new File(plugin.getDataFolder(), "config.yml"),
			eventFile = new File(plugin.getDataFolder(), "event.yml"),
			siegeFile = new File(plugin.getDataFolder(), "sieges.yml");
	protected static YamlConfiguration config;
	private YamlConfiguration eventConfig, siegeConfig;
	
	private Map<String, String> options = new HashMap<String, String>();
	private Map<String, Field> fields = new HashMap<String, Field>();
	
	private Set<ApocBoss> bosses = new HashSet<ApocBoss>();
	private Set<ApocSiege> sieges = new HashSet<ApocSiege>();
	
	private ApocEvent event;
	private final ApocTools tools = plugin.getApocTools();
	
	public ApocConfig() {
		// Make bosses, sieges, and teams serializable
		ConfigurationSerialization.registerClass(ApocBoss.class, "ApocBoss");
		ConfigurationSerialization.registerClass(ApocSiege.class, "ApocSiege");
		ConfigurationSerialization.registerClass(ApocTeam.class, "ApocTeam");
		event = new ApocEvent();
	}
	
	/**
	 * Loads configuration options, event information, and siege information from file into memory.
	 */
	public void load() {
		// Make sure files exist, if not create them
		for (File file : Arrays.asList(configFile, eventFile, siegeFile)) {
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// Unable to create new yml file
					e.printStackTrace();
				}
			}
		}
		// Load main configuration file
		config = YamlConfiguration.loadConfiguration(configFile);
		// Add all config options to maps so they're easier to find
		for (Field field : ConfigOption.class.getFields()) {
			if (field.isAnnotationPresent(ConfigDesc.class)) {
				fields.put(field.getAnnotation(ConfigDesc.class).path(), field);
				options.put(field.getAnnotation(ConfigDesc.class).path(), field.getAnnotation(ConfigDesc.class).desc());
				// If config doesn't have an entry for the referenced field, create it
				if (!config.contains(field.getAnnotation(ConfigDesc.class).path())) {
					try {
						config.set(field.getAnnotation(ConfigDesc.class).path(), field.get(null));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		// Load event configuration file
		eventConfig = YamlConfiguration.loadConfiguration(eventFile);
		if (eventConfig.contains("world")) {
			event.setWorld(plugin.getServer().getWorld(UUID.fromString(config.getString("world"))));
		}
		if (eventConfig.contains("teams")) {
			for (String string : eventConfig.getConfigurationSection("teams").getKeys(false)) {
				ApocTeam team = (ApocTeam) eventConfig.get("teams." + string);
				event.addTeam(team);
			}
		}
		// TODO
		siegeConfig = YamlConfiguration.loadConfiguration(siegeFile);
		if (siegeConfig.contains("bosses")) {
			for (String name : siegeConfig.getConfigurationSection("bosses").getKeys(false)) {
				ApocBoss boss = (ApocBoss) siegeConfig.get("bosses." + name);
				bosses.add(boss);
			}
		}
		if (siegeConfig.contains("sieges")) {
			for (String name : siegeConfig.getConfigurationSection("sieges").getKeys(false)) {
				ApocSiege siege = (ApocSiege) siegeConfig.get("sieges." + name);
				sieges.add(siege);
			}
		}
		if (ConfigOption.SIEGES_LOAD_PRELOADED_BOSSES) {
			for (ApocBoss boss : tools.getClasses(ApocBoss.class)) {
				if (!bosses.contains(boss)) bosses.add(boss);
			}
		}
		if (ConfigOption.SIEGES_LOAD_PRELOADED_SIEGES) {
			for (ApocSiege siege : tools.getClasses(ApocSiege.class)) {
				if (!sieges.contains(siege)) sieges.add(siege);
			}
		}
		// TODO
	}
	
	/**
	 * Saves configuration options, event information, and siege information from memory to file.
	 */
	public void save() {
		eventConfig = new YamlConfiguration();
		if (event.getWorld() != null) eventConfig.set("world", event.getWorld().getUID().toString());
		for (ApocTeam team : event.getTeams()) {
			eventConfig.set("teams." + team.getName(), team);
		}
		siegeConfig = new YamlConfiguration();
		for (ApocBoss boss : bosses) {
			String name = boss.getName().replaceAll(" ", "_");
			siegeConfig.set("bosses." + name, boss);
		}
		for (ApocSiege siege : sieges) {
			String name = siege.getName().replaceAll(" ", "_");
			siegeConfig.set("sieges." + name, siege);
		}
		try {
			config.save(configFile);
			eventConfig.save(eventFile);
			siegeConfig.save(siegeFile);
		} catch (IOException e) {
			// Failed to save file
			e.printStackTrace();
		}
	}
	
	public Set<ApocBoss> getBosses() {
		return bosses;
	}
	
	/**
	 * Returns the queued or currently active event.
	 */
	public ApocEvent getEvent() {
		return event;
	}
	
	/**
	 * Returns a Map of all loaded options.
	 */
	public Map<String, String> getOptions() {
		return options;
	}
	
	public Set<ApocSiege> getSieges() {
		return sieges;
	}
	
	/**
	 * Retrieves the config value of the specified option.
	 */
	public Object getValue(String string) {
		if (!config.contains(string)
				&& fields.containsKey(string)) {
			try {
				config.set(string, fields.get(string).get(null));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return config.get(string);
	}
	
	/**
	 * Sets the specified option to the specified value.
	 */
	public void setValue(String string, Object value) {
		config.set(string, value);
	}
	
}
