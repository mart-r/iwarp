package me.ford.iwarp.addons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import me.ford.iwarp.IWarpPlugin;

public class WarpLimiter implements IWarpAddOn {
    private static final List<String> EMPTY = new ArrayList<>();
    private static final String PERM_BASE = "iwarp.limits.";
    private static final String OVERRIDE_PERM = "iwarp.limits.override";
    private final IWarpPlugin plugin;
    private final Map<Permission, Integer> permMap = new HashMap<>();

    public WarpLimiter(IWarpPlugin plugin) {
        this.plugin = plugin;
        populateMap();
    }

    public void reload() {
        populateMap();
    }

    private void populateMap() {
        permMap.clear();
        ConfigurationSection section = plugin.getConfig()
                .getConfigurationSection("addons.warp-limiter.limits");
        if (section == null) {
            plugin.getLogger().warning("Warp limiter enabled but no limits section defined for permissions."
                    + " This will mean only those with the override permission will be able to create a warp");
            return;
        }
        PluginManager pm = plugin.getServer().getPluginManager();
        for (String permName : section.getKeys(false)) {
            int val = section.getInt(permName, 1);
            String fullName = PERM_BASE + permName;
            Permission perm = new Permission(fullName, "Allows " + val + " warps", PermissionDefault.FALSE);
            try {
                pm.addPermission(perm);
            } catch (IllegalArgumentException e) {
                perm = pm.getPermission(fullName);
            }
            permMap.put(perm, val);
        }
    }

    public int getAllowedWarps(Player player) {
        if (player.hasPermission(OVERRIDE_PERM)) {
            return Integer.MAX_VALUE;
        }
        int max = 0;
        for (Entry<Permission, Integer> entry : permMap.entrySet()) {
            Permission perm = entry.getKey();
            int val = entry.getValue();
            if (player.hasPermission(perm) && val > max) {
                max = val;
            }
        }
        return max;
    }

    @Override
    public IWarpAddOnType getType() {
        return IWarpAddOnType.WARPLIMITER;
    }

    @Override
    public Collection<String> getCommands() {
        return EMPTY;
    }

    @Override
    public Collection<String> optionsOnCommand(int argnr) {
        return EMPTY;
    }

    @Override
    public boolean onCommand(CommandSender sender, String subCommand, String[] args) {
        return false;
    }

}