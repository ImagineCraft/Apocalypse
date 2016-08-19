package org.imaginecraft.apocalypse.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import org.imaginecraft.apocalypse.Apocalypse;
import org.imaginecraft.apocalypse.config.ApocConfig;
import org.imaginecraft.apocalypse.config.ConfigOption;
import org.imaginecraft.apocalypse.events.ApocBoss;
import org.imaginecraft.apocalypse.events.ApocEvent;
import org.imaginecraft.apocalypse.teams.ApocTeam;

import com.google.common.collect.Lists;

public class ApocComExec implements CommandExecutor {
	
	private final Apocalypse plugin = JavaPlugin.getPlugin(Apocalypse.class);
	private final ApocConfig config = plugin.getApocConfig();
	private ApocEvent event = config.getEvent();

	@Override
	public boolean onCommand(CommandSender sender, Command com, String name, String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("join")) {
				if (sender instanceof Player) {
					if (sender.hasPermission("apocalypse.join")) {
						Player player = (Player) sender;
						if (event.getAllPlayers().contains(player)) {
							if (args.length > 1) {
								if (ConfigOption.PLAYERS_CAN_SWITCH_TEAMS) {
									ApocTeam newTeam = ApocTeam.getTeam(args[1]);
									if (newTeam != null) {
										if (newTeam.getSize() < ConfigOption.TEAMS_MAXIMUM_MEMBERS) {
											newTeam.addPlayer(player.getUniqueId());
											sender.sendMessage(ChatColor.GREEN + "You successfully joined "+ newTeam.getName() + "!");
										}
										else {
											sender.sendMessage(ChatColor.RED + newTeam.getName() + " is already full.");
										}
									}
									else {
										sender.sendMessage(ChatColor.RED + "Team '" + args[1] + "' doesn't exist.");
									}
								}
								else {
									sender.sendMessage(ChatColor.RED + "You can't switch teams.");
								}
							}
							else {
								sender.sendMessage(ChatColor.RED + "You have already joined this event.");
							}
						}
						else {
							if (args.length > 1) {
								if (ConfigOption.PLAYERS_CAN_PICK_TEAM) {
									ApocTeam team = ApocTeam.getTeam(args[1]);
									if (team != null) {
										if (team.getSize() < ConfigOption.TEAMS_MAXIMUM_MEMBERS) {
											team.addPlayer(player.getUniqueId());
											sender.sendMessage(ChatColor.GREEN + "You successfully joined " + team.getName() + "!");
										}
										else {
											sender.sendMessage(ChatColor.RED + team.getName() + " is already full.");
										}
									}
									else {
										sender.sendMessage(ChatColor.RED + "Team '" + args[1] + "' doesn't exist.");
									}
								}
								else {
									sender.sendMessage(ChatColor.RED + "You can't pick your team.");
								}
							}
							else {
								ApocTeam team = event.getAvailableTeam();
								if (team != null) {
									team.addPlayer(player.getUniqueId());
									sender.sendMessage(ChatColor.GREEN + "You successfully joined " + team.getName() + "!");
								}
								else {
									sender.sendMessage(ChatColor.RED + "No available teams to join.");
								}
							}
						}
					}
					else {
						sender.sendMessage(ChatColor.RED + "You do not have permission to join an Apocalypse event.");
					}
				}
				else {
					sender.sendMessage(ChatColor.RED + "This command is only accessible to players.");
				}
			}
			else if (args[0].equalsIgnoreCase("leave")) {
				if (sender.hasPermission("apocalypse.leave")) {
					// TODO
				}
			}
			else if (args[0].equalsIgnoreCase("setoption")) {
				if (sender.hasPermission("apocalypse.setoption")) {
					if (args.length > 1) {
						if (config.getOptions().keySet().contains(args[1])) {
							if (args.length > 2) {
								Object value = config.getValue(args[1]);
								Object newValue = null;
								if (value instanceof Boolean) {
									newValue = Boolean.parseBoolean(args[2]);
								}
								else if (value instanceof Double) {
									try {
										newValue = Double.parseDouble(args[2]);
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "This option must be set to a number.");
										return true;
									}
								}
								else if (value instanceof Integer) {
									try {
										newValue = Integer.parseInt(args[2]);
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "This option must be set to a whole number.");
										return true;
									}
								}
								else if (value instanceof Long) {
									try {
										newValue = Long.parseLong(args[2]);
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "This option must be set to a whole number.");
										return true;
									}
								}
								if (newValue != null) {
									config.setValue(args[1], newValue);
									sender.sendMessage(ChatColor.GREEN + "Successfully set " + ChatColor.RESET + args[1] + ChatColor.GREEN + " to value " + ChatColor.WHITE + newValue.toString());
								}
								else {
									sender.sendMessage(ChatColor.RED + "Failed to set option to the specified value.");
								}
							}
							else {
								sender.sendMessage(ChatColor.UNDERLINE + args[1]);
								sender.sendMessage(ChatColor.GRAY + config.getOptions().get(args[1]));
								sender.sendMessage(ChatColor.WHITE + "Current value: " + ChatColor.GRAY + config.getValue(args[1]));
							}
						}
						else {
							sender.sendMessage(ChatColor.RED + "Unknown option: " + ChatColor.RESET + args[1]);
						}
					}
					else {
						sender.sendMessage(ChatColor.UNDERLINE + "Current options:");
						List<String> options = Lists.newArrayList(config.getOptions().keySet());
						Collections.sort(options);
						for (int i = 0; i < options.size(); i ++) {
							Object value = config.getValue(options.get(i));
							sender.sendMessage(options.get(i) + " = " + value.toString());
						}
					}
				}
				else {
					sender.sendMessage(ChatColor.RED + "You don't have permission to view or edit configuration options.");
				}
			}
			else if (args[0].equalsIgnoreCase("settown")) {
				if (sender.hasPermission("apocalypse.settown")) {
					// TODO
				}
				else {
					sender.sendMessage(ChatColor.RED + "You don't have permission to set towns.");
				}
			}
			else if (args[0].equalsIgnoreCase("setworld")) {
				if (sender.hasPermission("apocalypse.setworld")) {
					if (args.length > 1) {
						World world = plugin.getServer().getWorld(args[1]);
						if (world != null) {
							event.setWorld(world);
							sender.sendMessage(ChatColor.GREEN + "Set event world as " + ChatColor.WHITE + world.getName() + ChatColor.GREEN + ".");
						}
						else {
							sender.sendMessage(ChatColor.RED + "World '" + ChatColor.WHITE + args[1] + ChatColor.RED + "' is not loaded or doesn't exist.");
						}
					}
					else if (sender instanceof Player) {
						World world = ((Player) sender).getWorld();
						event.setWorld(world);
						sender.sendMessage(ChatColor.GREEN + "Set event world as " + ChatColor.WHITE + world.getName() + ChatColor.GREEN + ".");
					}
					else {
						sender.sendMessage(ChatColor.RED + "To use this command from the console you must supply the world name.");
					}
				}
				else {
					sender.sendMessage(ChatColor.RED + "You don't have permission to set the event world.");
				}
			}
			else if (args[0].equalsIgnoreCase("start")) {
				if (sender.hasPermission("apocalypse.start")) {
					// TODO
				}
			}
			else if (args[0].equalsIgnoreCase("testboss")) {
				if (sender instanceof Player) {
					if (sender.hasPermission("apocalypse.testboss")) {
						if (args.length > 1) {
							String bossName = "";
							for (int i = 1; i < args.length; i ++) {
								if (bossName == "") bossName = args[i];
								else bossName = bossName + " " + args[i];
							}
							Player player = (Player) sender;
							ApocBoss boss = ApocBoss.getBoss(bossName);
							if (boss != null) {
								ApocTeam team = ApocTeam.getTeam("Test");
								if (team == null) team = new ApocTeam(ChatColor.GRAY, "Test");
								team.addPlayer(player.getUniqueId());
								Location loc = new Location(player.getWorld(), -1860.0D, 67.0D, -450.0D);
								boss.spawn(team, player.getLocation(), loc);
								loc.getBlock().setType(Material.LAPIS_BLOCK);
							}
							else {
								sender.sendMessage(ChatColor.RED + "Unknown boss: " + bossName + ".");
							}
						}
						else {
							// TODO
						}
					}
					else {
						sender.sendMessage(ChatColor.RED + "You don't have permission to spawn bosses.");
					}
				}
				else {
					sender.sendMessage(ChatColor.RED + "This command is only accessible to players.");
				}
			}
		}
		else {
			// TODO
		}
		return true;
	}

}
