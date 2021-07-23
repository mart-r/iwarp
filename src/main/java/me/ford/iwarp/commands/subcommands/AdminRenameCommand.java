package me.ford.iwarp.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import me.ford.iwarp.IWarpPlugin;
import me.ford.iwarp.Settings;
import me.ford.iwarp.WarpHandler;

/**
 * AdminRenameCommand
 */
public class AdminRenameCommand extends AbstractSubCommand {
    private final String usage = "/iwarp arename <oldname> <newname>";
    private static final String NAME = "arename";
    private static final String PERMISSION = "iwarp.command.adminrename";

    public AdminRenameCommand(IWarpPlugin plugin) {
        super(plugin, NAME, PERMISSION);
    }

    // /iwarp rename <oldname> <newname> - Rename a warp
    // If the player owns the warp, a warp with <newname> does not already exist,
    // and the player has renamecost money, then the warp will be renamed to
    // <newname> and renamecost will be removed from their balance.
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], IW.getWarpHandler().getAllWarps(), new ArrayList<>());
        }
        return new ArrayList<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("iwarp.command.adminrename")) {
            sender.sendMessage(IW.getSettings().getInsufficientPermissionsMessage());
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(usage);
            return true;
        }

        String oldName = args[1].toLowerCase();
        String newName = args[2].toLowerCase();

        WarpHandler wh = IW.getWarpHandler();
        Settings settings = IW.getSettings();

        // handle warp existance
        if (wh.isWarp(newName)) {
            sender.sendMessage(settings.getWarpExistsMessage(newName));
            return true;
        }

        // warp name check
        try {
            Integer.parseInt(newName);
            sender.sendMessage(settings.getNameNotIntMessage());
            return true;
        } catch (NumberFormatException e) {
            /* continue */ }

        double price = 0D;

        // rename
        if (wh.rename(oldName, newName)) {
            sender.sendMessage(settings.getRenamedWarpMessage(oldName, newName, price));
        } else {
            String msg = settings.getIssueWhileRenamingWarpMessage(oldName, newName);
            sender.sendMessage(msg);
            IW.getLogger().warning(msg);
        }
        return true;
    }

}