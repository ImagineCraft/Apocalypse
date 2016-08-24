package org.imaginecraft.apocalypse.teams;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("ApocTeam")
public class TestTeam extends ApocTeam {

	public TestTeam() {
		super(ChatColor.GRAY, "Test");
	}

}
