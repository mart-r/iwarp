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

public class RenameCommand extends AbstractSubCommand {
	private final String usage = "/iwarp rename <oldname> <newname>";
    private static final String NAME = "rename";
    private static final String PERMISSION = "iwarp.command.rename";
	
	public RenameCommand(IWarpPlugin plugin) {
        super(plugin, NAME, PERMISSION);
	}
//	/iwarp rename <oldname> <newname> - Rename a warp
//	If the player owns the warp, a warp with <newname> does not already exist, and the player has renamecost money, then the warp will be renamed to <newname> and renamecost will be removed from their balance.
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
		if (!sender.hasPermission("iwarp.command.rename")) {
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
		Player owner = (Player) sender;
		
		String oldName = args[1].toLowerCase();
		String newName = args[2].toLowerCase();
		
		WarpHandler wh = IW.getWarpHandler();
		Settings settings = IW.getSettings();
		
		// check ownership
		if (!wh.getWarpsOf(owner).contains(oldName)) {
			owner.sendMessage(settings.getNotYourWarpMessage(oldName));
			return true;
		}

		if (wh.isProhibitedName(sender, newName)) return true;
		
		// check money
		double price = settings.getRenameCost();
		if (!IW.getEcon().has(owner, price)) {
			owner.sendMessage(settings.getNotEnoughMoneyMessage(price));
			return true;
		}
		
		// rename
		if (wh.rename(oldName, newName)) {
			IW.getEcon().withdrawPlayer(owner, price);
			owner.sendMessage(settings.getRenamedWarpMessage(oldName, newName, price));
		} else {
			String msg = settings.getIssueWhileRenamingWarpMessage(oldName, newName);
			owner.sendMessage(msg);
			IW.getLogger().warning(msg);
		}
		return true;
	}

}
