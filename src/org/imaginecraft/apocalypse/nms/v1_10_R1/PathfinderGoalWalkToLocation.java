package org.imaginecraft.apocalypse.nms.v1_10_R1;

import org.bukkit.Location;

import net.minecraft.server.v1_10_R1.EntityInsentient;
import net.minecraft.server.v1_10_R1.Navigation;
import net.minecraft.server.v1_10_R1.PathEntity;
import net.minecraft.server.v1_10_R1.PathfinderGoal;

public class PathfinderGoalWalkToLocation extends PathfinderGoal {

	private EntityInsentient entity;
	private Location loc;
	private Navigation navigation;
	private double speed;
	
	public PathfinderGoalWalkToLocation(EntityInsentient entity, Location loc, double speed) {
		this.entity = entity;
		this.loc = loc;
		this.speed = speed;
		navigation = (Navigation) entity.getNavigation();
	}
	
	@Override
	public boolean a() {
		return true;
	}
	
	@Override
	public void c() {
		entity.onGround = true;
		PathEntity pathEntity = navigation.a(loc.getX(), loc.getY(), loc.getZ());
		navigation.a(pathEntity, speed);
	}

}
