package org.imaginecraft.apocalypse.events.bosses;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import org.imaginecraft.apocalypse.events.ApocBoss;

@SerializableAs("ApocBoss")
public class ZombieCaptain extends ApocBoss {

	private final ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS, 1),
			chest = new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1),
			helmet = new ItemStack(Material.CHAINMAIL_HELMET, 1),
			pants = new ItemStack(Material.CHAINMAIL_LEGGINGS, 1),
			sword = new ItemStack(Material.STONE_SWORD, 1);
	
	public ZombieCaptain() {
		super("Zombie Captain");
		this.setBaby(false);
		this.setEquipment(EquipmentSlot.CHEST, chest);
		this.setEquipment(EquipmentSlot.FEET, boots);
		this.setEquipment(EquipmentSlot.HAND, sword);
		this.setEquipment(EquipmentSlot.HEAD, helmet);
		this.setEquipment(EquipmentSlot.LEGS, pants);
		this.setDroppedExp(50);
		this.setHealth(50.0D);
		this.setPoints(50);
		this.setResistance(DamageCause.FIRE, 0.0D);
		this.setResistance(DamageCause.FIRE_TICK, 0.0D);
		this.setResistance(DamageCause.LAVA, 0.0D);
		this.setType(EntityType.ZOMBIE);
		this.setZombieType(Profession.NORMAL);
	}

}
