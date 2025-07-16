package me.ford.iwarp.commands.subcommands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import me.ford.iwarp.IWarpPlugin;
import me.ford.iwarp.Settings;
import me.ford.iwarp.WarpHandler;
import me.ford.iwarp.addons.WarpLimiter;

public class CreateCommand extends AbstractSubCommand {
	private final String usage = "/iwarp create <warpname> <days>";
    private static final String NAME = "create";
    private static final String PERMISSION = "iwarp.command.create";

	public CreateCommand(IWarpPlugin plugin) {
        super(plugin, NAME, PERMISSION);
	}

	// /iwarp create <warpname> <days> - Create a warp
	// If the player has the permission iwarp.use, a warp with <warpname> does not
	// already exist, and the player has createcost + (renewcost * days) money, a
	// warp will be created. Then, createcost + (renewcost * days) will be removed
	// from their balance.
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
		if (args.length == 1) {
			sender.sendMessage(usage);
			return true;
		}

		// Calculate number of days. If no value, assume 1 day.
		final int days;
		if (args.length == 2) {
			days = 1;
		} else {
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
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage(IW.getSettings().getSenderMustBePlayerMessage());
			return true;
		}
		final Player player = (Player) sender;

		final String warpName = args[1];
		final WarpHandler wh = IW.getWarpHandler();
		if (wh.isProhibitedName(sender, warpName)) return true;

		try {
			if (!warpName.matches(IW.getSettings().getWarpNameFormat())) {
				sender.sendMessage(IW.getSettings().getNameDoesntMatchPatternMessage(warpName));
				return true;
			}
		} catch (PatternSyntaxException exception) {
			Bukkit.getLogger().warning("You have an error in the warp-name-format configuration setting. Until this error is fixed, iwarp will allow any warp name. You can reset it to \"^.{1,15}$\".");
		}

		// helpers
		final Settings settings = IW.getSettings();
		final WarpLimiter limiter = IW.getWarpLimiter();

		// handle limits
		int cur, max;
		if (limiter != null && (max = limiter.getAllowedWarps(player)) <= (cur = wh.getWarpsOf(player).size())) {
			player.sendMessage(settings.getTooManyWarpsMessage(cur, max));
			return true;
		}

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
			ConversationFactory factory = new ConversationFactory(IW);
			factory.withFirstPrompt(new CreatePrompt(player, wh, warpName, settings, days, price))
				   .withTimeout(30).buildConversation(player).begin();
			return true;
		}
		return true;
	}
	
	private boolean create(Player player, WarpHandler wh, String warpName, Settings settings, int days, double price) {
		if (wh.createWarp(warpName, player, days)) {
			IW.getEcon().withdrawPlayer(player, price);
			player.sendMessage(settings.getCreatedWarpMessage(warpName, days, price));
			return true;
		} else {
			String msg = settings.getIssueWhileCreatingWarpMessage(warpName);
			player.sendMessage(msg);
			IW.getLogger().warning(msg);
			return false;
		}
	}

	private class CreatePrompt extends StringPrompt {
		private final Player player;
		private final WarpHandler wh;
		private final String warpName;
		private final Settings settings;
		private final int days;
		private final double price;

		private CreatePrompt(Player player, WarpHandler wh, String warpName, Settings settings, int days, double price) {
			this.player = player;
			this.wh = wh;
			this.warpName = warpName;
			this.settings = settings;
			this.days = days;
			this.price = price;
		}
		

		@Override
		public String getPromptText(ConversationContext context) {
			return settings.getCreateWarpConfirmMessage(warpName, price);
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String input) {
			if (!"confirm".equalsIgnoreCase(input)) {
				return new DonePrompt(settings.getCanceledCreatingWarpMessage());
			} else if (create(player, wh, warpName, settings, days, price)) {
				return new DonePrompt(settings.getCreatedWarpMessage(warpName, days, price));
			} else {
				String msg = settings.getIssueWhileCreatingWarpMessage(warpName);
				IW.getLogger().warning(msg);
				return new DonePrompt(msg);
			}
		}

	}

	private class DonePrompt extends MessagePrompt {
		private final String msg;

		public DonePrompt(String msg) {
			this.msg = msg;
		}

		@Override
		public String getPromptText(ConversationContext context) {
			return msg;
		}

		@Override
		protected Prompt getNextPrompt(ConversationContext context) {
			return Prompt.END_OF_CONVERSATION;
		}
		
	}

}
