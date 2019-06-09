package me.ford.iwarp.commands.subcommands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ford.iwarp.IWarpPlugin;

public class ListCommand extends AbstractSubCommand {
	
	public ListCommand(IWarpPlugin plugin) {
		super(plugin);
	}
//	/iwarp list <playername> - List all warps owned by a player
//
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return null; // should default to online players' names
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("iwarp.command.list")) {
			sender.sendMessage(IW.getSettings().getInsufficientPermissionsMessage());
			return true;
		}
		Player target = null;
		if (args.length == 1 && sender instanceof Player) {
			target = (Player) sender;
		}
		String targetPlayerName = "N/A";
		if (args.length > 1) {
			targetPlayerName = args[1];
			target = IW.getServer().getPlayer(targetPlayerName);
		}
		if (target == null) {
			sender.sendMessage(IW.getSettings().getPlayerNotFoundMessage(targetPlayerName));
			return true;
		}
		
		List<String> warps = IW.getWarpHandler().getWarpsOf(target);
		sender.sendMessage(IW.getSettings().getListWarpsMessage(target, warps));
		return true;
	}

}
