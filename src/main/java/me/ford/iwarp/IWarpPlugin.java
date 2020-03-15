package me.ford.iwarp;

import java.util.HashMap;
import java.util.Map;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.ford.iwarp.addons.IWarpAddOnType;
import me.ford.iwarp.addons.OldWarpLocationLogger;
import me.ford.iwarp.addons.WarpExpiryNotifier;
import me.ford.iwarp.addons.IWarpAddOn;
import me.ford.iwarp.commands.CommandIWarp;
import me.ford.iwarp.commands.CommandIWarpAddOns;
import net.milkbowl.vault.economy.Economy;

public class IWarpPlugin extends JavaPlugin {
	private Settings settings;
	private WarpHandler warpHandler;
	private Economy econ;
	private Map<IWarpAddOnType, IWarpAddOn> addOns = new HashMap<>();
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		settings = new Settings(this);
		saveConfig();
		try {
			warpHandler = new WarpHandler(this);
		} catch (ClassNotFoundException e) {
			getLogger().severe("EssentialsX not found - aborting plugin!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		// Vault
		if (!setupEconomy()) {
			getLogger().severe("Was unable to hook into a Vault economy - aborting plugin!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		if (settings.useBstats()) {
			new Metrics(this);
		}
		
		// addons
		if (settings.isAddOnEnabled(IWarpAddOnType.OLDWARPLOCATIONLOGGER)) {
			addOns.put(IWarpAddOnType.OLDWARPLOCATIONLOGGER, new OldWarpLocationLogger(this));
		}
		if (settings.isAddOnEnabled(IWarpAddOnType.WARPEXPIRYNOTIFIER)) {
			addOns.put(IWarpAddOnType.WARPEXPIRYNOTIFIER, new WarpExpiryNotifier(this));
		}
		
		// commands
		getCommand("iwarp").setExecutor(new CommandIWarp(this));
		getCommand("iwarpaddons").setExecutor(new CommandIWarpAddOns(this));
	}
	
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    public void reload() {
    	reloadConfig();
    	warpHandler.reload();
    }
	
	public Settings getSettings() {
		return settings;
	}
	
	public WarpHandler getWarpHandler() {
		return warpHandler;
	}
	
	public Economy getEcon() {
		return econ;
	}
	
	public IWarpAddOn getAddOn(IWarpAddOnType type) {
		return addOns.get(type);
	}
	
	public OldWarpLocationLogger getOldLocationLogger() {
		return (OldWarpLocationLogger) getAddOn(IWarpAddOnType.OLDWARPLOCATIONLOGGER);
	}

}
