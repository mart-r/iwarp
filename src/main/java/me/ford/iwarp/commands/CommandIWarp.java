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
		registerCommand(new CreateCommand(IW));
		registerCommand(new RenewCommand(IW));
		registerCommand(new MoveCommand(IW));
		registerCommand(new RenameCommand(IW));
		registerCommand(new TransferCommand(IW));
		registerCommand(new ListCommand(IW));
		registerCommand(new InfoCommand(IW));
		registerCommand(new ReloadCommand(IW));
		registerCommand(new ChangeExpirationCommand(IW));
		registerCommand(new AdminRenameCommand(IW));
	}

	private void registerCommand(AbstractSubCommand cmd) {
		subCommands.put(cmd.getName(), cmd);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		switch (args.length) {
			case 0:
				return new ArrayList<>();
			case 1:
				ArrayList<String> list = new ArrayList<>();
				for (AbstractSubCommand cmd : subCommands.values()) {
					if (cmd.hasPermission(sender)) {
						list.add(cmd.getName());
					}
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
