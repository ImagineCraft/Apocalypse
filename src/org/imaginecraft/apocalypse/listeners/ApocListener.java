package org.imaginecraft.apocalypse.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.plugin.java.JavaPlugin;

import org.imaginecraft.apocalypse.Apocalypse;
import org.imaginecraft.apocalypse.config.ConfigOption;
import org.imaginecraft.apocalypse.events.ApocEvent;

public class ApocListener implements Listener {
	
	private final Apocalypse plugin = JavaPlugin.getPlugin(Apocalypse.class);
	private final ApocEvent aEvent = plugin.getApocConfig().getEvent();

	@EventHandler(ignoreCancelled = true)
	private void onPlace(BlockPlaceEvent event) {
		if (event.getBlock().getWorld() == aEvent.getWorld()
				&& event.getBlockAgainst().getType() == Material.CHEST
				&& event.getBlockPlaced().getType() == Material.WALL_SIGN) {
			event.setCancelled(!ConfigOption.PLAYERS_CAN_PLACE_SIGNS_ON_CHESTS);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void onPortal(PortalCreateEvent event) {
		if (event.getWorld() == aEvent.getWorld()) {
			event.setCancelled(!ConfigOption.PLAYERS_CAN_CREATE_PORTALS);
		}
	}
	
}
