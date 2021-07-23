package me.ford.iwarp.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import me.ford.iwarp.IWarpPlugin;

public class InfoCommand extends AbstractSubCommand {
	private final String usage = "/iwarp info <warpname>";
    private static final String NAME = "info";
    private static final String PERMISSION = "iwarp.command.info";
	
	public InfoCommand(IWarpPlugin plugin) {
        super(plugin, NAME, PERMISSION);
	}
//	/iwarp info <warpname> - Show information about a warp
//	Shows who owns a warp and when it will expire.
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> list = new ArrayList<>();
		if (args.length == 2) {
			return StringUtil.copyPartialMatches(args[1], IW.getWarpHandler().getAllWarps(), list);
		}
		return list;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("iwarp.command.info")) {
			sender.sendMessage(IW.getSettings().getInsufficientPermissionsMessage());
			return true;
		}
		if (args.length < 2) {
			sender.sendMessage(usage);
			return true;
		}
		String warpName = args[1];
		if (!IW.getWarpHandler().isWarp(warpName)) {
			sender.sendMessage(IW.getSettings().getWarpNotFoundMessage(warpName));
			return true;
		}
		long timeLeft = IW.getWarpHandler().getTotalTime(warpName);
		OfflinePlayer owner = IW.getWarpHandler().getOwner(warpName);
		sender.sendMessage(IW.getSettings().getWarpInfoMessage(warpName, owner, timeLeft));
		return true;
	}

}
