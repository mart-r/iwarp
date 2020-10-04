package me.ford.iwarp;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import com.earth2me.essentials.utils.DateUtil;

import me.ford.iwarp.addons.IWarpAddOnType;

public class Settings {
	private final IWarpPlugin IW;
	
	public Settings(IWarpPlugin plugin) {
		IW = plugin;
	}
	
	// config entries
	
	public boolean getConfirmCreate() {
		return IW.getConfig().getBoolean("confirm.create", false);
	}
	
	public boolean getConfirmMove() {
		return IW.getConfig().getBoolean("confirm.move", false);
	}

	public boolean getConfirmRenew() {
		return IW.getConfig().getBoolean("confirm.renew", false);
	}

	public double getCreateCost() {
		return IW.getConfig().getDouble("createcost", 0);
	}
	
	public double getRenewCost() {
		return IW.getConfig().getDouble("renewcost", 0);
	}
	
	public double getMoveCost() {
		return IW.getConfig().getDouble("movecost", 0);
	}
	
	public double getRenameCost() {
		return IW.getConfig().getDouble("renamecost", 0);
	}
	
	public double getTransferCost() {
		return IW.getConfig().getDouble("transfercost", 0);
	}
	
	public int getCheckTicks() {
		return IW.getConfig().getInt("check-delay-ticks", 200);
	}
	
	public boolean doOfflinePlayerLookup() {
		return IW.getConfig().getBoolean("lookup-offline-players", true);
	}
	
	public boolean listEssentialsWarps() {
		return IW.getConfig().getBoolean("include-essentials-warps-in-list", false);
	}

	public List<String> getCommandsOnWarpExpire() {
		return IW.getConfig().getStringList("commands-on-warp-expire");
	}

	public boolean useBstats() {
		return IW.getConfig().getBoolean("use-bstats", true);
	}
	
	// addons
	
	public boolean isAddOnEnabled(IWarpAddOnType type) {
		switch (type) {
		case OLDWARPLOCATIONLOGGER:
			return IW.getConfig().getBoolean("addons.save-expired-warp-locations", false);
		case WARPEXPIRYNOTIFIER:
			return IW.getConfig().getBoolean("addons.warp-expiry-notifier.enabled", false);
		case WARPLIMITER:
		    return IW.getConfig().getBoolean("addons.warp-limiter.enabled", false);
		default:
			return false;	
		}
	}
	
	public int daysForExpiryNotification() {
		return IW.getConfig().getInt("addons.warp-expiry-notifier.days-for-notification", 1);
	}
	
	public boolean notifyOnlyOwner() {
		return IW.getConfig().getBoolean("addons.warp-expiry-notifier.only-owner", false);
	}
	
	// messages

	public String getTooManyWarpsMessage(int cur, int max) {
		String msg = getMessage("have-max-warps", "&7You have &6{cur}&7 out of &8{max}&7 allowed warps");
		return msg.replace("{cur}", String.valueOf(cur)).replace("{max}", String.valueOf(max));
	}
	
	public String getWarpExistsMessage(String warpName) {
		String msg = getMessage("warp-already-exists", "&cWarp already exists: &6{name}");
		return msg.replace("{name}", warpName);
	}
	
	public String getSenderMustBePlayerMessage() {
		return getMessage("sender-must-be-player", "&cSender must be a player!");
	}

	public String getNameContainsPeriodMessage(String name) {
		return getMessage("name-cannot-have-period",
				"%cThe name of the warp %6{name}&c cointains a period (&7.&c) which is not allowed");
	}
	
	public String getNotEnoughMoneyMessage(double price) {
		String msg = getMessage("not-enough-money", "&cYou do not have enough money: &4{amount}");
		return msg.replace("{amount}", Utils.doubleFormat(price));
	}
	
	public String getCreatedWarpMessage(String warpName, int days, double price) {
		String msg = getMessage("created-warp", "&7You've successfully created the warp &6{name}&7 for &8{days}&7 days for &6{amount}&7.");
		return msg.replace("{name}", warpName).replace("{days}", String.valueOf(days)).replace("{amount}", Utils.doubleFormat(price));
	}
	
	public String getCreateWarpConfirmMessage(String name, double price) {
		String msg = getMessage("confirm-create", "&7Type 'confirm' to confirm the location for warp &7{name}&7. The cost is &8{amount}&7.").replace("{name}", name);
		return msg.replace("{name}", name).replace("{amount}", Utils.doubleFormat(price));
	}
	
	public String getNotYourWarpMessage(String name) {
		return getMessage("not-your-warp", "&cThis warp does not belong to you: &7{name}").replace("{name}", name);
	}
	
	public String getRenewedWarpMessage(String warpName, int days, double amount, int total) {
		String msg = getMessage("renewed-warp", "&7You've successfully renewed the warp &6{name}&7 for another &8{days}&7 days for $&8{amount}&7 (&6{total}&7 days left in total)");
		return msg.replace("{name}", warpName).replace("{days}", String.valueOf(days)).replace("{amount}", Utils.doubleFormat(amount)).replace("{total}", String.valueOf(total));
	}
	
	public String getIssueWhileCreatingWarpMessage(String warpName) {
		String msg = getMessage("issue-while-creating-warp", "&cThere was an unexpected issue while creating warp: &4{name}");
		return msg.replace("{name}", warpName);
	}
	
	public String getNameNotIntMessage() {
		return getMessage("name-not-int", "&cWarp names cannot be integers!");
	}
	
