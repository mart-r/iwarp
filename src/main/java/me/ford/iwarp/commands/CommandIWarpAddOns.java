package me.ford.iwarp.commands;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import me.ford.iwarp.IWarpPlugin;
import me.ford.iwarp.addons.IWarpAddOn;
import me.ford.iwarp.addons.IWarpAddOnType;

public class CommandIWarpAddOns implements TabExecutor {
	private final IWarpPlugin IW;
	private final Map<String, IWarpAddOn> subCommands = new HashMap<>();
	
	public CommandIWarpAddOns(IWarpPlugin plugin) {
		IW = plugin;
		for (IWarpAddOnType type : IWarpAddOnType.values()) {
			IWarpAddOn addOn = IW.getAddOn(type);
			if (addOn != null) {
				for (String key : addOn.getCommands()) {
					subCommands.put(key.toLowerCase(), addOn);
				}
			}
		}
	}
	

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> list = new ArrayList<>();
		if (args.length == 1) {
			return StringUtil.copyPartialMatches(args[0], subCommands.keySet(), list);
		} else if (args.length >= 2) {
			String subCommand = args[0].toLowerCase();
			if (subCommands.containsKey(subCommand)) {
				return StringUtil.copyPartialMatches(args[1], subCommands.get(subCommand).optionsOnCommand(args.length), list);
			}
		}
		return list;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
			return false;
		}
		String subCommand = args[0].toLowerCase();
		if (!subCommands.containsKey(subCommand)) {
			return false;
		}
		return subCommands.get(subCommand).onCommand(sender, subCommand, args);
	}

}
