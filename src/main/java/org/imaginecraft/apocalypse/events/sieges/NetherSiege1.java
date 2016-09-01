package org.imaginecraft.apocalypse.events.sieges;

import org.bukkit.block.Biome;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.EntityType;
import org.imaginecraft.apocalypse.events.ApocSiege;

@SerializableAs("ApocSiege")
public class NetherSiege1 extends ApocSiege {

	public NetherSiege1() {
		super("Nether Siege 1");
		this.setBiome(Biome.HELL);
		this.setMobs(EntityType.BLAZE, 3);
		this.setMobs(EntityType.PIG_ZOMBIE, 3);
		this.setMobs("wither_skeleton", 3);
	}
	
}
