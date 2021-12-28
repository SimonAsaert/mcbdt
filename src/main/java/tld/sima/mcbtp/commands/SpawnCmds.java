package tld.sima.mcbtp.commands;

import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import net.md_5.bungee.api.ChatColor;
import tld.sima.mcbtp.Main;

public class SpawnCmds implements CommandExecutor {
	
	Main plugin = Main.getPlugin(Main.class);
	
	public String spawnCmd = "spawn";
	public String setSpawnCmd = "setspawn";
	public String mcbConfirmCmd = "mcbconfirm";
	public String delSpawnCmd = "delspawn";
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			if (command.getName().equalsIgnoreCase(spawnCmd)) {
				final Player player = (Player) sender;
				
				Location spawn;
				if (plugin.api == null) {
					spawn = player.getLocation().getWorld().getSpawnLocation().clone();
				}else {
					if (plugin.getSpawnMap().containsKey(plugin.api.getUserManager().getUser(player.getUniqueId()).getPrimaryGroup().toLowerCase())) {
						spawn = plugin.getSpawnMap().get(plugin.api.getUserManager().getUser(player.getUniqueId()).getPrimaryGroup().toLowerCase());
					}else {
						spawn = plugin.getSpawnMap().get("default");
					}
				}
				Location checkers = spawn.clone();
				boolean flag = true;
				if (!(player.getGameMode().equals(GameMode.CREATIVE) || player.isFlying())) {
					if (!(checkers.getBlock().getType().equals(Material.AIR))) {
						flag = false;
					}
					checkers.add(0, +1, 0);
					if (!(checkers.getBlock().getType().equals(Material.AIR))) {
						flag = false;
					}
					checkers.add(0, -2, 0);
					if (checkers.getBlock().getType().equals(Material.AIR)) {
						flag = false;
					}
				}
				if (!flag) {
					player.sendMessage(ChatColor.RED + "Spawn location is dangerous. Type " + ChatColor.WHITE + "/" + mcbConfirmCmd + ChatColor.RED + " within " + ChatColor.WHITE + "10" + ChatColor.RED + "seconds if you still want to go to spawn");
					plugin.getTempMap().put(player.getUniqueId(),spawn);
					BukkitScheduler scheduler = Bukkit.getScheduler();
					scheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
						public void run() {
							plugin.getTempMap().remove(player.getUniqueId());
						}
					}, 200L);
				}else {
					plugin.getBackMap().put(player.getUniqueId(), player.getLocation());
					player.sendMessage(ChatColor.GOLD + "Teleporting to spawn");
					player.teleport(spawn);
				}
				return true;
			}else if(command.getName().equalsIgnoreCase(setSpawnCmd)) {
				Player player = (Player) sender;
				if (args.length == 1 && plugin.api != null) {
					Group group = plugin.api.getGroupManager().getGroup(args[0]);
					if (group == null) {
						player.sendMessage(ChatColor.RED + "Group doesn't exist!");
						return false;
					}
					plugin.getSpawnMap().put(args[0].toLowerCase(), player.getLocation());
					player.sendMessage(ChatColor.GOLD + "Setting current location as " + ChatColor.WHITE + args[0].toLowerCase() + ChatColor.GOLD + "'s current location");
				}else if (args.length == 0 && plugin.api != null) {
					plugin.getSpawnMap().put("default", player.getLocation());
					player.sendMessage(ChatColor.GOLD + "Setting current location as " + ChatColor.WHITE + "default" + ChatColor.GOLD + "'s current location");
				}else if (plugin.api != null) {
					player.sendMessage("Used to set spawn locations");
					return false;
				}else {
					player.sendMessage(ChatColor.GOLD + "Setting " + ChatColor.WHITE + player.getLocation().getWorld().getName() + ChatColor.GOLD + " spawn to current location");
//					Bukkit.getServer().getWorld(player.getLocation().getWorld().getUID()).setSpawnLocation(player.getLocation());
					Location loc = player.getLocation();
					player.getLocation().getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getYaw());
				}
				return true;
			}else if(command.getName().equalsIgnoreCase(mcbConfirmCmd)) {
				Player player = (Player) sender;
				if (plugin.getTempMap().containsKey(player.getUniqueId())) {
					plugin.getBackMap().put(player.getUniqueId(), player.getLocation());
					player.sendMessage(ChatColor.GOLD + "Teleporting to location");
					player.teleport(plugin.getTempMap().get(player.getUniqueId()));
				}
				return true;
			}else if (command.getName().equalsIgnoreCase(delSpawnCmd)) {
				Player player = (Player) sender;
				if (args.length == 1 && plugin.api != null) {
					Group group = plugin.api.getGroupManager().getGroup(args[0]);
					if (group != null) {
						if(plugin.getSpawnMap().containsKey(args[0].toLowerCase())) {
						plugin.getSpawnMap().remove(args[0].toLowerCase());
						player.sendMessage(ChatColor.WHITE + args[0].toLowerCase() + ChatColor.GOLD + "'s defined spawn location removed!");
						}else {
							player.sendMessage(ChatColor.WHITE + args[0].toLowerCase() + ChatColor.RED + " is already using the default spawn location!");
						}
					}else {
						player.sendMessage(ChatColor.WHITE + args[0].toLowerCase() + ChatColor.RED + " group is not found!");
					}
				}else if (plugin.api == null) {
					player.sendMessage(ChatColor.RED + "LuckyPerms is not used on this server. This command has no use");
				}else {
					player.sendMessage("Used to remove a custom defined spawn for a specific group");
					return false;
				}
				return true;
			}
		}else {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "You have to be a player to use this command.");
			return true;
		}
		return false;
	}
}
