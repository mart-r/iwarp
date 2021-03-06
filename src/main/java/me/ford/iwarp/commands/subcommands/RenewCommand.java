package me.ford.iwarp.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.ford.iwarp.IWarpPlugin;
import me.ford.iwarp.Settings;
import me.ford.iwarp.WarpHandler;

public class RenewCommand extends AbstractSubCommand {
	private final String usage = "/iwarp renew <warpname> <days>";
    private static final String NAME = "renew";
    private static final String PERMISSION = "iwarp.command.renew";
	
	public RenewCommand(IWarpPlugin plugin) {
        super(plugin, NAME, PERMISSION);
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
		if (!settings.getConfirmRenew()) {
			renew(wh, warpName, days, target, price, settings);
		} else {
			ConversationFactory factory = new ConversationFactory(IW);
			factory.withFirstPrompt(new RenewPrompt(target, wh, warpName, days, settings, price))
				   .withTimeout(30).buildConversation(target).begin();
		}
		return true;
	}

	private void renew(WarpHandler wh, String warpName, int days, Player target, double price, Settings settings) {
		wh.addTimeToWarp(warpName, days);
		IW.getEcon().withdrawPlayer(target, price);
		int total = wh.getTotalDays(warpName);
		target.sendMessage(settings.getRenewedWarpMessage(warpName, days, price, total));
	}

	private class RenewPrompt extends StringPrompt {
		private final Player player;
		private final WarpHandler wh;
		private final String warpName;
		private final int days;
		private final Settings settings;
		private final double price;

		private RenewPrompt(Player player, WarpHandler wh, String warpName, int days, Settings settings, double price) {
			this.player = player;
			this.wh = wh;
			this.warpName = warpName;
			this.days = days;
			this.settings = settings;
			this.price = price;
		}

		@Override
		public String getPromptText(ConversationContext context) {
			return settings.getRenewWarpConfirmMessage(warpName, days, price);
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String input) {
			if ("confirm".equalsIgnoreCase(input)) {
				renew(wh, warpName, days, player, price, settings);
				int total = wh.getTotalDays(warpName);
				return new DonePrompt(settings.getRenewedWarpMessage(warpName, days, price, total));
			}
			return Prompt.END_OF_CONVERSATION;
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
