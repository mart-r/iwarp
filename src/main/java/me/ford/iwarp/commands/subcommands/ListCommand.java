package me.ford.iwarp.commands.subcommands;

import java.util.List;

import org.bukkit.OfflinePlayer;
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
	public List<String> onTabComplete(final CommandSender sender, Command command, String label, String[] args) {
		return null; // should default to online players' names
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("iwarp.command.list")) {
			sender.sendMessage(IW.getSettings().getInsufficientPermissionsMessage());
			return true;
		}
		OfflinePlayer target = null;
		if (args.length == 1 && sender instanceof Player) {
			target = (Player) sender;
		}
		final String targetPlayerName;
		if (args.length > 1) {
			targetPlayerName = args[1];
			target = IW.getServer().getPlayer(targetPlayerName);
			if (target == null && IW.getSettings().doOfflinePlayerLookup()) {
				IW.getServer().getScheduler().runTaskAsynchronously(IW, () -> {
					OfflinePlayer otarget = IW.getServer().getOfflinePlayer(targetPlayerName);
					final OfflinePlayer ftarget = otarget.hasPlayedBefore() ? otarget : null;
					IW.getServer().getScheduler().runTask(IW, () -> dealWithTarget(sender, ftarget, targetPlayerName));
				});
				return true;
			}
		} else {
			targetPlayerName = "N/A";
		}
		dealWithTarget(sender, target, targetPlayerName);
		return true;
	}
	
	private void dealWithTarget(CommandSender sender, OfflinePlayer target, String targetPlayerName) {
		if (target == null) {
			sender.sendMessage(IW.getSettings().getPlayerNotFoundMessage(targetPlayerName));
			return;
		}
		
		List<String> warps = IW.getWarpHandler().getWarpsOf(target);
		sender.sendMessage(IW.getSettings().getListWarpsMessage(target, warps));
	}

}
