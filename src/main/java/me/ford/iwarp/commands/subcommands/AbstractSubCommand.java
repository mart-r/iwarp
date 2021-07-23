package me.ford.iwarp.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import me.ford.iwarp.IWarpPlugin;

public abstract class AbstractSubCommand implements TabExecutor {
	private final String name;
	private final String permission;
	protected final IWarpPlugin IW;

	public AbstractSubCommand(IWarpPlugin plugin, String name, String permission) {
		IW = plugin;
		this.name = name;
		this.permission = permission;
	}

	public String getName() {
		return name;
	}

	public String getPermission() {
		return permission;
	}

	public boolean hasPermission(CommandSender sender) {
		return sender.hasPermission(permission);
	}

}
