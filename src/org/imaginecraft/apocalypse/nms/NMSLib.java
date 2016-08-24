package org.imaginecraft.apocalypse.nms;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public interface NMSLib {

	public void setAggressive(LivingEntity entity);
	
	public void setDestination(LivingEntity entity, Location loc);
	
	public default Class<?> getPrivateClass(String className, Class<?> clazz) {
		// TODO
		return null;
	}
	
	public default Object getPrivateField(String fieldName, Class<?> clazz, Object object) {
		// TODO
		return null;
	}
	
}
