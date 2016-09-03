package org.imaginecraft.apocalypse.events.sieges;

import org.bukkit.block.Biome;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.EntityType;
import org.imaginecraft.apocalypse.events.ApocSiege;

@SerializableAs("ApocSiege")
public class SnowSiege1 extends ApocSiege {

	public SnowSiege1() {
		super("Snow Siege 1");
		this.setBiome(Biome.TAIGA_COLD);
		this.setMobs(EntityType.POLAR_BEAR, 3);
		this.setMobs(EntityType.SNOWMAN, 5);
		this.setStorm(true);
	}

}
