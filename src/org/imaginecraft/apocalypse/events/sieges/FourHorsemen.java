package org.imaginecraft.apocalypse.events.sieges;

import org.bukkit.block.Biome;
import org.bukkit.configuration.serialization.SerializableAs;
import org.imaginecraft.apocalypse.events.ApocBoss;
import org.imaginecraft.apocalypse.events.ApocSiege;

@SerializableAs("ApocSiege")
public class FourHorsemen extends ApocSiege {

	public FourHorsemen() {
		super("Four Horsemen");
		this.addBoss(ApocBoss.getBoss("Death"));
		this.addBoss(ApocBoss.getBoss("Famine"));
		this.addBoss(ApocBoss.getBoss("Pestilence"));
		this.addBoss(ApocBoss.getBoss("War"));
		this.setBiome(Biome.HELL);
		this.setStorm(true);
		this.setThunder(true);
	}

}
