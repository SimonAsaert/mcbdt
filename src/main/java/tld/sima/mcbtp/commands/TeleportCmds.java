package tld.sima.mcbtp.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import net.md_5.bungee.api.ChatColor;
import tld.sima.mcbtp.Main;

public class TeleportCmds implements CommandExecutor {

	private final Main plugin = Main.getPlugin(Main.class);
	
	public String tppos = "tppos";
	public String getpos = "getpos";
	public String tpa = "tpa";
	public String tpaccept = "tpaccept";
	public String tpdeny = "tpdeny";
	public String tpall = "tpall";
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			if (command.getName().equalsIgnoreCase(tppos)) {
				final Player player = (Player) sender;
				if (args.length == 3) {
					Double[] xyz = new Double[3];
					for (int i = 0 ; i < args.length ; i++) {
						try {
							xyz[i] = Double.parseDouble(args[i]);
						}catch(Exception e) {
							player.sendMessage(ChatColor.RED + "Position input is not a number!");
						}
					}
					Location loc = new Location(player.getWorld(), xyz[0], xyz[1], xyz[2]);
					Location checkers = loc.clone();
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
						player.sendMessage(ChatColor.RED + "Teleport location is dangerous. Type " + ChatColor.WHITE + "/mcbconfirm" + ChatColor.RED + " within " + ChatColor.WHITE + "10" + ChatColor.RED + " seconds if you still want to go to spawn");
						plugin.getTempMap().put(player.getUniqueId(), loc);
						BukkitScheduler scheduler = Bukkit.getScheduler();
						scheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
							public void run() {
								plugin.getTempMap().remove(player.getUniqueId());
							}
							
						}, 200L);
					}else {
						plugin.getBackMap().put(player.getUniqueId(), player.getLocation());
						player.sendMessage(ChatColor.GOLD + "Teleporting to location");
						player.teleport(loc);
					}
				}
			}else if (command.getName().equalsIgnoreCase(getpos)) {
				Player player = (Player) sender;
				if (args.length == 1) {
					Player otherPlayer = Bukkit.getPlayer(args[0]);
					if (otherPlayer == null) {
						player.sendMessage(ChatColor.RED + "Other player not found!");
						return false;
					}
					Location loc = otherPlayer.getLocation();
					player.sendMessage(ChatColor.GOLD + "Player location: " + ChatColor.AQUA + loc.getX() + " " + loc.getY() + " " + loc.getZ() + ChatColor.GOLD + " in world: " + ChatColor.AQUA + loc.getWorld().getName());
				}else {
					player.sendMessage(ChatColor.WHITE + "Gets location of player another player");
					player.sendMessage(ChatColor.WHITE + "/getpos <Player>");
				}
				// tpa
			}else if (command.getName().equalsIgnoreCase(tpa)) {
				final Player player = (Player) sender;
				if (args.length == 1) {
					final Player otherPlayer = Bukkit.getPlayer(args[0]);
					if (otherPlayer == null) {
						player.sendMessage(ChatColor.RED + "Other player not found!");
						return false;
					}else if (player.getName().equalsIgnoreCase(args[0])) {
						player.sendMessage(ChatColor.RED + "You are already here!");
						return true;
					}else if (plugin.getTpaMap().containsKey(otherPlayer.getUniqueId())) {
						player.sendMessage(ChatColor.RED + "Other player already has a tpa request");
						return true;
					}
					
					otherPlayer.sendMessage(ChatColor.WHITE + player.getDisplayName() + ChatColor.GOLD + " has sent you a teleport request. Type " + ChatColor.GREEN + ChatColor.BOLD + "/tpaccept" + ChatColor.GOLD + " to let them tp to you, or " + ChatColor.RED + ChatColor.BOLD + "/tpdeny" + ChatColor.GOLD + "to deny them.");
					plugin.getTpaMap().put(otherPlayer.getUniqueId(), player.getUniqueId());
					
					new BukkitRunnable() {
						public void run() {
							if (plugin.getTpaMap().containsKey(otherPlayer.getUniqueId())) {
								plugin.getTpaMap().remove(otherPlayer.getUniqueId());
							}
						}
					}.runTaskLater(plugin, 400);
					
				}else {
					player.sendMessage(ChatColor.WHITE + "Request a teleport to a user");
					return false;
				}
				//tpaccept
			}else if (command.getName().equalsIgnoreCase(tpaccept)) {
				Player player = (Player) sender;
				if (plugin.getTpaMap().containsKey(player.getUniqueId())) {
					final Player otherPlayer = Bukkit.getPlayer(plugin.getTpaMap().get(player.getUniqueId()));
					if (otherPlayer == null) {
						player.sendMessage(ChatColor.RED + "Something went wrong. Other player not found!");
						plugin.getTpaMap().remove(player.getUniqueId());
						return true;
					}
					otherPlayer.sendMessage(ChatColor.GREEN + "Teleport request accepted!");
					player.sendMessage(ChatColor.GOLD + "You have " + ChatColor.GREEN + "accepted " + ChatColor.WHITE + otherPlayer.getName() + ChatColor.GOLD + "'s teleport request");
					otherPlayer.sendMessage(ChatColor.GOLD + "You will be teleported in 3 seconds!");
					final Location loc = player.getLocation().clone();
					plugin.getTpaMap().remove(player.getUniqueId());

					new BukkitRunnable() {
						public void run() {
							otherPlayer.sendMessage(ChatColor.GOLD + "Teleporting!");
							otherPlayer.teleport(loc);
						}
					}.runTaskLater(plugin, 60);
				}else {
					player.sendMessage(ChatColor.RED + "No teleport requests found!");
				}
				//tpdeny
			}else if (command.getName().equalsIgnoreCase(tpdeny)) {
				Player player = (Player) sender;
				if (plugin.getTpaMap().containsKey(player.getUniqueId())) {
					Player otherPlayer = Bukkit.getPlayer(plugin.getTpaMap().get(player.getUniqueId()));
					if (otherPlayer == null) {
						player.sendMessage(ChatColor.RED + "Something went wrong. Other player not found!");
						plugin.getTpaMap().remove(player.getUniqueId());
						return true;
					}
					otherPlayer.sendMessage(ChatColor.RED + "Teleport request denied!");
					player.sendMessage(ChatColor.GOLD + "You have " + ChatColor.RED + "denied " + ChatColor.WHITE + otherPlayer.getName() + ChatColor.GOLD + "'s teleport request");
					plugin.getTpaMap().remove(player.getUniqueId());
				}else {
					player.sendMessage(ChatColor.RED + "No teleport requests found!");
				}
			}else if (command.getName().equalsIgnoreCase(tpall)){
				Player player = (Player) sender;
				if (player.hasPermission("mcbdt.tpall")) {
					for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
						if (otherPlayer.getUniqueId() != player.getUniqueId()) {
							otherPlayer.sendMessage(ChatColor.GOLD + "You have been summoned to " + ChatColor.WHITE + player.getDisplayName());
							otherPlayer.teleport(player);
						}
					}
				}else {
					player.sendMessage("You do not have mcbdt.tpall permission");
				}
			}
		}else {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "You have to be a player to use that command!");
		}
		return true;
	}
}
