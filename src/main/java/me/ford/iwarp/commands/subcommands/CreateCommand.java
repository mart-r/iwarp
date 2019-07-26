package me.ford.iwarp.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ford.iwarp.IWarpPlugin;
import me.ford.iwarp.ReactionRunnable;
import me.ford.iwarp.Settings;
import me.ford.iwarp.WarpHandler;
import me.ford.iwarp.listeners.ChatListener;

public class CreateCommand extends AbstractSubCommand {
	private final String usage = "/iwarp create <warpname> <days>";
	
	public CreateCommand(IWarpPlugin plugin) {
		super(plugin);
	}

//	/iwarp create <warpname> <days> - Create a warp
//	If the player has the permission iwarp.use, a warp with <warpname> does not already exist, and the player has createcost + (renewcost * days) money, a warp will be created. Then, createcost + (renewcost * days) will be removed from their balance.
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return new ArrayList<>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("iwarp.command.create")) {
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
		final Player player = (Player) sender;
		
		final String warpName = args[1];
		
		final int days; // parse number of days
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
		
		// helpers
		final WarpHandler wh = IW.getWarpHandler();
		final Settings settings = IW.getSettings();
		
		// handle warp existance
		if (wh.warpExists(warpName)) {
			sender.sendMessage(settings.getWarpExistsMessage(warpName));
			return true;
		}
		
		// warp name check
		try {
			Integer.parseInt(warpName);
			player.sendMessage(settings.getNameNotIntMessage());
			return true;
		} catch (NumberFormatException e) {	/* continue */ }
		
		// handle price
		final double price = settings.getCreateCost() + settings.getRenewCost() * days;
		if (!IW.getEcon().has(player, price)) {
			player.sendMessage(settings.getNotEnoughMoneyMessage(price));
			return true;
		}
		
		// create warp
		if (!IW.getSettings().getConfirmCreate()) {
			create(player, wh, warpName, settings, days, price);
		} else {
			ReactionRunnable run = new ReactionRunnable() {
				@Override
				public void run() {
					create(player, wh, warpName, settings, days, price);
				}
			};
			player.sendMessage(settings.getCreateWarpConfirmMessage(warpName, price));
			IW.getServer().getPluginManager().registerEvents(new ChatListener(IW, player.getUniqueId(), run), IW);
			return true;
		}
		return true;
	}
	
	private void create(Player player, WarpHandler wh, String warpName, Settings settings, int days, double price) {
		if (wh.createWarp(warpName, player, days)) {
			IW.getEcon().withdrawPlayer(player, price);
			player.sendMessage(settings.getCreatedWarpMessage(warpName, days, price));
		} else {
			String msg = settings.getIssueWhileCreatingWarpMessage(warpName);
			player.sendMessage(msg);
			IW.getLogger().warning(msg);
		}
	}

}
