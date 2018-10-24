package tld.sima.mcbtp.commands;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import net.md_5.bungee.api.ChatColor;
import tld.sima.mcbtp.Main;
import tld.sima.mcbtp.files.PlayerStorageManager;

public class HomeCmds implements CommandExecutor{
	
	Main plugin = Main.getPlugin(Main.class);

	public String cmd1 = "homes";
	public String cmd2 = "sethome";
	public String cmd3 = "home";
	public String cmd4 = "delhome";
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			// List homes
			if (command.getName().equalsIgnoreCase(cmd1)) {
				Player player = (Player) sender;
				if (args.length == 1) {
					if (player.hasPermission("mcdt.homes.others")) {
						Player otherPlayer = Bukkit.getPlayer(args[0]);
						if (otherPlayer == null) {
							OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
							if (op == null) {
								player.sendMessage(ChatColor.RED + "Player " + ChatColor.DARK_RED + args[0] + ChatColor.RED + "not found!");
								return false;
							}else {
								PlayerStorageManager psm = new PlayerStorageManager();
								psm.setup(op.getUniqueId());
								HashMap<String, Location> loc = psm.getMap();
								ArrayList<String> homeList = new ArrayList<String>(loc.keySet());
								if (homeList.size() == 0) {
									player.sendMessage(ChatColor.RED + "No homes found");
									psm.getFile().delete();
									return true;
								}
								StringBuilder homes = new StringBuilder();
								homes.append(homeList.get(0));
								for (int i = 1 ; i < homeList.size() ; i++) {
									homes.append(", ").append(homeList.get(i));
								}
								player.sendMessage(ChatColor.GOLD + "Homes: " + ChatColor.AQUA + homes.toString());
							}
							return true;
						}
						if (plugin.getHomeMapUser(otherPlayer.getUniqueId()).containsKey("bed")) {
							if (otherPlayer.getBedSpawnLocation() == null) {
								plugin.getHomeMapUser(otherPlayer.getUniqueId()).remove("bed");
							}
						}
						ArrayList<String> homelist = new ArrayList<String>(plugin.getHomeMapUser(otherPlayer.getUniqueId()).keySet());
						if (homelist.size() == 0) {
							player.sendMessage(ChatColor.RED + "No homes found");
							return true;
						}
						StringBuilder homes = new StringBuilder();
						homes.append(homelist.get(0));
						for (int i = 1 ; i < homelist.size() ; i++) {
							homes.append(", ").append(homelist.get(i));
						}
						player.sendMessage(ChatColor.GOLD + "Homes: " + ChatColor.AQUA + homes.toString());
						return true;
					}
				}
				StringBuilder homes = new StringBuilder();
				if (plugin.getHomeMapUser(player.getUniqueId()).containsKey("bed")) {
					if (player.getBedSpawnLocation() == null) {
						plugin.getHomeMapUser(player.getUniqueId()).remove("bed");
					}
				}
				ArrayList<String> homelist = new ArrayList<String>(plugin.getHomeMapUser(player.getUniqueId()).keySet());
				if (homelist.size() == 0) {
					player.sendMessage(ChatColor.RED + "No homes found");
					return true;
				}
				homes.append(homelist.get(0));
				for (int i = 1 ; i < homelist.size() ; i++) {
					homes.append(", ").append(homelist.get(i));
				}
				player.sendMessage(ChatColor.GOLD + "Homes: " + ChatColor.AQUA + homes.toString());
				return true;
			// Set user home
			}else if (command.getName().equalsIgnoreCase(cmd2)) { 
				Player player = (Player) sender;
				if ((args.length == 1) || (args.length == 0)) {
					String homeName;
					if (args.length == 1) {
						homeName = args[0];
					}else {
						homeName = "home";
					}
					if (!plugin.getHomeMapUser(player.getUniqueId()).containsKey(homeName)) {
						int maxHomes = 3;
						for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
							if (perm.getPermission().startsWith("mcdt.homenumb.")) {
								String permission = perm.getPermission();
								String delims = "[.]";
								String[] tokens = permission.split(delims);
								String out = tokens[tokens.length-1];
								try {
									maxHomes = Integer.parseInt(out);
								}catch(Exception e){
									Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Permission node for setting number of homes doesn't end in an int");
								}
								break;
							}else {
								continue;
							}
						}
						int homelength = plugin.getHomeMapUser(player.getUniqueId()).keySet().size();
						if (plugin.getHomeMapUser(player.getUniqueId()).containsKey("bed")) {
							homelength--;
						}
						if (homelength > maxHomes) {
							player.sendMessage(ChatColor.RED + "You have reached your maximum allowance of homes at " + ChatColor.WHITE + maxHomes);
							return true;
						}
					}
					plugin.getHomeMapUser(player.getUniqueId()).put(homeName, player.getLocation());
					player.sendMessage(ChatColor.GOLD + "Home " + ChatColor.WHITE + homeName + ChatColor.GOLD + " set!");
				}else if (args.length == 2) {
					Player otherPlayer = Bukkit.getServer().getPlayer(args[0]);
					if (otherPlayer == null) {
						player.sendMessage(ChatColor.RED + "Player " + args[0] + " not found!");
						return false;
					}
					plugin.getHomeMapUser(otherPlayer.getUniqueId()).put(args[1], player.getLocation());
				}else {
					player.sendMessage(ChatColor.WHITE + "Use to set your home.");
					return false;
				}
				return true;
			// Teleport user to home
			}else if (command.getName().equalsIgnoreCase(cmd3)) {
				Player player = (Player) sender;
				if ((args.length == 1) || (args.length == 0)) {
					String homeName;
					if (args.length == 1) {
						homeName = args[0];
					}else {
						homeName = "home";
					}
					if (plugin.getHomeMapUser(player.getUniqueId()).containsKey(homeName)) {
						player.sendMessage(ChatColor.GOLD + "Teleporting you to " + ChatColor.WHITE + homeName);
						player.teleport(plugin.getHomeMapUser(player.getUniqueId()).get(homeName));
					}else {
						player.sendMessage(ChatColor.RED + "Home " + ChatColor.WHITE + homeName + ChatColor.RED + " not found!");
					}
				}else if (args.length == 2) {
					if (player.hasPermission("mcdt.home.others")) {
						Player otherPlayer = Bukkit.getPlayer(args[0]);
						if ((otherPlayer == null)) {
							OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
							if (op == null) {
								player.sendMessage(ChatColor.RED + "Player " + ChatColor.DARK_RED + args[0] + ChatColor.RED + "not found!");
								return true;
							}else {
								PlayerStorageManager psm = new PlayerStorageManager();
								psm.setup(op.getUniqueId());
								HashMap<String, Location> loc = psm.getMap();
								if (loc.containsKey(args[1])){
									player.sendMessage(ChatColor.GOLD + "Teleporting you to " + ChatColor.WHITE + args[1]);
									player.teleport(loc.get(args[1]));
								}else {
									player.sendMessage(ChatColor.RED + "Home " + ChatColor.WHITE + args[1] + ChatColor.RED + " not found!");
									ArrayList<String> homes = new ArrayList<String>(loc.keySet());
									if (homes.size() == 0 ) {
										psm.getFile().delete();
									}
								}
							}
							return true;
						}
						if (plugin.getHomeMapUser(otherPlayer.getUniqueId()).containsKey(args[1])) {
							player.sendMessage(ChatColor.GOLD + "Teleporting you to " + ChatColor.WHITE + args[1]);
							player.teleport(plugin.getHomeMapUser(otherPlayer.getUniqueId()).get(args[1]));
						}else {
							player.sendMessage(ChatColor.RED + "Home " + ChatColor.WHITE + args[1] + ChatColor.RED + " not found!");
						}
						return true;
					}else {
						player.sendMessage(ChatColor.WHITE + "Use to teleport to your home.");
						return false;
					}
				}else {
					player.sendMessage(ChatColor.WHITE + "Use to teleport to your home.");
					return false;
				}
				return true;
			// Delete home
			}else if (command.getName().equalsIgnoreCase(cmd4)) {
				Player player = (Player) sender;
				if (args.length == 1) {
					if (plugin.getHomeMapUser(player.getUniqueId()).containsKey(args[0])){
						plugin.getHomeMapUser(player.getUniqueId()).remove(args[0]);
						player.sendMessage(ChatColor.GOLD + "Home " + ChatColor.WHITE + args[0] + ChatColor.GOLD + " deleted");
					}else {
						player.sendMessage(ChatColor.RED + "Home " + ChatColor.DARK_RED + args[0] + " not found.");
					}
					return true;
				}else if (args.length == 2) {
					if (player.hasPermission("mcdt.delhome.others")) {
						Player otherPlayer = Bukkit.getServer().getPlayer(args[0]);
						if (otherPlayer == null) {
							player.sendMessage(ChatColor.RED + "Player " + ChatColor.DARK_RED + args[0] + ChatColor.RED + " not found");
							return false;
						}
						if (plugin.getHomeMapUser(otherPlayer.getUniqueId()).containsKey(args[1])){
							plugin.getHomeMapUser(otherPlayer.getUniqueId()).remove(args[1]);
							player.sendMessage(ChatColor.GOLD + "Home " + ChatColor.WHITE + args[1] + ChatColor.GOLD + " deleted");
						}else {
							player.sendMessage(ChatColor.RED + "Home " + ChatColor.DARK_RED + args[1] + " not found.");
							return false;
						}
					}else {
						player.sendMessage(ChatColor.WHITE + "Delete home");
						return false;
					}
				}else {
					player.sendMessage(ChatColor.WHITE + "Delete home");
					return false;
				}
				return true;
			}
		}else {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "You have to be a player to use this command.");
		}
		return false;
	}
}
