package org.imaginecraft.apocalypse.config;

import org.imaginecraft.apocalypse.config.ConfigDesc;

public class ConfigOption {

//	@ConfigDesc(path = "event.credit-spawned-mobs-only", desc = "Whether players should only score points for monsters spawned in sieges.")
//	public static boolean EVENT_CREDIT_SPAWNED_MOBS_ONLY = ApocConfig.config.getBoolean("event.credit-spawned-mobs-only", true);

	@ConfigDesc(path = "event.duration", desc = "How long in milliseconds the event should last.")
	public static long EVENT_DURATION = ApocConfig.config.getLong("event.duration", 1209600000L);
	
	@ConfigDesc(path = "event.warning-notifications", desc = "How many notifications players should get that an event is about to start.")
	public static int EVENT_WARNING_NOTIFICATIONS = ApocConfig.config.getInt("event.warning-notifications", 3);
	
	@ConfigDesc(path = "event.warning-time", desc = "How long in advance in milliseconds players should be notified of an upcoming event.")
	public static long EVENT_WARNING_TIME = ApocConfig.config.getLong("event.warning-time", 300000L);
	
	@ConfigDesc(path = "players.can-create-portals", desc = "Whether players can create portals in the event world.")
	public static boolean PLAYERS_CAN_CREATE_PORTALS = ApocConfig.config.getBoolean("players.can-create-portals", false);
	
	@ConfigDesc(path = "players.can-pick-team", desc = "Whether players can pick their team when joining.")
	public static boolean PLAYERS_CAN_PICK_TEAM = ApocConfig.config.getBoolean("players.can-pick-team", false);
	
	@ConfigDesc(path = "players.can-place-signs-on-chests", desc = "Whether players can place signs on chests in the event world.")
	public static boolean PLAYERS_CAN_PLACE_SIGNS_ON_CHESTS = ApocConfig.config.getBoolean("players.can-place-signs-on-chests", false);
	
	@ConfigDesc(path = "players.can-set-town", desc = "Whether players can set the town for their team.")
	public static boolean PLAYERS_CAN_SET_TOWN = ApocConfig.config.getBoolean("players.can-set-town", false);
	
	@ConfigDesc(path = "players.can-switch-teams", desc = "Whether players can switch teams before the event starts.")
	public static boolean PLAYERS_CAN_SWITCH_TEAMS = ApocConfig.config.getBoolean("players.can-switch-teams", false);

	@ConfigDesc(path = "players.points-per-kill.blaze", desc = "Amount of points player will earn per blaze kill.")
	public static int PLAYERS_POINTS_PER_KILL_BLAZE = ApocConfig.config.getInt("players.points-per-kill.blaze", 10);

	@ConfigDesc(path = "players.points-per-kill.cave-spider", desc = "Amount of points player will earn per cave spider kill.")
	public static int PLAYERS_POINTS_PER_KILL_CAVE_SPIDER = ApocConfig.config.getInt("players.points-per-kill.cave-spider", 3);

	@ConfigDesc(path = "players.points-per-kill.creeper", desc = "Amount of points player will earn per creeper kill.")
	public static int PLAYERS_POINTS_PER_KILL_CREEPER = ApocConfig.config.getInt("players.points-per-kill.creeper", 5);

	@ConfigDesc(path = "players.points-per-kill.enderman", desc = "Amount of points player will earn per enderman kill.")
	public static int PLAYERS_POINTS_PER_KILL_ENDERMAN = ApocConfig.config.getInt("players.points-per-kill.enderman", 10);

	@ConfigDesc(path = "players.points-per-kill.endermite", desc = "Amount of points player will earn per endermite kill.")
	public static int PLAYERS_POINTS_PER_KILL_ENDERMITE = ApocConfig.config.getInt("players.points-per-kill.endermite", 3);

	@ConfigDesc(path = "players.points-per-kill.husk", desc = "Amount of points player will earn per husk kill.")
	public static int PLAYERS_POINTS_PER_KILL_HUSK = ApocConfig.config.getInt("players.points-per-kill.husk", 7);

	@ConfigDesc(path = "players.points-per-kill.magma-cube", desc = "Amount of points player will earn per magma cube kill.")
	public static int PLAYERS_POINTS_PER_KILL_MAGMA_CUBE = ApocConfig.config.getInt("players.points-per-kill.magma-cube", 5);

	@ConfigDesc(path = "players.points-per-kill.pig-zombie", desc = "Amount of points player will earn per pig zombie kill.")
	public static int PLAYERS_POINTS_PER_KILL_PIG_ZOMBIE = ApocConfig.config.getInt("players.points-per-kill.pig-zombie", 7);

	@ConfigDesc(path = "players.points-per-kill.player", desc = "Amount of points player will earn per player kill.")
	public static int PLAYERS_POINTS_PER_KILL_PLAYER = ApocConfig.config.getInt("players.points-per-kill.player", 50);

	@ConfigDesc(path = "players.points-per-kill.polar-bear", desc = "Amount of points player will earn per polar bear kill.")
	public static int PLAYERS_POINTS_PER_KILL_POLAR_BEAR = ApocConfig.config.getInt("players.points-per-kill.polar-bear", 10);

