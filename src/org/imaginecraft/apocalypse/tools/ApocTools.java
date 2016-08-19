package org.imaginecraft.apocalypse.tools;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import org.imaginecraft.apocalypse.Apocalypse;
import org.imaginecraft.apocalypse.config.ConfigOption;
import org.imaginecraft.apocalypse.nms.NMSLib;

import net.md_5.bungee.api.ChatColor;

public class ApocTools {

	private static Apocalypse plugin = JavaPlugin.getPlugin(Apocalypse.class);
	
	private static final String OBC_PREFIX = Bukkit.getServer().getClass().getPackage().getName() + ".";
	private static final String NMS_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server");
	private static final String CUSTOM_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "org.imaginecraft.apocalypse.nms");
	
	private final static byte MILLISECONDS_PER_TICK = 50;
	private final static short MILLISECONDS_PER_SECOND = 1000;
	private final static int MILLISECONDS_PER_MINUTE = MILLISECONDS_PER_SECOND * 60;
	private final static int MILLISECONDS_PER_HOUR = MILLISECONDS_PER_MINUTE * 60;
	private final static int MILLISECONDS_PER_DAY = MILLISECONDS_PER_HOUR * 24;

	private final static Class<?> cusPathfinderGoalWalkToLocation = resolveClass(CUSTOM_PREFIX + "PathfinderGoalWalkToLocation"),
			nmsEntityBlaze = resolveClass(NMS_PREFIX + "EntityBlaze"),
			nmsEntityCreature = resolveClass(NMS_PREFIX + "EntityCreature"),
			nmsEntityHuman = resolveClass(NMS_PREFIX + "EntityHuman"),
			nmsEntityInsentient = resolveClass(NMS_PREFIX + "EntityInsentient"),
			nmsEntityLiving = resolveClass(NMS_PREFIX + "EntityLiving"),
			nmsEntitySpider = resolveClass(NMS_PREFIX + "EntitySpider"),
			nmsPathfinderGoal = resolveClass(NMS_PREFIX + "PathfinderGoal"),
			nmsPathfinderGoalBlazeFireball = resolveClass(NMS_PREFIX + "EntityBlaze$PathfinderGoalBlazeFireball"),
			nmsPathfinderGoalFloat = resolveClass(NMS_PREFIX + "PathfinderGoalFloat"),
			nmsPathfinderGoalHurtByTarget = resolveClass(NMS_PREFIX + "PathfinderGoalHurtByTarget"),
			nmsPathfinderGoalLeapAtTarget = resolveClass(NMS_PREFIX + "PathfinderGoalLeapAtTarget"),
			nmsPathfinderGoalRandomLookaround = resolveClass(NMS_PREFIX + "PathfinderGoalRandomLookaround"),
			nmsPathfinderGoalLookAtPlayer = resolveClass(NMS_PREFIX + "PathfinderGoalLookAtPlayer"),
			nmsPathfinderGoalRandomStroll = resolveClass(NMS_PREFIX + "PathfinderGoalRandomStroll"),
			nmsPathfinderGoalSpiderMeleeAttack = resolveClass(NMS_PREFIX + "EntitySpider$PathfinderGoalSpiderMeleeAttack"),
			nmsPathfinderGoalSpiderNearestAttackableTarget = resolveClass(NMS_PREFIX + "EntitySpider$PathfinderGoalSpiderNearestAttackableTarget");
	
	private static NMSLib nms;
	
	public ApocTools() {
		try {
			nms = (NMSLib) Class.forName(CUSTOM_PREFIX + "NMSLib").newInstance();
		} catch (Exception e) {
			plugin.getLogger().warning(ChatColor.RED + "NMSLib not found for this version of Bukkit, using reflection instead.");
			nms = null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Set<T> getClasses(Class<T> clazz) {
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
	
	private static Object getHandle(Object obj) {
		try {
			if (obj instanceof Entity) {
				return getPrivateField(obj, "entity").get(obj);
			}
			else if (obj instanceof World) {
				return getPrivateMethod(obj, "getHandle").invoke(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static Field getPrivateField(Object obj, String name) {
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
	
	private static Method getPrivateMethod(Object obj, String name, Class<?>... params) {
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
	
	public static long getTicks(long millis) {
		return millis / MILLISECONDS_PER_TICK;
	}
	
	public static String getTime(long millis) {
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
	
	private static Class<?> resolveClass(String name) {
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
	 * Will fail if distance is too great or entity and location are in different worlds.
	 */
	public static void setAggressive(Creature entity, Location loc) {
		if (ConfigOption.PLUGIN_USE_REFLECTION
				|| nms == null) {
			try {
				Object nmsEntity = getHandle(entity);
				nmsEntity.getClass().getMethod("setGoalTarget", nmsEntityLiving).invoke(nmsEntity, (Object)null);
				Object goalSelector = nmsEntity.getClass().getField("goalSelector").get(nmsEntity);
				Object targetSelector = nmsEntity.getClass().getField("goalSelector").get(nmsEntity);
				Set<?> goalB = (Set<?>) getPrivateField(goalSelector, "b").get(goalSelector);
				Set<?> goalC = (Set<?>) getPrivateField(goalSelector, "c").get(goalSelector);
				Set<?> targetB = (Set<?>) getPrivateField(targetSelector, "b").get(targetSelector);
				Set<?> targetC = (Set<?>) getPrivateField(targetSelector, "c").get(targetSelector);
				goalB.clear();
				goalC.clear();
				targetB.clear();
				targetC.clear();
				Method goalMethod = goalSelector.getClass().getMethod("a", int.class, nmsPathfinderGoal);
				Method targetMethod = targetSelector.getClass().getMethod("a", int.class, nmsPathfinderGoal);
				goalMethod.invoke(goalSelector, 0, nmsPathfinderGoalFloat.getConstructor(nmsEntityInsentient)
						.newInstance(nmsEntity));
				goalMethod.invoke(goalSelector, 5, cusPathfinderGoalWalkToLocation.getConstructor(nmsEntityInsentient, Location.class, double.class)
						.newInstance(nmsEntity, loc, 1.0D));
				goalMethod.invoke(goalSelector, 7, nmsPathfinderGoalRandomStroll.getConstructor(nmsEntityCreature, double.class)
						.newInstance(nmsEntity, 1.0D));
				goalMethod.invoke(goalSelector, 8, nmsPathfinderGoalLookAtPlayer.getConstructor(nmsEntityInsentient, Class.class, float.class)
						.newInstance(nmsEntity, nmsEntityHuman, 8.0F));
				goalMethod.invoke(goalSelector, 8, nmsPathfinderGoalRandomLookaround.getConstructor(nmsEntityInsentient)
						.newInstance(nmsEntity));
				targetMethod.invoke(targetSelector, 1, nmsPathfinderGoalHurtByTarget.getConstructor(nmsEntityCreature, boolean.class, Class[].class)
						.newInstance(nmsEntity, true, new Class[0]));
				switch (entity.getType()) {
					case BLAZE:
						goalMethod.invoke(goalSelector, 4, nmsPathfinderGoalBlazeFireball.getConstructor(nmsEntityBlaze)
								.newInstance(nmsEntity));
						break;
					case CAVE_SPIDER: case SPIDER:
						goalMethod.invoke(goalSelector, 3, nmsPathfinderGoalLeapAtTarget.getConstructor(nmsEntityInsentient, float.class)
								.newInstance(nmsEntity, 0.4F));
						goalMethod.invoke(goalSelector, 4, nmsPathfinderGoalSpiderNearestAttackableTarget.getConstructor(nmsEntitySpider, Class.class)
								.newInstance(nmsEntity, nmsEntityHuman));
						targetMethod.invoke(targetSelector, 2, nmsPathfinderGoalSpiderMeleeAttack.getConstructor(nmsEntitySpider)
								.newInstance(nmsEntity));
						break;
					case CREEPER:
						// TODO
					case SKELETON:
						// TODO
					case SNOWMAN:
						// TODO
					case WOLF:
						// TODO
					default:
						// TODO
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			nms.setAggressive(entity, loc);
		}
	}

}
