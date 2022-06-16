package me.ford.iwarp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.IUser;
import com.earth2me.essentials.Warps;
import com.earth2me.essentials.commands.WarpNotFoundException;

import net.ess3.api.InvalidWorldException;

public class EssentialsHook {
	private final IWarpPlugin IW;
	private final Essentials ess;
	
	public EssentialsHook(IWarpPlugin plugin) throws ClassNotFoundException {
		IW = plugin;
		ess = (Essentials) JavaPlugin.getPlugin(Essentials.class);
	}
	
	public Location findWarp(String name) {
		try {
			return ess.getWarps().getWarp(name);
		} catch (WarpNotFoundException | InvalidWorldException e) {
			return null;
		}
	}
	
	public boolean createWarp(String name, Player owner) {
		return createWarp(name, owner.getUniqueId(), owner.getLocation());
	}
	
	public boolean createWarp(String name, UUID owner, Location loc) {
		if (owner == null) {
			IW.getLogger().warning("Unable to set warp with no owner!");
			return false;
		}
		try {
			ess.getWarps().setWarp(wrap(owner), name, loc);
			return true;
		} catch (Exception e) {
			IW.getLogger().warning("Unable to create warp '" + name + "' at " + loc);
			return false;
		}
	}
	
	public void deleteWarp(String name) {
		try {
			ess.getWarps().removeWarp(name);
		} catch (Exception e) {
			IW.getLogger().warning("Error while deleting warp:" + name);
		}
	}
	
	public List<String> getWarpsOf(OfflinePlayer owner) {
		List<String> mywarps = new ArrayList<>();
		if (owner == null) {
			return mywarps;
		}
		Warps warps = ess.getWarps();
		for (String warpName : warps.getList()) {
			try {
				if (owner.getUniqueId().equals(warps.getLastOwner(warpName))) {
					mywarps.add(warpName);
				}
			} catch (WarpNotFoundException e) {
				IW.getLogger().severe("Error finding a warp while iterating through existing warps!");
			}
		}
		return mywarps;
	}
	
	public boolean rename(String from, String to) {
		Location loc;
		try {
			loc = ess.getWarps().getWarp(from);
		} catch (WarpNotFoundException | InvalidWorldException e) {
			return false;
		}
		UUID id;
		try {
			id = ess.getWarps().getLastOwner(from);
		} catch (WarpNotFoundException e) {
			return false;
		}
		if (createWarp(to, id, loc)) {
			deleteWarp(from);
			return true;
		}
		return false;
	}
	
	public UUID getOwner(String name) {
		try {
			return ess.getWarps().getLastOwner(name);
		} catch (WarpNotFoundException e) {
			return null;
		}
	}
	
	public boolean setWarpOwner(String name, Player owner) {
		return createWarp(name, owner.getUniqueId(), findWarp(name));
	}
	
	public Collection<String> getAllWarps() {
		return ess.getWarps().getList();
	}
	
	public Location getWarpLocation(String name) {
		try {
			return ess.getWarps().getWarp(name);
		} catch (WarpNotFoundException | InvalidWorldException e) {
			return null;
		}
	}
	
	private IUser wrap(UUID id) {
		return ess.getUser(id);
	}

}
