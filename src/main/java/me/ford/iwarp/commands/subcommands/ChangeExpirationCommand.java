package me.ford.iwarp.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import com.earth2me.essentials.utils.DateUtil;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import me.ford.iwarp.IWarpPlugin;
import me.ford.iwarp.Settings;
import me.ford.iwarp.WarpHandler;

/**
 * ChangeExpirationCommand /iwarp changeexpiration <warpname> <days> - Change
 * expiration
 */
public class ChangeExpirationCommand extends AbstractSubCommand {
    private final String usage = "/iwarp changeexpiration <warpname> <time>";
    private static final String NAME = "changeexpiration";
    private static final String PERMISSION = "iwarp.command.changeexpiration";

    public ChangeExpirationCommand(IWarpPlugin plugin) {
        super(plugin, NAME, PERMISSION);
    }

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
        if (!sender.hasPermission("iwarp.command.changeexpiration")) {
            sender.sendMessage(IW.getSettings().getInsufficientPermissionsMessage());
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(usage);
            return true;
        }

        final String warpName = args[1];

        long newTimeLeft;
        try {
            newTimeLeft = DateUtil.parseDateDiff(args[2], true);
        } catch (Exception e) {
            sender.sendMessage(usage);
            return true;
        }

		// helpers
		final WarpHandler wh = IW.getWarpHandler();
		final Settings settings = IW.getSettings();

		// handle warp existance
		if (!wh.warpExists(warpName)) {
			sender.sendMessage(settings.getWarpNotFoundMessage(warpName));
			return true;
		}

        // set new exipre time
        long curExpireTime = wh.getTotalTime(warpName);
        long addition = newTimeLeft - curExpireTime;
        wh.addTimeToWarp(warpName, addition);
        sender.sendMessage(settings.getChangedExpirationMessage(warpName, args[2]));
		return true;
    }

    
}