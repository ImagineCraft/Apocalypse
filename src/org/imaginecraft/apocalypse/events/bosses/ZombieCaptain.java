package org.imaginecraft.apocalypse.events.bosses;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.imaginecraft.apocalypse.events.ApocBoss;

@SerializableAs("ApocBoss")
public class ZombieCaptain extends ApocBoss {

	private final ItemStack chest = new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1),
			helmet = new ItemStack(Material.CHAINMAIL_HELMET, 1),
			sword = new ItemStack(Material.IRON_SWORD, 1);
	
	public ZombieCaptain() {
		chest.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 3);
		chest.addEnchantment(Enchantment.DURABILITY, 1);
		this.setBaby(false);
		this.setEquipment(EquipmentSlot.CHEST, chest);
		this.setEquipment(EquipmentSlot.HEAD, helmet);
		this.setEquipment(EquipmentSlot.HAND, sword);
		this.setHealth(50.0D);
		this.setName("Zombie Captain");
		this.setPoints(100);
		this.setResistance(DamageCause.FIRE, 0.0D);
		this.setResistance(DamageCause.FIRE_TICK, 0.0D);
		this.setResistance(DamageCause.LAVA, 0.0D);
		this.setType(EntityType.ZOMBIE);
		this.setZombieType(Profession.NORMAL);
	}

}
