package tld.sima.mcbtp.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.lucko.luckperms.api.Group;
import tld.sima.mcbtp.Main;

public class ServerSettingStorageManager {

	Main plugin = Main.getPlugin(Main.class);

	// File and File Configurations here
	private FileConfiguration storagecfg;
	private File storagefile;
	/*-------------------------------*/

	// Main Config setup
	public void setup() {
		// Create plugin folder if doesn't exist.
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		String FileLocation = plugin.getDataFolder().toString() + File.separator + "ServerGroups.yml";

		File tmp = new File(FileLocation);

		if(!tmp.exists()) {
			try {
				tmp.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				Bukkit.getServer().getConsoleSender().sendMessage(net.md_5.bungee.api.ChatColor.RED + "Player file unable to be created!");
				return;
			}
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Server Group Settings File created!");
		}
		storagefile = tmp;
		storagecfg = YamlConfiguration.loadConfiguration(storagefile);
		createStorageValues();
	}

	private void createStorageValues() {
		storagecfg.addDefault("group.list", new ArrayList<String>());
		storagecfg.options().copyDefaults(true);
		savecfg();
	}

	private boolean savecfg() {
		try {
			storagecfg.save(storagefile);
		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to save storage file!" );
			return false;
		}
		return true;
	}
	
	public void putNewLocation(String name, Location loc) {
		if ((loc == null) || (name == null) || (name == "")) {
			return;
		}
		List<String> oldList = storagecfg.getStringList("group.list");
		if (!oldList.contains(name)) {
			oldList.add(name);
			storagecfg.set("group.list", oldList);
		}
		storagecfg.set(name + ".x", loc.getX());
		storagecfg.set(name + ".y", loc.getY());
		storagecfg.set(name + ".z", loc.getZ());
		storagecfg.set(name + ".world", loc.getWorld().getUID().toString());
		
		savecfg();
	}
	
	private void putLocation(String name, Location loc, boolean flag) {
		storagecfg.set(name + ".x", loc.getX());
		storagecfg.set(name + ".y", loc.getY());
		storagecfg.set(name + ".z", loc.getZ());
		storagecfg.set(name + ".world", loc.getWorld().getUID().toString());
		if(flag) {
			savecfg();
		}
	}
	
	public Location getLocation(String name) {
		if ((name == null) || (name == "")) {
			return null;
		}
		String worldName = storagecfg.getString(name + ".world");
		UUID uuid = UUID.fromString(worldName);
		World world = Bukkit.getWorld(uuid);
		if (world == null) {
			return null;
		}

		Double x = storagecfg.getDouble(name + ".x");
		Double y = storagecfg.getDouble(name + ".y");
		Double z = storagecfg.getDouble(name + ".z");
		Location loc = new Location(world, x, y, z);
		
		return loc;
	}
	
	public void getAllLocations(Set<Group> groups) {
		List<String> namesOnFile = storagecfg.getStringList("group.list");
		for (Group group : groups) {
			String name = group.getName();
			if (!namesOnFile.contains(name.toLowerCase())) {
				continue;
			}
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + name.toLowerCase());
			Location loc = getLocation(name.toLowerCase());
			if (!(loc == null)) {
				plugin.getSpawnMap().put(name.toLowerCase(), loc);
			}
		}
	}
	
	public void saveAllLocations(HashMap<String, Location> spawnMap) {
		for (String groupName : spawnMap.keySet()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + groupName);
		}
		// Get list of groups already on file
		List<String> oldGroups = storagecfg.getStringList("group.list");
		// Get list of currently used groups from server
		List<String> newGroups = new ArrayList<String>(spawnMap.keySet());
		// Update file with the new groups
		storagecfg.set("group.list", new ArrayList<String>(newGroups));
		// Start to check through the list that were already on the file. Remove those that are no longer in use
		for (String groupName : oldGroups) {
			if (!newGroups.contains(groupName.toLowerCase())) {
				storagecfg.set(groupName.toLowerCase(), null);
			}else {
				putLocation(groupName.toLowerCase(), spawnMap.get(groupName.toLowerCase()), false);
				newGroups.remove(groupName.toLowerCase());
			}
		}
		for (String groupName: newGroups) {
			if (!oldGroups.contains(groupName.toLowerCase())) {
				putLocation(groupName.toLowerCase(), spawnMap.get(groupName.toLowerCase()), false);
			}
		}
		savecfg();
	}
}
