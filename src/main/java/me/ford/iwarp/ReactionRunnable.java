package me.ford.iwarp;

import org.bukkit.Bukkit;

public interface ReactionRunnable extends Runnable {
	
	public default void runNow(IWarpPlugin plugin) {
		Bukkit.getScheduler().runTask(plugin, this);
	}

}
