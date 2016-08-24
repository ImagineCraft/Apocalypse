package org.imaginecraft.apocalypse;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import org.imaginecraft.apocalypse.commands.ApocComExec;
import org.imaginecraft.apocalypse.config.ApocConfig;
import org.imaginecraft.apocalypse.listeners.ApocListener;
import org.imaginecraft.apocalypse.tools.ApocTools;

/**
 * Apocalypse event plugin made for the ImagineCraft server.
 * @author sirrus86
 */
public class Apocalypse extends JavaPlugin {

	private ApocConfig config;
	
	@Override
	public void onEnable() {
		if (!getDataFolder().exists()) getDataFolder().mkdirs();
		config = new ApocConfig();
		new ApocTools();
		config.load();
		getServer().getPluginManager().registerEvents(new ApocListener(), this);
		getCommand("apocalypse").setExecutor(new ApocComExec());
	}
	
	@Override
	public void onDisable() {
		config.save();
	}
	
	/**
	 * Returns the active instance of the Apocalypse config class.
	 */
	public ApocConfig getApocConfig() {
		return config;
	}
	
	/**
	 * Returns the jar file used by the Apocalypse plugin.
	 */
	public File getJarFile() {
		return this.getFile();
	}
	
}
