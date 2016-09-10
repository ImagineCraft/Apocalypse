package org.imaginecraft.apocalypse.config;

import java.lang.reflect.Field;

import org.imaginecraft.apocalypse.config.ConfigDesc;

public class ConfigOption {
	
	@ConfigDesc(path = "event.duration", desc = "How long in milliseconds the event should last.")
	public static long EVENT_DURATION = 1209600000L;
	
	@ConfigDesc(path = "event.warning-notifications", desc = "How many notifications players should get that an event is about to start.")
	public static int EVENT_WARNING_NOTIFICATIONS = 3;
	
	@ConfigDesc(path = "event.warning-time", desc = "How long in advance in milliseconds players should be notified of an upcoming event.")
	public static long EVENT_WARNING_TIME = 300000L;
	
	@ConfigDesc(path = "players.can-create-portals", desc = "Whether players can create portals in the event world.")
	public static boolean PLAYERS_CAN_CREATE_PORTALS = false;
	
	@ConfigDesc(path = "players.can-join-late", desc = "Whether players can join the event after it starts.")
	public static boolean PLAYERS_CAN_JOIN_LATE = true;
	
	@ConfigDesc(path = "players.can-pick-team", desc = "Whether players can pick their team when joining.")
	public static boolean PLAYERS_CAN_PICK_TEAM = false;
	
	@ConfigDesc(path = "players.can-place-signs-on-chests", desc = "Whether players can place signs on chests in the event world.")
	public static boolean PLAYERS_CAN_PLACE_SIGNS_ON_CHESTS = false;
	
	@ConfigDesc(path = "players.can-set-town", desc = "Whether players can set the town for their team.")
	public static boolean PLAYERS_CAN_SET_TOWN = false;
	
	@ConfigDesc(path = "players.can-switch-teams", desc = "Whether players can switch teams before the event starts.")
	public static boolean PLAYERS_CAN_SWITCH_TEAMS = false;

	@ConfigDesc(path = "players.points-per-kill.blaze", desc = "Amount of points player will earn per blaze kill.")
	public static int PLAYERS_POINTS_PER_KILL_BLAZE = 10;

	@ConfigDesc(path = "players.points-per-kill.cave-spider", desc = "Amount of points player will earn per cave spider kill.")
	public static int PLAYERS_POINTS_PER_KILL_CAVE_SPIDER = 3;

	@ConfigDesc(path = "players.points-per-kill.creeper", desc = "Amount of points player will earn per creeper kill.")
	public static int PLAYERS_POINTS_PER_KILL_CREEPER = 5;

	@ConfigDesc(path = "players.points-per-kill.enderman", desc = "Amount of points player will earn per enderman kill.")
	public static int PLAYERS_POINTS_PER_KILL_ENDERMAN = 10;

	@ConfigDesc(path = "players.points-per-kill.endermite", desc = "Amount of points player will earn per endermite kill.")
	public static int PLAYERS_POINTS_PER_KILL_ENDERMITE = 3;

	@ConfigDesc(path = "players.points-per-kill.husk", desc = "Amount of points player will earn per husk kill.")
	public static int PLAYERS_POINTS_PER_KILL_HUSK = 7;

	@ConfigDesc(path = "players.points-per-kill.magma-cube", desc = "Amount of points player will earn per magma cube kill.")
	public static int PLAYERS_POINTS_PER_KILL_MAGMA_CUBE = 5;

	@ConfigDesc(path = "players.points-per-kill.pig-zombie", desc = "Amount of points player will earn per pig zombie kill.")
	public static int PLAYERS_POINTS_PER_KILL_PIG_ZOMBIE = 7;

	@ConfigDesc(path = "players.points-per-kill.player", desc = "Amount of points player will earn per player kill.")
	public static int PLAYERS_POINTS_PER_KILL_PLAYER = 50;

	@ConfigDesc(path = "players.points-per-kill.polar-bear", desc = "Amount of points player will earn per polar bear kill.")
	public static int PLAYERS_POINTS_PER_KILL_POLAR_BEAR = 10;

	@ConfigDesc(path = "players.points-per-kill.silverfish", desc = "Amount of points player will earn per silverfish kill.")
	public static int PLAYERS_POINTS_PER_KILL_SILVERFISH = 3;

	@ConfigDesc(path = "players.points-per-kill.skeleton", desc = "Amount of points player will earn per skeleton kill.")
	public static int PLAYERS_POINTS_PER_KILL_SKELETON = 7;

