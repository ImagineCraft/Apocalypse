package org.imaginecraft.apocalypse.events.sieges;

import org.bukkit.block.Biome;
import org.bukkit.configuration.serialization.SerializableAs;
import org.imaginecraft.apocalypse.events.ApocSiege;

@SerializableAs("ApocSiege")
public class FourHorsemen extends ApocSiege {

	public FourHorsemen() {
		super("Four Horsemen");
		this.addBoss("Death");
		this.addBoss("Famine");
		this.addBoss("Pestilence");
		this.addBoss("War");
		this.setBiome(Biome.HELL);
		this.setStorm(true);
		this.setThunder(true);
	}

}
