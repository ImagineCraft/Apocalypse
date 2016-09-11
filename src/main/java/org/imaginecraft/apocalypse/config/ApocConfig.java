package org.imaginecraft.apocalypse.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
	private final Map<YamlConfiguration, File> configs = new LinkedHashMap<YamlConfiguration, File>();
	
	private YamlConfiguration bossConfig = new YamlConfiguration(),
			eventConfig = new YamlConfiguration(),
			pluginConfig = new YamlConfiguration(),
			siegeConfig = new YamlConfiguration(),
			teamConfig = new YamlConfiguration();
	
	private Map<String, String> options = new HashMap<String, String>();
	private Map<String, Field> fields = new HashMap<String, Field>();
	
	private ApocEvent event;
	private final ApocTools tools = plugin.getApocTools();
	
	public ApocConfig() {
		// Associate configs with their files
		configs.put(pluginConfig, new File(plugin.getDataFolder(), "config.yml"));
		configs.put(eventConfig, new File(plugin.getDataFolder(), "event.yml"));
		configs.put(teamConfig, new File(plugin.getDataFolder(), "teams.yml"));
		configs.put(bossConfig, new File(plugin.getDataFolder(), "bosses.yml"));
		configs.put(siegeConfig, new File(plugin.getDataFolder(), "sieges.yml"));
		// Make bosses, sieges, and teams serializable
		ConfigurationSerialization.registerClass(ApocBoss.class, "ApocBoss");
		ConfigurationSerialization.registerClass(ApocEvent.class, "ApocEvent");
		ConfigurationSerialization.registerClass(ApocSiege.class, "ApocSiege");
		ConfigurationSerialization.registerClass(ApocTeam.class, "ApocTeam");
	}
	
	/**
	 * Loads configuration options, event information, and siege information from file into memory.
	 */
	public void load() {
		for (YamlConfiguration config : configs.keySet()) {
			try {
				// Make sure files exist, if not create them
				if (!configs.get(config).exists()) configs.get(config).createNewFile();
				config.load(configs.get(config));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Add all config options to maps so they're easier to find
		for (Field field : ConfigOption.class.getFields()) {
			try {
				if (field.isAnnotationPresent(ConfigDesc.class)) {
					ConfigDesc desc = field.getAnnotation(ConfigDesc.class);
					fields.put(desc.path(), field);
					options.put(desc.path(), desc.desc());
					field.set(null, pluginConfig.get(desc.path(), field.get(null)));
					pluginConfig.set(desc.path(), field.get(null));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Load saved event parameters from config
		event = (ApocEvent) eventConfig.get("event", new ApocEvent());
		// Load saved teams from config
		if (teamConfig.contains("teams")) {
			for (String name : teamConfig.getConfigurationSection("teams").getKeys(false)) {
				ApocTeam team = (ApocTeam) teamConfig.get("teams." + name);
				team.setObjective(event.getObjective());
				team.setScoreboardTeam(event.getScoreboard().registerNewTeam(name));
				team.updateScores();
				event.addTeam(team);
			}
		}
		// Load saved bosses from config
		if (bossConfig.contains("bosses")) {
			for (String name : bossConfig.getConfigurationSection("bosses").getKeys(false)) {
				ApocBoss boss = (ApocBoss) bossConfig.get("bosses." + name);
				event.addBoss(boss);
			}
		}
		// Add pre-loaded bosses
		if (ConfigOption.SIEGES_LOAD_PRELOADED_BOSSES) {
			for (ApocBoss boss : tools.getClasses(ApocBoss.class)) {
				if (!event.getBosses().contains(boss)) event.addBoss(boss);
			}
		}
		// Load saved sieges from config
		if (siegeConfig.contains("sieges")) {
			for (String name : siegeConfig.getConfigurationSection("sieges").getKeys(false)) {
				ApocSiege siege = (ApocSiege) siegeConfig.get("sieges." + name);
				event.addSiege(siege);
			}
		}
		// Add pre-loaded sieges
		if (ConfigOption.SIEGES_LOAD_PRELOADED_SIEGES) {
			for (ApocSiege siege : tools.getClasses(ApocSiege.class)) {
				if (!event.getSieges().contains(siege)) event.addSiege(siege);
			}
		}
	}
	
	/**
	 * Saves configuration options, event information, and siege information from memory to file.
	 */
	public void save() {
		// Clear loaded configs
		for (YamlConfiguration config : configs.keySet()) {
			if (config != pluginConfig) {
				for (String name : config.getKeys(false)) {
					config.set(name, null);
				}
			}
		}
		// Save event parameters to config
		eventConfig.set("event", event);
		// Save teams to config
		for (ApocTeam team : event.getTeams()) {
			if (!team.getName().equalsIgnoreCase("Test")) teamConfig.set("teams." + team.getName(), team);
		}
		// Save bosses to config
		for (ApocBoss boss : event.getBosses()) {
			String name = boss.getName().replaceAll(" ", "_");
			bossConfig.set("bosses." + name, boss);
		}
		// Save sieges to config
		for (ApocSiege siege : event.getSieges()) {
			String name = siege.getName().replaceAll(" ", "_");
			siegeConfig.set("sieges." + name, siege);
		}
		try {
			// Save configs to file
			for (YamlConfiguration config : configs.keySet()) {
				config.save(configs.get(config));
			}
		} catch (IOException e) {
			// Failed to save file
			e.printStackTrace();
		}
	}
	
	/**
	 * TODO
	 * @return
	 */
	public YamlConfiguration getConfig() {
		return pluginConfig;
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
	
	/**
	 * Retrieves the config value of the specified option.
	 */
	public Object getValue(String string) {
		if (!pluginConfig.contains(string)
				&& fields.containsKey(string)) {
			try {
				pluginConfig.set(string, fields.get(string).get(null));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return pluginConfig.get(string);
	}
	
	/**
	 * Sets the specified option to the specified value.
	 */
	public void setValue(String string, Object value) {
		pluginConfig.set(string, value);
		try {
			Field field = fields.get(string);
			field.set(null, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
