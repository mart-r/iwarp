package me.ford.iwarp.listeners;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.ford.iwarp.IWarpPlugin;
import me.ford.iwarp.ReactionRunnable;

public class ChatListener implements Listener {
	private final UUID target;
	private final ReactionRunnable run;
	private final IWarpPlugin IW;
	
	public ChatListener(IWarpPlugin plugin, UUID target, ReactionRunnable runnable) {
		IW = plugin;
		this.target = target;
		run = runnable;
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if (event.getPlayer().getUniqueId().equals(target)) { // message content doesn't matter
			event.setCancelled(true);
			run.runNow(IW);
			HandlerList.unregisterAll(this);
		}
	}

}
