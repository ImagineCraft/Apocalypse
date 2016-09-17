package org.imaginecraft.apocalypse.listeners;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.plugin.java.JavaPlugin;

import org.imaginecraft.apocalypse.Apocalypse;
import org.imaginecraft.apocalypse.config.ConfigOption;
import org.imaginecraft.apocalypse.events.ApocEvent;
import org.imaginecraft.apocalypse.teams.ApocTeam;

public class ApocListener implements Listener {
	
	private final Apocalypse plugin = JavaPlugin.getPlugin(Apocalypse.class);
	private ApocEvent aEvent = plugin.getApocConfig().getEvent();

	@EventHandler(ignoreCancelled = true)
	private void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		if (aEvent.getAllPlayers().contains(player)) {
			ChatColor chatColor = ChatColor.WHITE;
			String message = event.getMessage();
			Set<OfflinePlayer> recipients = new HashSet<OfflinePlayer>();
			ChatColor teamColor = aEvent.getPlayerTeam(player).getColor();
			switch (aEvent.getChatType(player)) {
				case OFF: return;
				case PUBLIC: recipients = aEvent.getAllPlayers(); break;
				case TEAM: chatColor = ChatColor.GRAY; recipients = aEvent.getPlayerTeam(player).getPlayers(); break;
			}
			event.setCancelled(true);
			for (OfflinePlayer recipient : recipients) {
				if (recipient.isOnline()) {
					((Player)recipient).sendMessage(ChatColor.WHITE + "[" + teamColor + player.getName() + ChatColor.WHITE + "] "
							+ chatColor + message);
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void onPlace(BlockPlaceEvent event) {
		if (event.getBlock().getWorld() == aEvent.getWorld()
				&& event.getBlockAgainst().getType() == Material.CHEST
				&& event.getBlockPlaced().getType() == Material.WALL_SIGN) {
			event.setCancelled(!ConfigOption.PLAYERS_CAN_PLACE_SIGNS_ON_CHESTS);
		}
	}
	
	@EventHandler
	private void onJoin(PlayerJoinEvent event) {
		if (aEvent.getAllPlayers().contains(event.getPlayer())) {
			Player player = event.getPlayer();
			ApocTeam team = aEvent.getPlayerTeam(player);
			if (team != null) {
				if (!team.getScoreboardTeam().hasEntry(player.getName())) {
					team.getScoreboardTeam().addEntry(player.getName());
					aEvent.getObjective().getScore(player.getName()).setScore(team.getScore(player.getUniqueId()));
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void onPortal(PortalCreateEvent event) {
		if (event.getWorld() == aEvent.getWorld()) {
			event.setCancelled(!ConfigOption.PLAYERS_CAN_CREATE_PORTALS);
		}
	}
	
}
