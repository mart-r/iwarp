package me.ford.iwarp.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.ford.iwarp.IWarpPlugin;

public class ReloadCommand extends AbstractSubCommand {

	public ReloadCommand(IWarpPlugin plugin) {
		super(plugin);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return new ArrayList<>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("iwarp.command.reload")) {
			sender.sendMessage(IW.getSettings().getInsufficientPermissionsMessage());
			return true;
		}
		IW.reload();
		sender.sendMessage(IW.getSettings().getReloadedMessage());
		return true;
	}

}
