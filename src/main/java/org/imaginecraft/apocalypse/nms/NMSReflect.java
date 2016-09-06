package org.imaginecraft.apocalypse.nms;

import java.lang.reflect.Method;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class NMSReflect implements NMSLib {
	
	private final static String OBC_PREFIX = Bukkit.getServer().getClass().getPackage().getName() + ".";
	private final static String NMS_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server");
	private final static String CUSTOM_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "org.imaginecraft.apocalypse.nms");

	private final Class<?> cusPathfinderGoalWalkToLocation = resolveClass(CUSTOM_PREFIX + "PathfinderGoalWalkToLocation"),
			nmsEntityCreature = resolveClass(NMS_PREFIX + "EntityCreature"),
			nmsEntityHuman = resolveClass(NMS_PREFIX + "EntityHuman"),
			nmsEntityInsentient = resolveClass(NMS_PREFIX + "EntityInsentient"),
			nmsEntityLiving = resolveClass(NMS_PREFIX + "EntityLiving"),
			nmsPathfinderGoal = resolveClass(NMS_PREFIX + "PathfinderGoal"),
			nmsPathfinderGoalHurtByTarget = resolveClass(NMS_PREFIX + "PathfinderGoalHurtByTarget"),
			nmsPathfinderGoalNearestAttackableTarget = resolveClass(NMS_PREFIX + "PathfinderGoalNearestAttackableTarget");
	
	// Tries to return a specified class if it exists
	private Class<?> resolveClass(String name) {
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void setAggressive(LivingEntity entity, Location loc) {
		try {
			Object nmsEntity = getHandle(entity);
			if (nmsEntityCreature.isInstance(nmsEntity)) {
				nmsEntity.getClass().getMethod("setGoalTarget", nmsEntityLiving).invoke(nmsEntity, (Object)null);
				Object goalSelector = nmsEntity.getClass().getField("goalSelector").get(nmsEntity);
				Object targetSelector = nmsEntity.getClass().getField("targetSelector").get(nmsEntity);
				Set<?> targetB = (Set<?>) getPrivateField(targetSelector.getClass(), "b").get(targetSelector);
				Set<?> targetC = (Set<?>) getPrivateField(targetSelector.getClass(), "c").get(targetSelector);
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

}
