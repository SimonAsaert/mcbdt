package tld.sima.mcbtp.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.md_5.bungee.api.ChatColor;
import tld.sima.mcbtp.Main;

public class PlayerCmds implements CommandExecutor{
	
	private Main plugin = Main.getPlugin(Main.class);

	public String cmd1 = "heal";
	public String cmd2 = "feed";
	public String cmd3 = "god";
	public String cmd4 = "fly";
	public String cmd5 = "kill";
	public String cmd6 = "pvp";
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			if (command.getName().equalsIgnoreCase(cmd1)) {
				Player player = (Player) sender;
				if ((args.length == 1) && sender.hasPermission("mcdt.heal.others")) {
					Player otherPlayer = Bukkit.getPlayer(args[0]);
					if (otherPlayer == null) {
						player.sendMessage(ChatColor.RED + "Player not found!");
						return false;
					}
					otherPlayer.setHealth(20);
					otherPlayer.setFoodLevel(20);
				}else if (args.length == 0) {
					player.setHealth(20);
					player.setFoodLevel(20);
				}else {
					player.sendMessage(ChatColor.WHITE + "Sets player food and health to max");
					return false;
				}
				return true;
			// Feed
			}else if (command.getName().equalsIgnoreCase(cmd2)) {
				Player player = (Player) sender;
				if ((args.length == 1) && (sender.hasPermission("mcdt.feed.others"))) {
					Player otherPlayer = Bukkit.getPlayer(args[0]);
					if (otherPlayer == null) {
						player.sendMessage(ChatColor.RED + "Player not found!");
						return false;
					}
					otherPlayer.setFoodLevel(20);
				}else if (args.length == 0) {
					player.setFoodLevel(20);
				}else {
					player.sendMessage(ChatColor.WHITE + "Sets player food to max");
					return false;
				}
				return true;
			// Godmode
			}else if (command.getName().equalsIgnoreCase(cmd3)) {
				Player player = (Player) sender;
				if (args.length == 1) {
					Player otherPlayer = Bukkit.getPlayer(args[0]);
					if (otherPlayer == null) {
						player.sendMessage(ChatColor.RED + "Other player not found!");
						return false;
					}
					UUID uuid = otherPlayer.getUniqueId();
					if (plugin.getGodSet().contains(uuid)) {
						plugin.getGodSet().remove(uuid);
					}else {
						plugin.getGodSet().add(uuid);
					}
				}else if (args.length == 0) {
					UUID uuid = player.getUniqueId();
					if (plugin.getGodSet().contains(uuid)) {
						plugin.getGodSet().remove(uuid);
						player.sendMessage(ChatColor.GOLD + "Godmode " + ChatColor.RED + "removed" + ChatColor.GOLD + "!");
					}else {
						plugin.getGodSet().add(uuid);
						player.sendMessage(ChatColor.GOLD + "Godmode " + ChatColor.GREEN + "added" + ChatColor.GOLD + "!");
					}
				}else {
					player.sendMessage(ChatColor.WHITE + "Toggles user gamemode.");
					return false;
				}
				return true;
			// Flight
			}else if (command.getName().equalsIgnoreCase(cmd4)) {
				Player player = (Player) sender;
				if ((args.length == 1) && sender.hasPermission("mcdt.fly.others") ) {
					Player otherPlayer = Bukkit.getPlayer(args[0]);
					if (otherPlayer == null) {
						player.sendMessage(ChatColor.RED + "Other player not found!");
						return false;
					}
					if (otherPlayer.isFlying()) {
						otherPlayer.setFlying(false);
						otherPlayer.setAllowFlight(false);
						otherPlayer.sendMessage(ChatColor.GOLD + "Player " + ChatColor.WHITE + args[0] + ChatColor.GOLD + " is " + ChatColor.GREEN + "now" + ChatColor.GOLD + " flying!");
					}else {
						otherPlayer.setAllowFlight(true);
						otherPlayer.setFlying(true);
						otherPlayer.sendMessage(ChatColor.GOLD + "Player " + ChatColor.WHITE + args[0] + ChatColor.GOLD + " is " + ChatColor.RED+ "no longer" + ChatColor.GOLD + " flying!");
					}
				}else if (args.length == 0) {
					if (player.isFlying()) {
						player.setFlying(false);
						player.setAllowFlight(false);
						player.sendMessage(ChatColor.GOLD + "Hope you enjoyed your flight!");
					}else {
						player.setAllowFlight(true);
						player.setFlying(true);
						player.sendMessage(ChatColor.GOLD + "Enjoy your flight!");
					}
				}else {
					player.sendMessage("Toggle flight for user");
					return false;
				}
				return true;
			// kill
			}else if (command.getName().equalsIgnoreCase(cmd5)) {
				Player player = (Player) sender;
				if (args.length == 1) {
					Player otherPlayer = Bukkit.getPlayer(args[0]);
					if (otherPlayer == null) {
						player.sendMessage("Player name not found!");
						return false;
					}
					otherPlayer.setHealth(0);
				}else if (args.length == 0) {
					player.setHealth(0);
				}else {
					player.sendMessage(ChatColor.WHITE + "Kills player");
					return false;
				}
				return true;
			// pvp
			}else if (command.getName().equalsIgnoreCase(cmd6)) {
				final Player player = (Player) sender;
				if ((args.length == 1) && (player.hasPermission("mcdt.kill.others"))) {
					Player otherPlayer = Bukkit.getPlayer(args[0]);
					if (otherPlayer == null) {
						player.sendMessage("Player name not found!");
						return false;
					}
					UUID uuid = otherPlayer.getUniqueId();
					if (plugin.getPVPSet().contains(uuid)) {
						plugin.getPVPSet().remove(uuid);
						player.sendMessage(ChatColor.WHITE + otherPlayer.getName() + ChatColor.GOLD + "'s pvp is " + ChatColor.RED + "disabled!");
					}else {
						plugin.getPVPSet().add(uuid);
						player.sendMessage(ChatColor.WHITE + otherPlayer.getName() + ChatColor.GOLD + "'s pvp is " + ChatColor.GREEN + "enabled!");
					}
				}else if (args.length == 0) {
					final UUID uuid = player.getUniqueId();
					if (plugin.getPVPSet().contains(uuid) && !plugin.pvptask.containsKey(uuid)) {
						BukkitTask br = new BukkitRunnable() {
							public void run() {
								plugin.getPVPSet().remove(uuid);
								player.sendMessage(ChatColor.GOLD + "Your pvp is " + ChatColor.RED + "disabled" + ChatColor.GOLD + "!");
							}
						}.runTaskLater(plugin, (20*10L));
						plugin.pvptask.put(uuid, br);
						player.sendMessage(ChatColor.GOLD + "Your pvp will be disabled in " + ChatColor.WHITE + "20" + ChatColor.GOLD + " seconds!");
						
					}else if(!plugin.getPVPSet().contains(uuid)) {
						plugin.getPVPSet().add(uuid);
						player.sendMessage(ChatColor.GOLD + "Your pvp is " + ChatColor.GREEN + "enabled" + ChatColor.GOLD + "!");
					}else if (plugin.pvptask.containsKey(uuid)) {
						player.sendMessage(ChatColor.GOLD + "Pvp toggle has been cancelled!");
						plugin.pvptask.get(uuid).cancel();
						plugin.pvptask.remove(uuid);
					}
				}else {
					player.sendMessage(ChatColor.WHITE + "Toggles player's pvp");
					return false;
				}
				return true;
			}
		}
		
		return false;
	}
}