	@ConfigDesc(path = "players.points-per-kill.slime", desc = "Amount of points player will earn per slime kill.")
	public static int PLAYERS_POINTS_PER_KILL_SLIME = 3;

	@ConfigDesc(path = "players.points-per-kill.snowman", desc = "Amount of points player will earn per snow golem kill.")
	public static int PLAYERS_POINTS_PER_KILL_SNOWMAN = 5;

	@ConfigDesc(path = "players.points-per-kill.spider", desc = "Amount of points player will earn per spider kill.")
	public static int PLAYERS_POINTS_PER_KILL_SPIDER = 5;

	@ConfigDesc(path = "players.points-per-kill.stray", desc = "Amount of points player will earn per stray kill.")
	public static int PLAYERS_POINTS_PER_KILL_STRAY = 10;

	@ConfigDesc(path = "players.points-per-kill.witch", desc = "Amount of points player will earn per witch kill.")
	public static int PLAYERS_POINTS_PER_KILL_WITCH = 10;

	@ConfigDesc(path = "players.points-per-kill.wither-skeleton", desc = "Amount of points player will earn per wither skeleton kill.")
	public static int PLAYERS_POINTS_PER_KILL_WITHER_SKELETON = 10;

	@ConfigDesc(path = "players.points-per-kill.wolf", desc = "Amount of points player will earn per wolf kill.")
	public static int PLAYERS_POINTS_PER_KILL_WOLF = 5;

	@ConfigDesc(path = "players.points-per-kill.zombie", desc = "Amount of points player will earn per zombie kill.")
	public static int PLAYERS_POINTS_PER_KILL_ZOMBIE = 5;

	@ConfigDesc(path = "plugin.use-reflection", desc = "Whether reflection methods should be used. Reflection is slower but should always be forwards compatible.")
	public static boolean PLUGIN_USE_REFLECTION = false;

	@ConfigDesc(path = "sieges.load-preloaded-bosses", desc = "Whether preloaded bosses should be added along with specified ones.")
	public static boolean SIEGES_LOAD_PRELOADED_BOSSES = true;

	@ConfigDesc(path = "sieges.load-preloaded-sieges", desc = "Whether preloaded sieges should be added along with specified ones.")
	public static boolean SIEGES_LOAD_PRELOADED_SIEGES = true;

	@ConfigDesc(path = "sieges.spawn-distance", desc = "Maximum distance mobs should spawn from target team's town.")
	public static double SIEGES_SPAWN_DISTANCE = 50.0D;

	@ConfigDesc(path = "sieges.spawn-interval", desc = "Amount of time in milliseconds to wait between each mob being spawned in a siege.")
	public static long SIEGES_SPAWN_INTERVAL = 500L;

	@ConfigDesc(path = "sieges.start-time", desc = "When sieges should start. Uses the same time value that /settime uses.")
	public static long SIEGES_START_TIME = 18000L;

	@ConfigDesc(path = "teams.enforce-maximum-members", desc = "Whether teams can only support up to the number of players specified by teams.maximum-members.")
	public static boolean TEAMS_ENFORCE_MAXIMUM_MEMBERS = false;
	
	@ConfigDesc(path = "teams.leader-can-remove-team", desc = "Whether team's leader can remove their team from the event.")
	public static boolean TEAMS_LEADER_CAN_REMOVE_TEAM = true;
	
	@ConfigDesc(path = "teams.leader-can-set-town", desc = "Whether team's leader can set their team's town.")
	public static boolean TEAMS_LEADER_CAN_SET_TOWN = true;
	
	@ConfigDesc(path = "teams.maximum-members", desc = "Maximum number of players that can be on a given team.")
	public static int TEAMS_MAXIMUM_MEMBERS = 10;
	
	@ConfigDesc(path = "teams.maximum-spawning-distance", desc = "Maximum distance teams can spawn from the event world's world border center. If the world border is closer, it will be used instead.")
	public static double TEAMS_MAXIMUM_SPAWNING_DISTANCE = 5000.0D;
	
	@ConfigDesc(path = "teams.minimum-spawning-distance", desc = "Minimum distance teams can spawn from each other when event starts.")
	public static double TEAMS_MINIMUM_SPAWNING_DISTANCE = 1000.0D;
	
	public static int intValue(String name) {
		try {
			Field field = ConfigOption.class.getField(name);
			return field.getInt(field);
		} catch (Exception e) {
			return 0;
		}
	}
	
}
