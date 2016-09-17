package org.imaginecraft.apocalypse.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
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
import org.imaginecraft.apocalypse.events.ApocChatType;
import org.imaginecraft.apocalypse.events.ApocEvent;
import org.imaginecraft.apocalypse.events.ApocSiege;
import org.imaginecraft.apocalypse.teams.ApocTeam;
import org.imaginecraft.apocalypse.tools.ApocTools;

import com.google.common.collect.Lists;

/**
 * TODO
 */
public class ApocComExec implements CommandExecutor {
	
	private final Apocalypse plugin = JavaPlugin.getPlugin(Apocalypse.class);
	private final ApocConfig config = plugin.getApocConfig();
	private ApocEvent event = config.getEvent();
	private ApocTools tools = plugin.getApocTools();
	
	private Map<UUID, Long> confirmLeave = new HashMap<UUID, Long>();
	
	private final ApocTeam testTeam = new ApocTeam(ChatColor.GRAY, "Test");
	
	public ApocComExec() {
		testTeam.setCanJoin(false);
		event.addTeam(testTeam);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command com, String name, String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("addteam")) {
				if (sender.hasPermission("apocalypse.addteam")) {
					if (!event.hasStarted() || ConfigOption.TEAMS_CAN_ADD_LATE) {
						if (args.length > 1) {
							ApocTeam team = event.getTeam(args[1]);
							if (team == null) {
								UUID leader = null;
								if (sender instanceof Player) {
									if (event.getPlayerTeam((Player)sender) == null) {
										leader = ((Player) sender).getUniqueId();
									}
									else {
										sender.sendMessage(ChatColor.RED + "You must leave your current team before making a new one.");
										return true;
									}
								}
								team = event.createTeam(args[1]);
								if (leader != null) {
									team.addPlayer(leader);
									team.setLeader(leader);
								}
								sender.sendMessage(ChatColor.GREEN + "Successfully created team " + team.getColor() + team.getName() + ChatColor.GREEN + "!");
							}
							else {
								sender.sendMessage(ChatColor.RED + "Team " + team.getColor() + team.getName() + ChatColor.RED + " already exists.");
							}
						}
						else {
							sender.sendMessage(ChatColor.RED + "You must specify the name of the new team.");
						}
					}
					else {
						sender.sendMessage(ChatColor.RED + "You can't add teams after the event has begun.");
					}
				}
				else {
					sender.sendMessage(ChatColor.RED + "You don't have permission to add teams.");
				}
			}
			else if (args[0].equalsIgnoreCase("chat")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (event.getAllPlayers().contains(player)) {
						if (sender.hasPermission("apocalypse.chat")) {
							ApocChatType type = event.getChatType(player);
							if (args.length > 1) {
								try {
									type = ApocChatType.valueOf(args[1].toUpperCase());
								} catch (IllegalArgumentException e) {
									sender.sendMessage(ChatColor.RED + "Unknown chat type. Choose OFF, PUBLIC, or TEAM.");
									return true;
								}
							}
							else {
								switch(type) {
									case OFF: type = ApocChatType.PUBLIC; break;
									case PUBLIC: type = ApocChatType.TEAM; break;
									case TEAM: type = ApocChatType.OFF; break;
								}
							}
							event.setChatType(player, type);
							sender.sendMessage(ChatColor.GREEN + "Apocalypse chat set to '" + type.toString() + "'.");
						}
						else {
							sender.sendMessage(ChatColor.RED + "You don't have permission to use event chat.");
						}
					}
					else {
						sender.sendMessage(ChatColor.RED + "You must first join the event to be able to use event chat.");
					}
				}
				else {
					sender.sendMessage(ChatColor.RED + "This command is only accessible to players.");
				}
			}
			else if (args[0].equalsIgnoreCase("confirm")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (confirmLeave.containsKey(player.getUniqueId())
							&& System.currentTimeMillis() <= confirmLeave.get(player.getUniqueId())) {
						ApocTeam team = event.getPlayerTeam(player);
						if (team != null) {
							if (team.getLeader() != player
									|| team.getSize() == 1) {
								team.removePlayer(player);
								sender.sendMessage(ChatColor.GREEN + "You have left the event.");
								if (team.getSize() == 0) team.remove();
							}
							else {
								sender.sendMessage(ChatColor.RED + "You must assign a new leader to your team before you can leave.");
							}
						}
					}
				}
			}
			else if (args[0].equalsIgnoreCase("join")) {
				if (sender instanceof Player) {
					if (sender.hasPermission("apocalypse.join")) {
						if (!event.hasStarted() || ConfigOption.PLAYERS_CAN_JOIN_LATE) {
							Player player = (Player) sender;
							if (event.getAllPlayers().contains(player)) {
								// Player has already joined event
								if (args.length > 1) {
									// Player wishes to switch teams
									if (ConfigOption.PLAYERS_CAN_SWITCH_TEAMS) {
										ApocTeam newTeam = event.getTeam(args[1]);
										if (newTeam != null) {
											// Make sure team is joinable
											if (newTeam.canJoin()) {
												if (!ConfigOption.TEAMS_ENFORCE_MAXIMUM_MEMBERS
														|| newTeam.getSize() < ConfigOption.TEAMS_MAXIMUM_MEMBERS) {
													newTeam.addPlayer(player.getUniqueId());
													sender.sendMessage(ChatColor.GREEN + "You successfully joined " + newTeam.getColor() + newTeam.getName() + ChatColor.GREEN +  "!");
												}
												else {
													sender.sendMessage(newTeam.getColor() + newTeam.getName() + ChatColor.RED + " is already full.");
												}
											}
											else {
												sender.sendMessage(newTeam.getColor() + newTeam.getName() + ChatColor.RED + "' isn't joinable.");
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
								// Player hasn't joined the event yet
								if (args.length > 1) {
									// Player is trying to pick their team
									if (ConfigOption.PLAYERS_CAN_PICK_TEAM) {
										ApocTeam team = event.getTeam(args[1]);
										if (team != null) {
											if (team.getSize() < ConfigOption.TEAMS_MAXIMUM_MEMBERS) {
												if (team.canJoin()) {
													team.addPlayer(player.getUniqueId());
													sender.sendMessage(ChatColor.GREEN + "You successfully joined " + team.getColor() + team.getName() + ChatColor.GREEN +  "!");
												}
												else {
													sender.sendMessage(team.getColor() + team.getName() + ChatColor.RED + "' isn't joinable.");
												}
											}
											else {
												sender.sendMessage(team.getColor() + team.getName() + ChatColor.RED + " is already full.");
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
									// Player hasn't picked a team, one will be picked for them
									ApocTeam team = event.getAvailableTeam();
									if (team != null
											&& team.canJoin()) {
										team.addPlayer(player.getUniqueId());
										sender.sendMessage(ChatColor.GREEN + "You successfully joined " + team.getColor() + team.getName() + ChatColor.GREEN +  "!");
									}
									else {
										sender.sendMessage(ChatColor.RED + "No available teams to join.");
									}
								}
							}
						}
						else {
							sender.sendMessage(ChatColor.RED + "You can't join an event after it has started.");
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
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (sender.hasPermission("apocalypse.leave")) {
						if (event.getAllPlayers().contains(sender)) {
							confirmLeave.put(player.getUniqueId(), System.currentTimeMillis() + ConfigOption.PLUGIN_CONFIRM_TIMER);
							sender.sendMessage(ChatColor.YELLOW + "Are you sure you wish to leave the event?");
							if (ConfigOption.PLAYERS_CAN_JOIN_LATE) sender.sendMessage(ChatColor.YELLOW + "Once you leave you will be unable to rejoin.");
							sender.sendMessage(ChatColor.YELLOW + "To confirm leaving the event, type " + ChatColor.GREEN + "/apoc confirm" + ChatColor.YELLOW
									+ " in the next " + tools.getTime(ConfigOption.PLUGIN_CONFIRM_TIMER));
						}
						else {
							sender.sendMessage(ChatColor.RED + "You haven't joined the event.");
						}
					}
					else {
						sender.sendMessage(ChatColor.RED + "You do not have permission to leave an Apocalypse event.");
					}
				}
				else {
					sender.sendMessage(ChatColor.RED + "This command is only accessible to players.");
				}
			}
			else if (args[0].equalsIgnoreCase("removeteam")) {
				if (sender.hasPermission("apocalypse.removeteam")) {
					// Sender can remove any team
					if (args.length > 1) {
						ApocTeam team = event.getTeam(args[1]);
						if (team != null) {
							event.removeTeam(team);
							sender.sendMessage(ChatColor.GREEN + "Team " + team.getColor() + team.getName() + ChatColor.GREEN + "has been removed.");
						}
						else {
							sender.sendMessage(ChatColor.RED + "Team '" + args[1] + "' does not exist.");
						}
					}
					else {
						sender.sendMessage(ChatColor.RED + "You must specify the team to remove.");
					}
				}
				else if (sender instanceof Player
						&& ConfigOption.TEAMS_LEADER_CAN_REMOVE_TEAM) {
					// Player can only remove their own team
					Player player = (Player) sender;
					ApocTeam team = event.getPlayerTeam(player);
					if (team != null) {
						if (team.getLeader() == player) {
							event.removeTeam(team);
							sender.sendMessage(ChatColor.GREEN + "Team " + team.getColor() + team.getName() + ChatColor.GREEN + "has been removed.");
						}
						else {
							// Player isn't the team's leader
							sender.sendMessage(ChatColor.RED + "Only the team leader can remove their team.");
						}
					}
					else {
						// Player isn't on a team
						sender.sendMessage(ChatColor.RED + "You aren't on a team.");
					}
				}
				else {
					sender.sendMessage(ChatColor.RED + "You do not have permission to remove teams.");
				}
			}
			else if (args[0].equalsIgnoreCase("setoption")) {
				if (sender.hasPermission("apocalypse.setoption")) {
					if (args.length > 1) {
						// Sender either wants info on an option or wants to change one
						if (config.getOptions().keySet().contains(args[1])) {
							if (args.length > 2) {
								// Sender intends to change an option
								Object value = config.getValue(args[1]);
								Object newValue = null;
								if (value instanceof Boolean) {
									newValue = Boolean.parseBoolean(args[2]);
								}
								else {
									try {
										if (value instanceof Double) newValue = Double.parseDouble(args[2]);
										else if (value instanceof Integer) newValue = Integer.parseInt(args[2]);
										else if (value instanceof Long) newValue = Long.parseLong(args[2]);
										else if (value instanceof Short) newValue = Short.parseShort(args[2]);
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "This option must be set to a number.");
										return true;
									}
								}
								if (newValue != null) {
									config.setValue(args[1], newValue);
									sender.sendMessage(ChatColor.GREEN + "Successfully set " + ChatColor.RESET + args[1] + ChatColor.GREEN + " to value " + ChatColor.WHITE + newValue.toString());
								}
								// New value couldn't be parsed for whatever reason
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
						// Show sender list of all available options
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
					if (event.getWorld() != null) {
						if (!event.hasStarted()) {
							event.startWarning();
						}
						else {
							sender.sendMessage(ChatColor.RED + "The event is already active.");
						}
					}
					else {
						sender.sendMessage(ChatColor.RED + "You must set a world for the event.");
					}
				}
				else {
					sender.sendMessage(ChatColor.RED + "You don't have permission to start the event.");
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
							ApocBoss boss = event.getBoss(bossName);
							if (boss != null) {
								testTeam.setSpawn(player.getLocation());
								testTeam.addPlayer(player.getUniqueId());
								boss.spawn(testTeam, player.getWorld());
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
			else if (args[0].equalsIgnoreCase("testsiege")) {
				if (sender instanceof Player) {
					if (sender.hasPermission("apocalypse.testsiege")) {
						if (args.length > 1) {
							String siegeName = "";
							for (int i = 1; i < args.length; i ++) {
								if (siegeName == "") siegeName = args[i];
								else siegeName = siegeName + " " + args[i];
							}
							Player player = (Player) sender;
							ApocSiege siege = event.getSiege(siegeName);
							if (siege != null) {
								testTeam.setSpawn(player.getLocation());
								testTeam.addPlayer(player.getUniqueId());
								siege.spawn(testTeam, player.getWorld());
							}
							else {
								sender.sendMessage(ChatColor.RED + "Unknown siege: " + siegeName + ".");
							}
						}
						else {
							// TODO
						}
					}
					else {
						sender.sendMessage(ChatColor.RED + "You don't have permission to spawn sieges.");
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
