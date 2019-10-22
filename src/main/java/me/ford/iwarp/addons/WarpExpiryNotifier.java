package me.ford.iwarp.addons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.ford.iwarp.IWarpPlugin;
import net.ess3.api.events.UserWarpEvent;

public class WarpExpiryNotifier implements IWarpAddOn, Listener {
	private static final List<String> EMPTY = new ArrayList<>();
	private static final long DAY = 24 * 60 * 60 * 1000L;
	private final IWarpPlugin IW;
	
	public WarpExpiryNotifier(IWarpPlugin plugin) {
		IW = plugin;
		IW.getServer().getPluginManager().registerEvents(this, IW);
	}

	@Override
	public IWarpAddOnType getType() {
		return IWarpAddOnType.WARPEXPIRYNOTIFIER;
	}

	@Override
	public Collection<String> getCommands() {
		return EMPTY;
	}

	@Override
	public Collection<String> optionsOnCommand(int argnr) {
		return EMPTY;
	}

	@Override
	public boolean onCommand(CommandSender sender, String subCommand, String[] args) {
		return false;
	}
	
	@EventHandler
	public void onWarp(UserWarpEvent event) {
		// DEBUG
		IW.getLogger().info("WARPING:" + event.getUser().getName() + "->" + event.getWarp());
		// DEBUG
		if (IW.getWarpHandler().isWarp(event.getWarp())) {
			long time = IW.getWarpHandler().getTotalTime(event.getWarp());
			long timeLeft = time - System.currentTimeMillis();
			if (timeLeft < DAY * IW.getSettings().daysForExpiryNotification()) {
				if (IW.getSettings().notifyOnlyOwner() && !event.getUser().getBase().getUniqueId().equals(IW.getWarpHandler().getOwner(event.getWarp()).getUniqueId())) {
					return;
				}
				event.getUser().sendMessage(IW.getSettings().getWarpExpiringMessage(event.getWarp(), time));;
			}
		}
	}

}