	public String getInsufficientPermissionsMessage() {
		return getMessage("inssufficient-permissions", "&cYou do not have permission to use this command!");
	}
	
	public String getMovedWarpMessage(String warpName, double price) {
		String msg = getMessage("moved-warp", "&7You've successfully moved the warp &6{name}&7 to your current location for &8{amount}");
		return msg.replace("{name}", warpName).replace("{amount}", Utils.doubleFormat(price));
	}
	
	public String getMoveWarpConfirmMessage(String warpName, double price) {
		String msg = getMessage("confirm-move", "&7Type 'confirm' to confirm the new location for warp &6{name}&7. The cost is &8{amount}&7.");
		return msg.replace("{name}", warpName).replace("{amount}", Utils.doubleFormat(price));
	}

	public String getRenewWarpConfirmMessage(String warpName, int days, double price) {
		String msg = getMessage("confirm-renew", "&7Type 'confirm' to confirm the renewal for warp &6{name}&7 for &8{days}&7 days. The cost is &8{amount}&7.");
		return msg.replace("{name}", warpName).replace("{days}", String.valueOf(days)).replace("{amount}", Utils.doubleFormat(price));
	}
	
	public String getIssueWhileMovingWarpMessage(String warpName) {
		String msg = getMessage("issue-while-moving-warp", "&cThere was an unexpected issue while creating warp: &4{name}");
		return msg.replace("{name}", warpName);
	}
	
	public String getRenamedWarpMessage(String oldName, String newName, double price) {
		String msg = getMessage("renamed-warp", "&7You've successfully renamed the warp &8{old}&7 to &6{new}&7 for &8{amount}");
		return msg.replace("{old}", oldName).replace("{new}", newName).replace("{amount}", Utils.doubleFormat(price));
	}
	
	public String getIssueWhileRenamingWarpMessage(String oldName, String newName) {
		String msg = getMessage("issue-with-renaming-warp", "&cThere was an unexpected issue while renaming warp &4{old}&c to &c{new}");
		return msg.replace("{old}", oldName).replace("{new}", newName);
	}
	
	public String getPlayerNotFoundMessage(String name) {
		String msg = getMessage("player-not-found", "&cUnable to find online player: &4{player}");
		return msg.replace("{player}", name);
	}
	
	public String getTransferredWarpMessage(String name, Player to, double price) { 
		String msg = getMessage("transferred-warp", "&7Successfully transferred the warp &6{name}&7 to &8{player}&7 for &6{amount}");
		return msg.replace("{name}", name).replace("{player}", to.getName()).replace("{amount}", Utils.doubleFormat(price));
	}
	
	public String getIssueWhileTransferringWarpMessage(String name, Player to) {
		String msg = getMessage("issue-with-transferring-warp", "&cUnexpected issue while transferred the warp &6{name}&7 to &8{player}");
		return msg.replace("{name}", name).replace("{player}", to.getName());
	}
	
	public String getListWarpsMessage(OfflinePlayer owner, List<String> warps) {
		String msg = getMessage("list-warps", "&6{player}&7 has the following warps: &8{warps}");
		return msg.replace("{player}", owner.getName()).replace("{warps}", String.join(", ", warps));
	}
	
	public String getWarpNotFoundMessage(String name) {
		String msg = getMessage("warp-not-found", "&cWarp not found: &c{name}");
		return msg.replace("{name}", name);
	}
	
	public String getWarpInfoMessage(String name, OfflinePlayer owner, long timeLeft) {
		String msg = getMessage("warp-info", "&7The warp &6{name}&7 is owned by &8{player}&7 and will expire in &6{time}");
		return msg.replace("{name}", name).replace("{player}", owner.getName()).replace("{time}", DateUtil.formatDateDiff(timeLeft));
	}
	
	public String getReloadedMessage() {
		return getMessage("reloaded", "&cSuccessfully loaded config and iwarps!");
	}
	
	public String getNoPreviousLocationsMessage(String name) {
		return getMessage("no-previous-locations", "&7No previous locations for warp &6{name}").replace("{name}", name);
	}
	
	public String getPreviousLocationsMessage(String name, Map<Long, Location> locs) {
		String msg = getMessage("previous-locations", "&7Previous locations for warp &6{name}&7: &8{locs}");
		String res = "";
		for (Entry<Long, Location> entry : locs.entrySet()) {
			Location loc = entry.getValue();
			if (res.length() > 0) {
				res += ", ";
			}
			res += DateUtil.formatDateDiff(entry.getKey()) + ": " + String.format("(%s)%s,%s,%s", 
					loc.getWorld().getName(), Utils.doubleFormat(loc.getX()), Utils.doubleFormat(loc.getY()), Utils.doubleFormat(loc.getZ()));
		}
		return msg.replace("{name}", name).replace("{locs}", res);
	}
	
	public String getWarpExpiringMessage(String name, long timeLeft) {
		String msg = getMessage("warp-expiring", "&7The warp &7{warp}&7 will expire in &8{time}&7!");
		return msg.replace("{warp}", name).replace("{time}", DateUtil.formatDateDiff(timeLeft));
	}

	public String getChangedExpirationMessage(String warp, String time) {
		return getMessage("changed-warp-expire-date", "&7Changed the warp &6{warp}&7 to expire &8{time}&7 from now.")
					.replace("{warp}", warp).replace("{time}", time);
	}
	
	private String getMessage(String path, String def) {
		return Utils.color(IW.getConfig().getString("messages." + path, def));
	}

}
