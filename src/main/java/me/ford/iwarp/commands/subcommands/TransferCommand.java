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

public class TransferCommand extends AbstractSubCommand {
	private final String usage = "/iwarp transfer <warpname> <newowner>";
	
	public TransferCommand(IWarpPlugin plugin) {
		super(plugin);
	}
//	/iwarp transfer <warpname> <newowner> - Transfer ownership of a warp
//	If the player owns the warp and the player has transfercost money, the warp will be transferred to the new owner. Then, transfercost will be removed from their balance.
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return new ArrayList<>();
		}
		Player owner = (Player) sender;
		if (args.length == 2) {
			return StringUtil.copyPartialMatches(args[1], IW.getWarpHandler().getWarpsOf(owner), new ArrayList<>());
		} else if (args.length == 3) {
			return null; // defaults to online players I believe
		}
		return new ArrayList<>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("iwarp.command.transfer")) {
			sender.sendMessage(IW.getSettings().getInsufficientPermissionsMessage());
			return true;
		}
		if (args.length < 3) {
			sender.sendMessage(usage);
			return true;
		}
		if (!(sender instanceof Player)) { // otherwise there is no ownership
			sender.sendMessage(IW.getSettings().getSenderMustBePlayerMessage());
			return true;
		}
		Player oldOwner = (Player) sender;
		
		String warpName = args[1].toLowerCase();
		String newOwnerName = args[2].toLowerCase();
		
		WarpHandler wh = IW.getWarpHandler();
		Settings settings = IW.getSettings();
		
		// check ownership
		if (!wh.getWarpsOf(oldOwner).contains(warpName)) {
			oldOwner.sendMessage(settings.getNotYourWarpMessage(warpName));
			return true;
		}
		
		// handle new owner
		@SuppressWarnings("deprecation")
		Player newOwner = IW.getServer().getPlayer(newOwnerName);
		if (newOwner == null) {
			oldOwner.sendMessage(IW.getSettings().getPlayerNotFoundMessage(newOwnerName));
			return true;
		}
		
		// check money
		double price = settings.getTransferCost();
		if (!IW.getEcon().has(oldOwner, price)) {
			oldOwner.sendMessage(settings.getNotEnoughMoneyMessage(price));
			return true;
		}
		
		// transfer
		if (wh.changeOwner(warpName, newOwner)) {
			IW.getEcon().withdrawPlayer(oldOwner, price);
			oldOwner.sendMessage(settings.getTransferredWarpMessage(warpName, newOwner, price));
		} else {
			String msg = settings.getIssueWhileTransferringWarpMessage(warpName, newOwner);
			oldOwner.sendMessage(msg);
			IW.getLogger().warning(msg);
		}
		return true;
	}

}
