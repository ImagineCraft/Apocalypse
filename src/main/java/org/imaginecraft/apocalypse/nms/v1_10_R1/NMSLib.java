package org.imaginecraft.apocalypse.nms.v1_10_R1;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;

import net.minecraft.server.v1_10_R1.Entity;
import net.minecraft.server.v1_10_R1.EntityCreature;
import net.minecraft.server.v1_10_R1.EntityHuman;
import net.minecraft.server.v1_10_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_10_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_10_R1.PathfinderGoalSelector;

public class NMSLib implements org.imaginecraft.apocalypse.nms.NMSLib {

	@Override
	public void setAggressive(LivingEntity entity, Location loc) {
		Entity nmsEntity = ((CraftEntity)entity).getHandle();
		if (nmsEntity instanceof EntityCreature) {
			EntityCreature handle = (EntityCreature) nmsEntity;
			handle.setGoalTarget(null);
			Set<?> targetB, targetC;
			try {
				targetB = (Set<?>) getPrivateField("b", PathfinderGoalSelector.class).get(handle.targetSelector);
				targetC = (Set<?>) getPrivateField("c", PathfinderGoalSelector.class).get(handle.targetSelector);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			targetB.clear();
			targetC.clear();
			handle.goalSelector.a(1, new PathfinderGoalWalkToLocation(handle, loc, 1.0D));
			handle.targetSelector.a(1, new PathfinderGoalHurtByTarget(handle, true, new Class[0]));
			switch (entity.getType()) {
				default:
					handle.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<EntityHuman>(handle, EntityHuman.class, true));
					break;
			}
		}
	}

}