	@ConfigDesc(path = "players.points-per-kill.silverfish", desc = "Amount of points player will earn per silverfish kill.")
	public static int PLAYERS_POINTS_PER_KILL_SILVERFISH = ApocConfig.config.getInt("players.points-per-kill.silverfish", 3);

	@ConfigDesc(path = "players.points-per-kill.skeleton", desc = "Amount of points player will earn per skeleton kill.")
	public static int PLAYERS_POINTS_PER_KILL_SKELETON = ApocConfig.config.getInt("players.points-per-kill.skeleton", 7);

	@ConfigDesc(path = "players.points-per-kill.slime", desc = "Amount of points player will earn per slime kill.")
	public static int PLAYERS_POINTS_PER_KILL_SLIME = ApocConfig.config.getInt("players.points-per-kill.slime", 3);

	@ConfigDesc(path = "players.points-per-kill.snowman", desc = "Amount of points player will earn per snow golem kill.")
	public static int PLAYERS_POINTS_PER_KILL_SNOWMAN = ApocConfig.config.getInt("players.points-per-kill.snowman", 5);

	@ConfigDesc(path = "players.points-per-kill.spider", desc = "Amount of points player will earn per spider kill.")
	public static int PLAYERS_POINTS_PER_KILL_SPIDER = ApocConfig.config.getInt("players.points-per-kill.spider", 5);

	@ConfigDesc(path = "players.points-per-kill.stray", desc = "Amount of points player will earn per stray kill.")
	public static int PLAYERS_POINTS_PER_KILL_STRAY = ApocConfig.config.getInt("players.points-per-kill.stray", 10);

	@ConfigDesc(path = "players.points-per-kill.witch", desc = "Amount of points player will earn per witch kill.")
	public static int PLAYERS_POINTS_PER_KILL_WITCH = ApocConfig.config.getInt("players.points-per-kill.witch", 10);

	@ConfigDesc(path = "players.points-per-kill.wither-skeleton", desc = "Amount of points player will earn per wither skeleton kill.")
	public static int PLAYERS_POINTS_PER_KILL_WITHER_SKELETON = ApocConfig.config.getInt("players.points-per-kill.wither-skeleton", 10);

	@ConfigDesc(path = "players.points-per-kill.wolf", desc = "Amount of points player will earn per wolf kill.")
	public static int PLAYERS_POINTS_PER_KILL_WOLF = ApocConfig.config.getInt("players.points-per-kill.wolf", 5);

	@ConfigDesc(path = "players.points-per-kill.zombie", desc = "Amount of points player will earn per zombie kill.")
	public static int PLAYERS_POINTS_PER_KILL_ZOMBIE = ApocConfig.config.getInt("players.points-per-kill.zombie", 5);

	@ConfigDesc(path = "plugin.use-reflection", desc = "Whether reflection methods should be used. Reflection is slower but should always be forwards compatible.")
	public static boolean PLUGIN_USE_REFLECTION = ApocConfig.config.getBoolean("plugin.use-reflection", true);

	@ConfigDesc(path = "sieges.load-preloaded-bosses", desc = "Whether preloaded bosses should be added along with specified ones.")
	public static boolean SIEGES_LOAD_PRELOADED_BOSSES = ApocConfig.config.getBoolean("sieges.load-preloaded-bosses", true);

	@ConfigDesc(path = "sieges.load-preloaded-sieges", desc = "Whether preloaded sieges should be added along with specified ones.")
	public static boolean SIEGES_LOAD_PRELOADED_SIEGES = ApocConfig.config.getBoolean("sieges.load-preloaded-sieges", true);

	@ConfigDesc(path = "sieges.spawn-distance", desc = "Maximum distance mobs should spawn from target team's town.")
	public static double SIEGES_SPAWN_DISTANCE = ApocConfig.config.getDouble("sieges.spawn-distance", 50.0D);

	@ConfigDesc(path = "sieges.spawn-interval", desc = "Amount of time to wait between each mob being spawned in a siege.")
	public static long SIEGES_SPAWN_INTERVAL = ApocConfig.config.getLong("sieges.spawn-interval", 500L);

	@ConfigDesc(path = "sieges.start-time", desc = "When sieges should start. Uses the same time value that /settime uses.")
	public static long SIEGES_START_TIME = ApocConfig.config.getLong("sieges.start-time", 18000L);

	@ConfigDesc(path = "teams.enforce-maximum-members", desc = "Whether teams can only support up to the number of players specified by teams.maximum-members.")
	public static boolean TEAMS_ENFORCE_MAXIMUM_MEMBERS = ApocConfig.config.getBoolean("teams.enforce-maximum-members", false);
	
	@ConfigDesc(path = "teams.maximum-members", desc = "Maximum number of players that can be on a given team.")
	public static int TEAMS_MAXIMUM_MEMBERS = ApocConfig.config.getInt("teams.maximum-members", 10);
	
	@ConfigDesc(path = "teams.minimum-spawning-distance", desc = "Minimum distance teams can spawn from each other when event starts.")
	public static double TEAMS_MINIMUM_SPAWNING_DISTANCE = ApocConfig.config.getDouble("teams.minimum-spawning-distance", 1000.0D);
	
}
