package me.ford.iwarp.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

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
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		switch (args.length) {
			case 0:
				return new ArrayList<>();
			case 1:
				ArrayList<String> list = new ArrayList<>();
				if (sender.hasPermission("iwarp.command.create")) {
					list.add("create");
				}
				if (sender.hasPermission("iwarp.command.renew")) {
					list.add("renew");
				}
				if (sender.hasPermission("iwarp.command.move")) {
					list.add("move");
				}
				if (sender.hasPermission("iwarp.command.rename")) {
					list.add("rename");
				}
				if (sender.hasPermission("iwarp.command.transfer")) {
					list.add("transfer");
				}
				if (sender.hasPermission("iwarp.command.list")) {
					list.add("list");
				}
				if (sender.hasPermission("iwarp.command.info")) {
					list.add("info");
				}
				if (sender.hasPermission("iwarp.command")) {
					list.add("help");
				}
				if (sender.hasPermission("iwarp.command.changeexpiration")) {
					list.add("changeexpiration");
				}
				if (sender.hasPermission("iwarp.command.adminrename")) {
					list.add("arename");
				}
				return list;
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
