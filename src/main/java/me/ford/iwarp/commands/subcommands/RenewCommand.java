package me.ford.iwarp.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.ford.iwarp.IWarpPlugin;
import me.ford.iwarp.Settings;
import me.ford.iwarp.WarpHandler;

public class RenewCommand extends AbstractSubCommand {
	private final String usage = "/iwarp renew <warpname> <days>";
	
	public RenewCommand(IWarpPlugin plugin) {
		super(plugin);
	}
//	/iwarp renew <warpname> <days> - Renew a warp
//	If the user has <days>*renewcost money, add <days> days to the warp’s expiration and take renewcost*days amount of money from the user. This command can be used by people who do not own that warp.
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return new ArrayList<>();
		}
		if (args.length == 2) {
			return StringUtil.copyPartialMatches(args[1], IW.getWarpHandler().getAllWarps(), new ArrayList<>());
		}
		return new ArrayList<>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("iwarp.command.renew")) {
			sender.sendMessage(IW.getSettings().getInsufficientPermissionsMessage());
			return true;
		}
		if (args.length < 3) {
			sender.sendMessage(usage);
			return true;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage(IW.getSettings().getSenderMustBePlayerMessage());
			return true;
		}
		Player target = (Player) sender;
		
		String warpName = args[1].toLowerCase();
		int days; // parse number of days
		try {
			days = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			sender.sendMessage(usage);
			return true;
		}
		if (days < 1) {
			sender.sendMessage(usage);
			return true;
		}
		
		Settings settings = IW.getSettings();
		WarpHandler wh = IW.getWarpHandler();
		
		double price = days * settings.getRenewCost();
		
		// handle price
		if (!IW.getEcon().has(target, price)) {
			target.sendMessage(settings.getNotEnoughMoneyMessage(price));
			return true;
		}
		
		// warp existance
		if (!wh.isWarp(warpName)) {
			target.sendMessage(settings.getWarpNotFoundMessage(warpName));
			return true;
		}
		
		// renew warp
		wh.addTimeToWarp(warpName, days);
		IW.getEcon().withdrawPlayer(target, price);
		int total = wh.getTotalDays(warpName);
		target.sendMessage(settings.getRenewedWarpMessage(warpName, days, price, total));
		return true;
	}

}
