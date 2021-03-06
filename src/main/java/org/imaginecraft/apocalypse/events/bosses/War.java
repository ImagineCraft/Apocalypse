package org.imaginecraft.apocalypse.events.bosses;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.imaginecraft.apocalypse.events.ApocBoss;

@SerializableAs("ApocBoss")
public class War extends ApocBoss {

	public War() {
		super("War");
		ItemStack arrows = new ItemStack(Material.TIPPED_ARROW, 16);
		PotionMeta meta1 = (PotionMeta) arrows.getItemMeta();
		meta1.setBasePotionData(new PotionData(PotionType.UNCRAFTABLE));
		meta1.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1), true);
		arrows.setItemMeta(meta1);
		this.setDroppedExp(75);
		this.setEquipment(EquipmentSlot.CHEST, new ItemStack(Material.IRON_CHESTPLATE, 1));
		this.setEquipment(EquipmentSlot.FEET, new ItemStack(Material.IRON_BOOTS, 1));
		this.setEquipment(EquipmentSlot.HAND, new ItemStack(Material.BOW, 1));
		this.setEquipment(EquipmentSlot.HEAD, new ItemStack(Material.IRON_HELMET, 1));
		this.setEquipment(EquipmentSlot.LEGS, new ItemStack(Material.IRON_LEGGINGS, 1));
		this.setEquipment(EquipmentSlot.OFF_HAND, arrows);
		this.setHealth(60.0D);
		this.setHorse(Horse.Variant.SKELETON_HORSE);
		this.setPoints(75);
		this.setSkeletonType(SkeletonType.STRAY);
		this.setType(EntityType.SKELETON);
	}

}
