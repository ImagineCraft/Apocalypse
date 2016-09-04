package org.imaginecraft.apocalypse.nms;

import java.lang.reflect.Field;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public interface NMSLib {

	/**
	 * TODO
	 * @param entity
	 * @param loc
	 */
	public void setAggressive(LivingEntity entity, Location loc);
	
	/**
	 * TODO
	 * @param fieldName
	 * @param clazz
	 * @return
	 */
	public default Field getPrivateField(String fieldName, Class<?> clazz) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
