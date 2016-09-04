package org.imaginecraft.apocalypse.tools;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import org.imaginecraft.apocalypse.Apocalypse;
import org.imaginecraft.apocalypse.config.ConfigOption;
import org.imaginecraft.apocalypse.nms.NMSLib;
import org.imaginecraft.apocalypse.teams.ApocTeam;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.PacketConstructor;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.ChunkCoordIntPair;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;
import com.comphenix.protocol.wrappers.WrappedBlockData;

/**
 * TODO
 */
public class ApocTools {

	private final Apocalypse plugin = JavaPlugin.getPlugin(Apocalypse.class);
	
	private final static String OBC_PREFIX = Bukkit.getServer().getClass().getPackage().getName() + ".";
	private final static String NMS_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server");
	private final static String CUSTOM_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "org.imaginecraft.apocalypse.nms");
	
	private final static byte MILLISECONDS_PER_TICK = 50;
	private final static short MILLISECONDS_PER_SECOND = 1000;
	private final static int MILLISECONDS_PER_MINUTE = MILLISECONDS_PER_SECOND * 60;
	private final static int MILLISECONDS_PER_HOUR = MILLISECONDS_PER_MINUTE * 60;
	private final static int MILLISECONDS_PER_DAY = MILLISECONDS_PER_HOUR * 24;

	private final Class<?> cusPathfinderGoalWalkToLocation = resolveClass(CUSTOM_PREFIX + "PathfinderGoalWalkToLocation"),
			nmsEntityCreature = resolveClass(NMS_PREFIX + "EntityCreature"),
			nmsEntityHuman = resolveClass(NMS_PREFIX + "EntityHuman"),
			nmsEntityInsentient = resolveClass(NMS_PREFIX + "EntityInsentient"),
			nmsEntityLiving = resolveClass(NMS_PREFIX + "EntityLiving"),
			nmsPathfinderGoal = resolveClass(NMS_PREFIX + "PathfinderGoal"),
			nmsPathfinderGoalHurtByTarget = resolveClass(NMS_PREFIX + "PathfinderGoalHurtByTarget"),
			nmsPathfinderGoalNearestAttackableTarget = resolveClass(NMS_PREFIX + "PathfinderGoalNearestAttackableTarget");
	
	private Map<BlockPosition, PacketContainer> blocks = new HashMap<BlockPosition, PacketContainer>();
	
	private final NMSLib nms = getNMSLib();
	private final ProtocolManager pm = ProtocolLibrary.getProtocolManager();
	private PacketConstructor chunkConst;
	private final Random random = new Random();
	
	public ApocTools() {
		pm.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.BLOCK_CHANGE) {
			@Override
			public void onPacketSending(PacketEvent event) {
				if (event.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE) {
					BlockPosition bPos = event.getPacket().getBlockPositionModifier().read(0);
					if (blocks.containsKey(bPos)) {
						event.setPacket(blocks.get(bPos));
					}
				}
			}
		});
	}
	
	/**
	 * Makes a collection of blocks appear as a different material.
	 * <p>
	 * Uses packets to maintain the appearance. The disguise will remain until {@link PowerTools#blockUpdate(Collection<Block>)} is called.
	 * <p>
	 * Block disguises are not maintained after the server restarts or reloads.
	 * @param blocks - Blocks to disguise
	 * @param material - Material to disguise the blocks as. Using non-block materials may kick any players who can see it
	 */
	public void blockDisguise(Collection<Block> blocks, Material material) {
		blockDisguise(blocks, material, 0);
	}
	
	/**
	 * Makes a collection of blocks appear as a different material.
	 * <p>
	 * Uses packets to maintain the appearance. The disguise will remain until {@link PowerTools#blockUpdate(Collection<Block>)} is called.
	 * <p>
	 * Block disguises are not maintained after the server restarts or reloads.
	 * @param blocks - Blocks to disguise
	 * @param material - Material to disguise the blocks as. Using non-block materials may kick any players who can see it
	 * @param meta - Metadata to be applied to the packet. Useful for materials that appear different with metadata (e.g. Wool)
	 */
	public void blockDisguise(Collection<Block> blocks, Material material, int meta) {
		Map<Chunk, List<Block>> chunks = new HashMap<Chunk, List<Block>>();
		for (Block block : blocks) {
			BlockPosition bPos = new BlockPosition(block.getX(), block.getY(), block.getZ());
			PacketContainer packet = pm.createPacket(PacketType.Play.Server.BLOCK_CHANGE, true);
			packet.getBlockPositionModifier().write(0, bPos);
			packet.getBlockData().write(0, WrappedBlockData.createData(material, meta));
			this.blocks.put(bPos, packet);
			if (!chunks.containsKey(block.getChunk())) {
				chunks.put(block.getChunk(), new ArrayList<Block>());
			}
			chunks.get(block.getChunk()).add(block);
		}
		for (Chunk chunk : chunks.keySet()) {
			PacketContainer packet = pm.createPacket(PacketType.Play.Server.MULTI_BLOCK_CHANGE, true);
			packet.getChunkCoordIntPairs().write(0, new ChunkCoordIntPair(chunk.getX(), chunk.getZ()));
			MultiBlockChangeInfo[] changes = new MultiBlockChangeInfo[chunks.get(chunk).size()];
			for (int i = 0; i < changes.length; i ++) {
				changes[i] = new MultiBlockChangeInfo(chunks.get(chunk).get(i).getLocation(), WrappedBlockData.createData(material, meta));
			}
			packet.getMultiBlockChangeInfoArrays().write(0, changes);
			pm.broadcastServerPacket(packet);
		}
	}
	
	/**
	 * Removes any disguises from a collection of blocks, making them appear as they should again.
	 * @param blocks - Blocks to update
	 */
	@SuppressWarnings("deprecation")
	public void blockUpdate(Collection<Block> blocks) {
		Map<Chunk, List<Block>> chunks = new HashMap<Chunk, List<Block>>();
		for (Block block : blocks) {
			if (!chunks.containsKey(block.getChunk())) {
				chunks.put(block.getChunk(), new ArrayList<Block>());
			}
			chunks.get(block.getChunk()).add(block);
		}
		for (Chunk chunk : chunks.keySet()) {
			PacketContainer packet = pm.createPacket(PacketType.Play.Server.MULTI_BLOCK_CHANGE, true);
			packet.getChunkCoordIntPairs().write(0, new ChunkCoordIntPair(chunk.getX(), chunk.getZ()));
			MultiBlockChangeInfo[] changes = new MultiBlockChangeInfo[chunks.get(chunk).size()];
			for (int i = 0; i < changes.length; i ++) {
				changes[i] = new MultiBlockChangeInfo(chunks.get(chunk).get(i).getLocation(),
						WrappedBlockData.createData(chunks.get(chunk).get(i).getType(), chunks.get(chunk).get(i).getData()));
			}
			packet.getMultiBlockChangeInfoArrays().write(0, changes);
			pm.broadcastServerPacket(packet);
		}
	}
	
	/**
	 * TODO
	 * @param team
	 * @param world
	 * @return
	 */
	public Location findCenterLocation(ApocTeam team, World world) {
		Location loc = team.getTown();
		if (loc == null) {
			Location loc1 = team.getSpawn(), loc2 = team.getSpawn();
			for (OfflinePlayer player1 : team.getPlayers()) {
				for (OfflinePlayer player2 : team.getPlayers()) {
					if (player1.isOnline() && player2.isOnline()) {
						Location loc3 = ((Player)player1).getLocation(), loc4 = ((Player)player2).getLocation();
						if (loc3.getWorld() == world
								&& loc4.getWorld() == world) {
							loc3.setY(0.0D);
							loc4.setY(0.0D);
							if (loc3.distanceSquared(loc4) > loc1.distanceSquared(loc2)) {
								loc1 = loc3;
								loc2 = loc4;
							}
						}
					}
				}
			}
			loc = loc1.toVector().midpoint(loc2.toVector()).toLocation(world);
		}
		return loc;
	}
	
	/**
	 * TODO
	 * @param loc
	 * @return
	 */
	public Location findSpawnLocation(Location loc) {
		int x = (int) ((random.nextDouble() * ((loc.getX() + ConfigOption.SIEGES_SPAWN_DISTANCE) - (loc.getX() - ConfigOption.SIEGES_SPAWN_DISTANCE))) + loc.getX() - ConfigOption.SIEGES_SPAWN_DISTANCE);
		int z = (int) ((random.nextDouble() * ((loc.getZ() + ConfigOption.SIEGES_SPAWN_DISTANCE) - (loc.getZ() - ConfigOption.SIEGES_SPAWN_DISTANCE))) + loc.getZ() - ConfigOption.SIEGES_SPAWN_DISTANCE);
		return loc.getWorld().getHighestBlockAt(x, z).getLocation();
	}
	
	/**
	 * TODO
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> Set<T> getClasses(Class<T> clazz) {
		Set<T> classes = new HashSet<T>();
		try {
			JarFile file = new JarFile(plugin.getJarFile());
			for (Enumeration<JarEntry> entry = file.entries(); entry.hasMoreElements();) {
				JarEntry jarEntry = entry.nextElement();
				String name = jarEntry.getName().replace("/", ".");
				if (name.startsWith("org.imaginecraft.apocalypse.") && name.endsWith(".class")) {
					Class<?> query = Class.forName(name.substring(0, name.length() - 6));
					if (query.getSuperclass() == clazz) {
						classes.add((T) query.newInstance());
					}
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classes;
	}
	
	// Gets the NMS instance of a given Bukkit object. Used for reflection
	private Object getHandle(Object obj) {
		try {
			if (obj instanceof Entity) {
				return getPrivateField(obj, "entity").get(obj);
			}
			else if (obj instanceof Chunk
					|| obj instanceof World) {
				return getPrivateMethod(obj, "getHandle").invoke(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// Gets a version of NMSLib that will work with the currently running version of Minecraft
	private NMSLib getNMSLib() {
		try {
			return (NMSLib) Class.forName(CUSTOM_PREFIX + "NMSLib").newInstance();
		} catch (Exception e) {
			// A version of NMSLib for this MC version doesn't exist
			plugin.getLogger().warning(ChatColor.RED + "NMSLib not found for this version of Bukkit, using reflection instead.");
			return null;
		}
	}
	
	// Gets a private field while making it accessible
	private Field getPrivateField(Object obj, String name) {
		Class<?> check = obj.getClass();
		do {
			for (Field field : check.getDeclaredFields()) {
				if (field.getName() == name) {
					field.setAccessible(true);
					return field;
				}
			}
			check = check.getSuperclass();
		} while (check != null);
		return null;
	}
	
	// Gets a private method while making it accessible
	private Method getPrivateMethod(Object obj, String name, Class<?>... params) {
		Class<?> check = obj.getClass();
		do {
			for (Method method : check.getDeclaredMethods()) {
				if (method.getName() == name
						&& Arrays.equals(method.getParameterTypes(), params)) {
					method.setAccessible(true);
					return method;
				}
			}
			check = check.getSuperclass();
		} while (check != null);
		return null;
	}
	
	/**
	 * Converts real world milliseconds into Minecraft ticks
	 * @param millis
	 * @return
	 */
	public long getTicks(long millis) {
		return millis / MILLISECONDS_PER_TICK;
	}
	
	/**
	 * Creates a reader-friendly string representation of a given amount of milliseconds
	 * @param millis
	 * @return
	 */
	public String getTime(long millis) {
		String tmp = "";
		long time = millis;
		if (time >= MILLISECONDS_PER_DAY) {
			tmp = tmp + time / MILLISECONDS_PER_DAY + ((time / MILLISECONDS_PER_DAY) == 1 ? " day " : " days ");
			time %= MILLISECONDS_PER_DAY;
		}
		if (time >= MILLISECONDS_PER_HOUR) {
			tmp = tmp + time / MILLISECONDS_PER_HOUR + ((time / MILLISECONDS_PER_HOUR) == 1 ? " hour " : " hours ");
			time %= MILLISECONDS_PER_HOUR;
		}
		if (time >= MILLISECONDS_PER_MINUTE) {
			tmp = tmp + time / MILLISECONDS_PER_MINUTE + ((time / MILLISECONDS_PER_MINUTE) == 1 ? " minute " : " minutes ");
			time %= MILLISECONDS_PER_MINUTE;
		}
		if (time >= MILLISECONDS_PER_SECOND) {
			tmp = tmp + time / MILLISECONDS_PER_SECOND + ((time / MILLISECONDS_PER_SECOND) == 1 ? " second " : " seconds ");
		}
		if (tmp.equalsIgnoreCase("")) return "less than 1 second";
		else {
			tmp = tmp.substring(0, tmp.lastIndexOf(" "));
			return tmp;
		}
	}
	
	// Tries to return a specified class if it exists
	private Class<?> resolveClass(String name) {
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Attempts to make the specified entity aggressive and navigate to the specified location.
	 * <p>
	 * Will fail if entity and location are in different worlds.
	 */
	public void setAggressive(LivingEntity entity, Location loc) {
		if (ConfigOption.PLUGIN_USE_REFLECTION
				|| nms == null) {
			try {
				Object nmsEntity = getHandle(entity);
				if (nmsEntityCreature.isInstance(nmsEntity)) {
					nmsEntity.getClass().getMethod("setGoalTarget", nmsEntityLiving).invoke(nmsEntity, (Object)null);
					Object goalSelector = nmsEntity.getClass().getField("goalSelector").get(nmsEntity);
					Object targetSelector = nmsEntity.getClass().getField("targetSelector").get(nmsEntity);
					Set<?> targetB = (Set<?>) getPrivateField(targetSelector, "b").get(targetSelector);
					Set<?> targetC = (Set<?>) getPrivateField(targetSelector, "c").get(targetSelector);
					targetB.clear();
					targetC.clear();
					Method goalMethod = goalSelector.getClass().getMethod("a", int.class, nmsPathfinderGoal);
					goalMethod.invoke(goalSelector, 1, cusPathfinderGoalWalkToLocation.getConstructor(nmsEntityInsentient, Location.class, double.class)
							.newInstance(nmsEntity, loc, 1.0D));
					Method targetMethod = targetSelector.getClass().getMethod("a", int.class, nmsPathfinderGoal);
					targetMethod.invoke(targetSelector, 1, nmsPathfinderGoalHurtByTarget.getConstructor(nmsEntityCreature, boolean.class, Class[].class)
							.newInstance(nmsEntity, true, new Class[0]));
					switch (entity.getType()) {
						default:
							targetMethod.invoke(targetSelector, 2, nmsPathfinderGoalNearestAttackableTarget.getConstructor(nmsEntityCreature, Class.class, boolean.class)
									.newInstance(nmsEntity, nmsEntityHuman, true));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			nms.setAggressive(entity, loc);
		}
	}
	
	/**
	 * TODO
	 * @param type
	 * @param loc
	 * @return
	 */
	public LivingEntity spawnMob(String type, Location loc) {
		LivingEntity entity = null;
		EntityType eType = null;
		if (type.equalsIgnoreCase("husk")) eType = EntityType.ZOMBIE;
		else if (type.equalsIgnoreCase("stray")
				|| type.equalsIgnoreCase("wither_skeleton")) eType = EntityType.SKELETON;
		else eType = EntityType.valueOf(type);
		if (eType == EntityType.BLAZE
				|| eType == EntityType.MAGMA_CUBE
				|| eType == EntityType.PIG_ZOMBIE
				|| type.equalsIgnoreCase("wither_skeleton")) {
			BlockFace dir = Math.random() >= 0.5D ? BlockFace.NORTH : BlockFace.EAST;
			int height = 3;
			int minX = -Math.abs(dir.getModZ());
			int maxX = Math.abs(dir.getModZ() * 2);
			int minZ = -Math.abs(dir.getModX());
			int maxZ = Math.abs(dir.getModX() * 2);
			List<Block> frame = new ArrayList<Block>();
			List<Block> portal = new ArrayList<Block>();
			for (int x = minX; x <= maxX; x ++) {
				for (int y = -1; y <= height; y ++) {
					for (int z = minZ; z <= maxZ; z ++) {
						if ((x == minX && minX != 0)
								|| (x == maxX && maxX != 0)
								|| (z == minZ && minZ != 0)
								|| (z == maxZ && maxZ != 0)
								|| y == -1
								|| y == height) {
							frame.add(loc.getBlock().getRelative(x, y, z));
						}
						else {
							portal.add(loc.getBlock().getRelative(x, y, z));
						}
					}
				}
			}
			blockDisguise(frame, Material.OBSIDIAN);
			blockDisguise(portal, Material.PORTAL, dir == BlockFace.NORTH ? 0 : 2);
			entity = (LivingEntity) loc.getWorld().spawnEntity(loc, eType);
			if (type.equalsIgnoreCase("wither_skeleton")) {
				((Skeleton) entity).setSkeletonType(SkeletonType.WITHER);
				entity.getEquipment().setItemInMainHand(new ItemStack(Material.STONE_SWORD));
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					for (Block block : frame) {
						block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, Material.OBSIDIAN);
					}
					blockUpdate(frame);
					blockUpdate(portal);
				}
			}.runTaskLater(plugin, 20L);
		}
//		else if (eType == EntityType.CAVE_SPIDER
//				|| eType == EntityType.SPIDER) {
//			// TODO
//		}
//		else if (eType == EntityType.CREEPER) {
//			// TODO
//		}
//		else if (eType == EntityType.ENDERMAN
//				|| eType == EntityType.ENDERMITE) {
//			// TODO
//		}
//		else if (eType == EntityType.POLAR_BEAR
//				|| eType == EntityType.SNOWMAN) {
//			// TODO
//		}
//		else if (eType == EntityType.SILVERFISH) {
//			// TODO
//		}
//		else if (eType == EntityType.SKELETON
//				|| eType == EntityType.ZOMBIE) {
//			// TODO
//		}
//		else if (eType == EntityType.SLIME) {
//			// TODO
//		}
//		else if (eType == EntityType.WITCH) {
//			// TODO
//		}
//		else if (eType == EntityType.WOLF) {
//			// TODO
//		}
		else {
			entity = (LivingEntity) loc.getWorld().spawnEntity(loc, eType);
		}
		return entity;
	}
	
	/**
	 * TODO
	 * @param chunk
	 */
	public void updateChunk(Chunk chunk) {
		if (chunkConst == null) chunkConst = pm.createPacketConstructor(PacketType.Play.Server.MAP_CHUNK, getHandle(chunk), 0);
		PacketContainer packet = chunkConst.createPacket(getHandle(chunk), 65535);
		pm.broadcastServerPacket(packet);
	}

}
