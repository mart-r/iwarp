package me.ford.iwarp.addons;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.ford.iwarp.IWarpPlugin;

public class OldWarpLocationLogger implements IWarpAddOn {
	private final IWarpPlugin IW;
	private final File file;
	private FileConfiguration config;
	private final String usage = "/iwa prevloc <warpname>";
	private final List<String> commands = new ArrayList<>();
	
	public OldWarpLocationLogger(IWarpPlugin plugin) {
		IW = plugin;
		file = new File(IW.getDataFolder(), "oldwarps.yml");
		config = YamlConfiguration.loadConfiguration(file);
		commands.add("prevloc");
	}
	
	public void onWarpDeletion(String name, Location loc) {
		name = name.toLowerCase();
		config.set(name + "." + System.currentTimeMillis(), loc);
		save();
	}
	
	public Map<Long, Location> getExpiredLocations(String name) {
		name = name.toLowerCase();
		Map<Long, Location> map = new HashMap<>();
		if (config.isConfigurationSection(name)) {
			ConfigurationSection section = config.getConfigurationSection(name);
			for (String time : section.getKeys(false)) {
				long t = Long.parseLong(time);
				Location loc;
				try {
					loc = (Location) section.get(time);
				} catch (ClassCastException e) {
					IW.getLogger().info("Unable to cast location to time for warp " + name + " and time " + time);
					continue;
				}
				map.put(t, loc);
			}
		}	
		return map;
	}
	
	public void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			IW.getLogger().warning("Unable to save oldwarps.yml!");
		}
	}

	@Override
	public IWarpAddOnType getType() {
		return IWarpAddOnType.OLDWARPLOCATIONLOGGER;
	}

	@Override
	public Collection<String> getCommands() {
		return commands;
	}

	@Override
	public Collection<String> optionsOnCommand(int argnr) {
		if (argnr == 2) {
			return config.getKeys(false);
		}
		return new ArrayList<>();
	}

	@Override
	public boolean onCommand(CommandSender sender, String subCommand, String[] args) {
		// only 'prevloc' for now
		if (args.length < 2) {
			sender.sendMessage(usage);
			return true;
		}
		String name = args[1];
		Map<Long, Location> locs = getExpiredLocations(name);
		if (locs.isEmpty()) {
			sender.sendMessage(IW.getSettings().getNoPreviousLocationsMessage(name));
		} else {
			sender.sendMessage(IW.getSettings().getPreviousLocationsMessage(name, locs));
		}
		return true;
	}

}
