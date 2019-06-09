package me.ford.iwarp;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.ford.iwarp.commands.CommandIWarp;
import net.milkbowl.vault.economy.Economy;

public class IWarpPlugin extends JavaPlugin {
	private Settings settings;
	private WarpHandler warpHandler;
	private Economy econ;
	
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
		
		// commands
		getCommand("iwarp").setExecutor(new CommandIWarp(this));
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
	
	public Settings getSettings() {
		return settings;
	}
	
	public WarpHandler getWarpHandler() {
		return warpHandler;
	}
	
	public Economy getEcon() {
		return econ;
	}

}
