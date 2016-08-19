package org.imaginecraft.apocalypse.nms.v1_10_R1;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftCreature;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Skeleton;

import net.minecraft.server.v1_10_R1.EntityBlaze;
import net.minecraft.server.v1_10_R1.EntityCreature;
import net.minecraft.server.v1_10_R1.EntityCreeper;
import net.minecraft.server.v1_10_R1.EntityHuman;
import net.minecraft.server.v1_10_R1.EntitySkeleton;
import net.minecraft.server.v1_10_R1.EntitySnowman;
import net.minecraft.server.v1_10_R1.EntitySpider;
import net.minecraft.server.v1_10_R1.PathfinderGoal;
import net.minecraft.server.v1_10_R1.PathfinderGoalArrowAttack;
import net.minecraft.server.v1_10_R1.PathfinderGoalBowShoot;
import net.minecraft.server.v1_10_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_10_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_10_R1.PathfinderGoalLeapAtTarget;
import net.minecraft.server.v1_10_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_10_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_10_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_10_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_10_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_10_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_10_R1.PathfinderGoalSwell;

public class NMSLib implements org.imaginecraft.apocalypse.nms.NMSLib {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void setAggressive(Creature entity, Location loc) {
		EntityCreature handle = ((CraftCreature)entity).getHandle();
		handle.setGoalTarget(null);
		Set<?> goalB = (Set<?>) getPrivateField("b", PathfinderGoalSelector.class, handle.goalSelector),
				goalC = (Set<?>) getPrivateField("c", PathfinderGoalSelector.class, handle.goalSelector),
				targetB = (Set<?>) getPrivateField("b", PathfinderGoalSelector.class, handle.targetSelector),
				targetC = (Set<?>) getPrivateField("c", PathfinderGoalSelector.class, handle.targetSelector);
		goalB.clear();
		goalC.clear();
		targetB.clear();
		targetC.clear();
		handle.goalSelector.a(0, new PathfinderGoalFloat(handle));
		handle.goalSelector.a(5, new PathfinderGoalWalkToLocation(handle, loc, 1.0D));
		handle.goalSelector.a(7, new PathfinderGoalRandomStroll(handle, 1.0D));
		handle.goalSelector.a(8, new PathfinderGoalLookAtPlayer(handle, EntityHuman.class, 8.0F));
		handle.goalSelector.a(8, new PathfinderGoalRandomLookaround(handle));
		handle.targetSelector.a(1, new PathfinderGoalHurtByTarget(handle, true, new Class[0]));
		switch (entity.getType()) {
			case BLAZE:
				Class<?> pathfinderGoalBlazeFireball = getPrivateClass("PathfinderGoalBlazeFireball", EntityBlaze.class);
				try {
					handle.goalSelector.a(4, (PathfinderGoal) pathfinderGoalBlazeFireball.getConstructor(EntityBlaze.class).newInstance(handle));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case CAVE_SPIDER: case SPIDER:
				handle.goalSelector.a(3, new PathfinderGoalLeapAtTarget(handle, 0.4F));
				Class<?> pathfinderGoalSpiderNearestAttackableTarget = getPrivateClass("PathfinderGoalSpiderNearestAttackableTarget", EntitySpider.class);
				Class<?> pathfinderGoalSpiderMeleeAttack = getPrivateClass("PathfinderGoalSpiderMeleeAttack", EntitySpider.class);
				try {
					handle.goalSelector.a(4, (PathfinderGoal) pathfinderGoalSpiderMeleeAttack.getConstructor(EntitySpider.class).newInstance(handle));
					handle.targetSelector.a(2, (PathfinderGoal) pathfinderGoalSpiderNearestAttackableTarget.getConstructor(EntitySpider.class, Class.class).newInstance(handle, EntityHuman.class));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case CREEPER:
				handle.goalSelector.a(2, new PathfinderGoalSwell((EntityCreeper) handle));
				handle.goalSelector.a(4, new PathfinderGoalMeleeAttack(handle, 1.0D, false));
				handle.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(handle, EntityHuman.class, true));
			case SKELETON:
				if (((Skeleton)entity).getEquipment().getItemInMainHand().getType() == Material.BOW) {
					handle.goalSelector.a(4, new PathfinderGoalBowShoot((EntitySkeleton)handle, 1.0D, 20, 15.0F));
				}
				else {
					handle.goalSelector.a(4, new PathfinderGoalMeleeAttack(handle, 1.2D, false));
				}
			case SNOWMAN:
				handle.goalSelector.a(1, new PathfinderGoalArrowAttack((EntitySnowman)handle, 1.25D, 20, 10.0F));
				handle.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(handle, EntityHuman.class, true));
			case WOLF:
				handle.goalSelector.a(3, new PathfinderGoalLeapAtTarget(handle, 0.4F));
				break;
			default:
				handle.goalSelector.a(2, new PathfinderGoalMeleeAttack(handle, 1.0D, false));
				handle.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(handle, EntityHuman.class, true));
				break;
		}
	}

}
