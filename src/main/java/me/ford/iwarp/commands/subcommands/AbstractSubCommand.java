package me.ford.iwarp.commands.subcommands;

import org.bukkit.command.TabExecutor;

import me.ford.iwarp.IWarpPlugin;

public abstract class AbstractSubCommand implements TabExecutor {
	protected final IWarpPlugin IW;
	
	public AbstractSubCommand(IWarpPlugin plugin) {
		IW = plugin;
	}
}
