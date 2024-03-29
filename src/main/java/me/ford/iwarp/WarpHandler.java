package me.ford.iwarp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.ford.iwarp.addons.IWarpAddOnType;

public class WarpHandler {
	private static final long DAY_TIME = 24 * 60 * 60 * 1000; // in ms 
	private final IWarpPlugin IW;
	private final File file;
	private FileConfiguration config; // maps warp name to deletion time
	private final EssentialsHook essHook;
	
	public WarpHandler(IWarpPlugin plugin) throws ClassNotFoundException {
		IW = plugin;
		file = new File(IW.getDataFolder(), "iwarps.yml");
		config = YamlConfiguration.loadConfiguration(file);
		essHook = new EssentialsHook(IW);
		new BukkitRunnable() {

			@Override
			public void run() {
				List<String> deleted = checkAndTerminate();
				if (!deleted.isEmpty()) {
					IW.getLogger().info("Some iwarps expired: " + String.join(", ", deleted));
				}
			}
			
		}.runTaskTimer(IW, 5L, IW.getSettings().getCheckTicks()); // first check is relativel quickly in case we had downtime
	}
	
	public void reload() {
		config = YamlConfiguration.loadConfiguration(file);
	}
	
	private List<String> checkAndTerminate() {
		List<String> toDelete = new ArrayList<>();
		long ctime = System.currentTimeMillis();
		for (String name : config.getKeys(false)) {
			long time = config.getLong(name);
			if (time < ctime || !isWarp(name)) {
				toDelete.add(name);
			}
		}
		if (!toDelete.isEmpty()) {
			for (String name : toDelete) {
				deleteWarp(name, false);
			}
			save(); // save once
		}
		return toDelete;
	}
	
	private void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			IW.getLogger().warning("Unable to save warps file!");
		}
	}
	
	public boolean isWarp(String name) {
		name = name.toLowerCase();
		return config.contains(name) && essHook.findWarp(name) != null;
	}
	
	public boolean warpExists(String name) {
		name = name.toLowerCase();
		return essHook.findWarp(name) != null;
	}
	
	public boolean createWarp(String name, Player owner, int days) {
		name = name.toLowerCase();
		if (!setWarp(name, owner)) {
			return false;
		}
		long endTime = System.currentTimeMillis() + DAY_TIME * days;
		config.set(name, endTime);
		save();
		return true;
	}
	
	private boolean setWarp(String name, Player owner) {
		return essHook.createWarp(name, owner);
	}
	
	public boolean moveWarp(String name, Player owner) {
		return setWarp(name, owner);
	}
	
	public boolean rename(String oldName, String newName) {
		oldName = oldName.toLowerCase();
		newName = newName.toLowerCase();
		if (!isWarp(oldName) || isWarp(newName)) {
			return false;
		}
		if (essHook.rename(oldName, newName)) {
			config.set(newName, config.getLong(oldName));
			config.set(oldName, null);
			save();
			return true;
		}
		return false;
	}
	
	public boolean changeOwner(String name, Player newOwner) {
		return essHook.setWarpOwner(name, newOwner);
	}

	public void addTimeToWarp(String name, long time) {
		name = name.toLowerCase();
		if (!config.contains(name)) {
			return;
		}
		long cEnd = config.getLong(name);
		long nEnd = cEnd + time;
		config.set(name, nEnd);
		save();
	}
	
	public void addTimeToWarp(String name, int days) {
		name = name.toLowerCase();
		if (!config.contains(name)) {
			return;
		}
		long cEnd = config.getLong(name);
		long nEnd = cEnd + days * DAY_TIME;
		config.set(name, nEnd);
		save();
	}
	
	public void deleteWarp(String name) {
		deleteWarp(name, true);
	}
	
	public void deleteWarp(String name, boolean save) {
		name = name.toLowerCase();
		if (isWarp(name)) {
			UUID ownerId = essHook.getOwner(name);
			Location loc = essHook.getWarpLocation(name);
			if (IW.getSettings().isAddOnEnabled(IWarpAddOnType.OLDWARPLOCATIONLOGGER)) {
				IW.getOldLocationLogger().onWarpDeletion(name, loc);
			}
			essHook.deleteWarp(name);
			OfflinePlayer player = IW.getServer().getOfflinePlayer(ownerId);
			if (player != null && player.hasPlayedBefore()) {
				String playerName = player.getName();
				String coords = String.format("(%s, %3.2f, %3.2f, %3.2f)", loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
				for (String cmd : IW.getSettings().getCommandsOnWarpExpire()) {
					cmd = cmd.replace("{player}", playerName).replace("{name}", name).replace("{coords}", coords);
					try {
						IW.getServer().dispatchCommand(IW.getServer().getConsoleSender(), cmd);
					} catch (CommandException e) {
						IW.getLogger().log(Level.SEVERE, "Problem executing command on warp expiration.", e);
					}
				}
			}
		}
		if (config.contains(name)) {
			config.set(name,  null);
			if (save) save(); // only if necessary
		}
	}
	
	public List<String> getWarpsOf(OfflinePlayer owner) {
		return getWarpsOf(owner, true);
	}
	
	public List<String> getWarpsOf(OfflinePlayer owner, boolean all) {
		List<String> warps = essHook.getWarpsOf(owner);
		if (all) {
			return warps;
		}
		return warps.stream().filter((warp) -> isWarp(warp)).collect(Collectors.toList());
	}
	
	public Collection<String> getAllWarps() {
		return essHook.getAllWarps();
	}
	
	public int getTotalDays(String name) {
		name = name.toLowerCase();
		if (!config.contains(name)) {
			return 0;
		}
		long timeLeft = config.getLong(name) - System.currentTimeMillis();
		return (int) (timeLeft / DAY_TIME);
	}
	
	public long getTotalTime(String name) {
		name = name.toLowerCase();
		if (!config.contains(name)) {
			return System.currentTimeMillis();
		}
		return config.getLong(name);
	}
	
	public OfflinePlayer getOwner(String name) { // I'm allowing owners not managed through this plugin
		UUID owner = essHook.getOwner(name);
		return IW.getServer().getOfflinePlayer(owner);
	}

	public boolean isProhibitedName(CommandSender sender, String warpName) {
		if (warpName.contains(".")) {
			sender.sendMessage(IW.getSettings().getNameContainsPeriodMessage(warpName));
			return true;
		}

		try {
			if (!warpName.matches(IW.getSettings().getWarpNameFormat())) {
				sender.sendMessage(IW.getSettings().getNameDoesntMatchPatternMessage(warpName));
				return true;
			}
		} catch (PatternSyntaxException exception) {
			Bukkit.getLogger().warning("You have an error in the warp-name-format configuration setting. Until this error is fixed, iwarp will deny every warp name. You can reset it to \"^.{1,15}$\".");
			sender.sendMessage(IW.getSettings().getNameDoesntMatchPatternMessage(warpName));
			return true;
		}

		// handle warp existance
		if (warpExists(warpName)) {
			sender.sendMessage(IW.getSettings().getWarpExistsMessage(warpName));
			return true;
		}

		// integer name check
		try {
			Integer.parseInt(warpName);
			sender.sendMessage(IW.getSettings().getNameNotIntMessage());
			return true;
		} catch (NumberFormatException e) {
			/* continue */ }

		return false;
	}

}
