package me.ford.iwarp.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.ford.iwarp.IWarpPlugin;
import me.ford.iwarp.ReactionRunnable;
import me.ford.iwarp.Settings;
import me.ford.iwarp.WarpHandler;
import me.ford.iwarp.listeners.ChatListener;

public class MoveCommand extends AbstractSubCommand {
	private final String usage = "/iwarp move <warpname>";
	
	public MoveCommand(IWarpPlugin plugin) {
		super(plugin);
	}
//	/iwarp move <warpname> - Move a warp to your current location
//	If the player has the permission iwarp.use, the player owns the warp, and the player has movecost money, then the warp will be moved to the current location of the player and movecost will be removed from their balance.
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return new ArrayList<>();
		}
		Player owner = (Player) sender;
		if (args.length == 2) {
			return StringUtil.copyPartialMatches(args[1], IW.getWarpHandler().getWarpsOf(owner), new ArrayList<>());
		}
		return new ArrayList<>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("iwarp.command.move")) {
			sender.sendMessage(IW.getSettings().getInsufficientPermissionsMessage());
			return true;
		}
		if (args.length < 2) {
			sender.sendMessage(usage);
			return true;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage(IW.getSettings().getSenderMustBePlayerMessage());
			return true;
		}
		final Player owner = (Player) sender;
		final String warpName = args[1].toLowerCase();
		
		final WarpHandler wh = IW.getWarpHandler();
		final Settings settings = IW.getSettings();
		// check ownership
		if (!wh.getWarpsOf(owner).contains(warpName)) {
			owner.sendMessage(settings.getNotYourWarpMessage(warpName));
			return true;
		}
		
		// check money
		final double price = settings.getMoveCost();
		if (!IW.getEcon().has(owner, price)) {
			owner.sendMessage(settings.getNotEnoughMoneyMessage(price));
			return true;
		}
		
		// move
		if (!settings.getConfirmMove()) {
			move(owner, wh, warpName, settings, price);
		} else {
			ReactionRunnable run = new ReactionRunnable() {
				@Override
				public void run() {
					move(owner, wh, warpName, settings, price);
				}
			};
			owner.sendMessage(settings.getMoveWarpConfirmMessage(warpName, price));
			IW.getServer().getPluginManager().registerEvents(new ChatListener(IW, owner.getUniqueId(), run), IW);
		}
		return true;
	}
	
	private void move(Player owner, WarpHandler wh, String warpName, Settings settings, double price) {
		if (wh.moveWarp(warpName, owner) || settings.getConfirmCreate()) {
			IW.getEcon().withdrawPlayer(owner, price);
			owner.sendMessage(settings.getMovedWarpMessage(warpName, price));
		} else {
			String msg = settings.getIssueWhileMovingWarpMessage(warpName);
			owner.sendMessage(msg);
			IW.getLogger().warning(msg);
		}
	}

}
