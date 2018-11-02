package tld.sima.mcbtp.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import tld.sima.mcbtp.Main;

public class PlayerStorageManager {
	
	Main plugin = Main.getPlugin(Main.class);
	
	// File and File Configurations here
	private FileConfiguration storagecfg;
	private File storagefile;
	/*-------------------------------*/
	UUID uuid;
	
	// Main Config setup
	public void setup(Player player) {
		setup(player.getUniqueId());
	}
	
	public void setup(UUID uuid) {
		// Create plugin folder if doesn't exist.
		if(!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		
		String FileLocation = plugin.getDataFolder().toString() + File.separator + "Storage";
		
		File tmp = new File(FileLocation);

		if(!tmp.exists()) {
			tmp.mkdir();
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Storage Folder created!");
		}
		
		FileLocation = plugin.getDataFolder().toString() + File.separator + "Storage" + File.separator + uuid.toString() + ".yml";
		
		// Create main file
		tmp = new File(FileLocation);
		storagefile = tmp;
		
		if (!storagefile.exists()) {
			try {
				storagefile.createNewFile();
			}catch(IOException e) {
				e.printStackTrace();
				Bukkit.getServer().getConsoleSender().sendMessage(net.md_5.bungee.api.ChatColor.RED + "Player file unable to be created!");
			}
		}
		storagecfg = YamlConfiguration.loadConfiguration(storagefile);
		
		createStorageValues();
	}
	
	private void createStorageValues() {
		storagecfg.addDefault("homes.names", new ArrayList<String>());
		storagecfg.addDefault("settings.pvp", true);
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
	
	public File getFile() {
		return storagefile;
	}
	
	public void savePVPflag(boolean pvp) {
		storagecfg.set("settings.pvp", pvp);
		savecfg();
	}
	
	public boolean getPVPflag() {
		return storagecfg.getBoolean("settings.pvp");
	}
	
	public void finalSave(HashMap<String, Location> locMap) {
		// Get and duplicate new HomeName list
		List<String> newList = new ArrayList<String>(locMap.keySet());
		storagecfg.set("homes.names", new ArrayList<String>(newList));

		// Get list of HomeNames already on file.
		List<String> oldList = storagecfg.getStringList("homes.names");
		List<String> Checker = new ArrayList<String>(oldList);
		
		for (String string : oldList) {
			if (!newList.contains(string)) {
				storagecfg.set("homes." + string, null);
			}else {
				inputDataWithoutList(string, locMap.get(string));
				newList.remove(string);
			}
		}
		Checker = new ArrayList<String>(newList);
		for (String string : Checker) {
			if(!oldList.contains(string)) {
				inputDataWithoutList(string, locMap.get(string));
			}
		}
		storagecfg.set("settings.pvp", !plugin.getPVPSet().contains(uuid));
		
		savecfg();
	}
	
	private void inputDataWithoutList(String name, Location loc) {
		String input = "homes." + name + ".";
		try {
			storagecfg.set(input+"getWorld", loc.getWorld().getUID().toString());
			storagecfg.set(input+"getX", loc.getX());
			storagecfg.set(input+"getY", loc.getY());
			storagecfg.set(input+"getZ", loc.getZ());
		}catch(NullPointerException e) {
			return;
		}
	}
	
	public void inputData(String name, Location loc) {
		List<String> names;
		try {
			names = storagecfg.getStringList("homes.names");
		}catch(NullPointerException e) {
			names = new ArrayList<String>();
		}
		
		String input = "homes." + name + ".";
		storagecfg.set(input+"getWorld", loc.getWorld().getUID().toString());
		storagecfg.set(input+"getX", loc.getX());
		storagecfg.set(input+"getY", loc.getY());
		storagecfg.set(input+"getZ", loc.getZ());

		names.add(name);
		storagecfg.set("homes.names", names);
		
		savecfg();
	}
	
	public void removeData(String name) {
		List<String> names = storagecfg.getStringList("homes.names");
		if (names.contains(name)) {
			storagecfg.set("homes." + name, null);
			names.remove(name);
			storagecfg.set("homes.names", names);
		}
		savecfg();
	}
	
	public HashMap<String, Location> getMap(){
		List<String> homes;
		try {
			homes = storagecfg.getStringList("homes.names");
		}catch(NullPointerException e) {
			return new HashMap<String, Location>();
		}
		HashMap<String, Location> homeMap = new HashMap<String, Location>();
		for (String home : homes) {
			String input = "homes." + home + ".";
			String worldName = storagecfg.getString(input + "getWorld");
			UUID uuid = UUID.fromString(worldName);
			World world = Bukkit.getWorld(uuid);
			if (world == null) {
				continue;
			}
			Double x = storagecfg.getDouble(input + "getX");
			Double y = storagecfg.getDouble(input + "getY");
			Double z = storagecfg.getDouble(input + "getZ");
			Location loc = new Location(world, x, y, z);
			homeMap.put(home, loc);
		}
		if (!storagecfg.getBoolean("settings.pvp")) {
			plugin.getPVPSet().add(uuid);
		}
		return homeMap;
	}
}