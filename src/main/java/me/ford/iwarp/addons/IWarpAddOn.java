package me.ford.iwarp.addons;

import java.util.Collection;

import org.bukkit.command.CommandSender;

public interface IWarpAddOn {
	
	public IWarpAddOnType getType();
	
	public Collection<String> getCommands();
	
	public Collection<String> optionsOnCommand(int argnr);
	
	public boolean onCommand(CommandSender sender, String subCommand, String[] args);

}
