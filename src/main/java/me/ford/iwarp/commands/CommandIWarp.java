package me.ford.iwarp.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import me.ford.iwarp.IWarpPlugin;
import me.ford.iwarp.commands.subcommands.AbstractSubCommand;
import me.ford.iwarp.commands.subcommands.AdminRenameCommand;
import me.ford.iwarp.commands.subcommands.ChangeExpirationCommand;
import me.ford.iwarp.commands.subcommands.CreateCommand;
import me.ford.iwarp.commands.subcommands.InfoCommand;
import me.ford.iwarp.commands.subcommands.ListCommand;
import me.ford.iwarp.commands.subcommands.MoveCommand;
import me.ford.iwarp.commands.subcommands.ReloadCommand;
import me.ford.iwarp.commands.subcommands.RenameCommand;
import me.ford.iwarp.commands.subcommands.RenewCommand;
import me.ford.iwarp.commands.subcommands.TransferCommand;

public class CommandIWarp implements TabExecutor {
	private final IWarpPlugin IW;
	private final Map<String, AbstractSubCommand> subCommands = new HashMap<>();
	private final Set<String> subCommandNames = new HashSet<>();
	
	public CommandIWarp(IWarpPlugin plugin) {
		IW = plugin;
		subCommands.put("create", new CreateCommand(IW));
		subCommands.put("renew", new RenewCommand(IW));
		subCommands.put("move", new MoveCommand(IW));
		subCommands.put("rename", new RenameCommand(IW));
		subCommands.put("transfer", new TransferCommand(IW));
		subCommands.put("list", new ListCommand(IW));
		subCommands.put("info", new InfoCommand(IW));
		subCommands.put("reload", new ReloadCommand(IW));
		subCommands.put("changeexpiration", new ChangeExpirationCommand(IW));
		subCommands.put("arename", new AdminRenameCommand(IW));
		subCommandNames.addAll(subCommands.keySet());
		subCommandNames.remove("reload"); // most people can't reload
		subCommandNames.add("help"); // I can tabcomplete, but it just returns false and displays usage
		subCommandNames.remove("changeexpiration"); // most people can't use this
		subCommandNames.remove("arename"); // most people can't use this
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		switch (args.length) {
		case 0:
			return new ArrayList<>();
		case 1:
			return StringUtil.copyPartialMatches(args[0], subCommandNames, new ArrayList<>());
		default:
			AbstractSubCommand sbc = subCommands.get(args[0].toLowerCase());
			if (sbc == null) {
				return new ArrayList<>();
			}
			return sbc.onTabComplete(sender, command, label, args);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
			return false;
		}
		AbstractSubCommand sbc = subCommands.get(args[0]);
		if (sbc == null) {
			return false;
		}
		return sbc.onCommand(sender, command, label, args);
	}

}
