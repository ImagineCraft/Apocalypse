package org.imaginecraft.apocalypse.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

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
	 * @param object
	 * @return
	 */
	public default Object getHandle(Object obj) {
		try {
			return getPrivateMethod(obj.getClass(), "getHandle").invoke(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * TODO
	 * @param clazz
	 * @param name
	 * @return
	 */
	public default Field getPrivateField(Class<?> clazz, String name) {
		do {
			for (Field field : clazz.getDeclaredFields()) {
				if (field.getName() == name) {
					field.setAccessible(true);
					return field;
				}
			}
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		return null;
	}
	
	/**
	 * TODO
	 * @param clazz
	 * @param name
	 * @param params
	 * @return
	 */
	public default Method getPrivateMethod(Class<?> clazz, String name, Class<?>... params) {
		do {
			for (Method method : clazz.getDeclaredMethods()) {
				if (method.getName() == name
						&& Arrays.equals(method.getParameterTypes(), params)) {
					method.setAccessible(true);
					return method;
				}
			}
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		return null;
	}
	
}
