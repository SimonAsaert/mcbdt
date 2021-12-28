package tld.sima.mcbtp.commands;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import tld.sima.mcbtp.Main;
import tld.sima.mcbtp.files.PlayerStorageManager;

public class MessageCmds implements CommandExecutor{
	
	private Main plugin = Main.getPlugin(Main.class);

	public String cmd1 = "msg";
	public String cmd2 = "r";
	public String cmd3 = "amute";
	public String cmd4 = "ac";
	public String cmd5 = "pm";
	public String cmd6 = "tell";
	public String cmd7 = "a";
	public String setJoinMsg = "setjoinmsg";
	public String setLeaveMsg = "setleavemsg";
	private final HashMap<UUID, UUID> playerMap = new HashMap<UUID, UUID>();
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase(setJoinMsg) || command.getName().equalsIgnoreCase(setLeaveMsg)){
			if(args.length == 0){
				return false;
			}else{
				OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(args[0]);
				if(!offlinePlayer.hasPlayedBefore()){
					sender.sendMessage(ChatColor.RED + "Unable to find player");
					return true;
				}
				PlayerStorageManager playerStorageManager = new PlayerStorageManager(offlinePlayer.getUniqueId());
				plugin.getServer().getConsoleSender().sendMessage(args);
				StringBuilder builder = new StringBuilder();
				builder.append(args.length > 1 ? args[1]: "");

				for (int i = 2 ; i < args.length ; i++){
					builder.append(" ").append(args[i]);
				}
				if(command.getName().equalsIgnoreCase(setJoinMsg)) {
					playerStorageManager.setLoginMsg(builder.toString());
					sender.sendMessage(ChatColor.GRAY + "Join Message for user " + ChatColor.WHITE + offlinePlayer.getName() + ChatColor.GRAY + " is now set to:");
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', playerStorageManager.getLoginMsg((Player) offlinePlayer)));
				}else{
					playerStorageManager.setLogoutMsg(builder.toString());
					sender.sendMessage(ChatColor.GRAY + "Leave Message for user " + ChatColor.WHITE + offlinePlayer.getName() + ChatColor.GRAY + " is now set to:");
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', playerStorageManager.getLogoutMsg((Player) offlinePlayer)));
				}
			}
			return true;
		}else if (sender instanceof Player) {
			if (command.getName().equalsIgnoreCase(cmd1) || command.getName().equalsIgnoreCase(cmd5) || command.getName().equalsIgnoreCase(cmd6) ) {
				Player player = (Player) sender;

				if ((args.length == 0) || (args.length == 1) ) {
					player.sendMessage(ChatColor.WHITE + "Send message to another user.");
					return false;
				}
				
				Player otherPlayer = Bukkit.getServer().getPlayer(args[0]);
				if (otherPlayer == null) {
					player.sendMessage(ChatColor.RED + "Player not found.");
				}else {
					playerMap.put(player.getUniqueId(), otherPlayer.getUniqueId());
					playerMap.put(otherPlayer.getUniqueId(), player.getUniqueId());
					
					StringBuilder arguments = new StringBuilder();
					for (int i = 1 ; i < args.length ; i++) {
						arguments.append(args[i]).append(" ");
					}
					
					player.sendMessage(ChatColor.GOLD + "[" + ChatColor.RED + "me" + ChatColor.GOLD + " -> " + ChatColor.DARK_RED + otherPlayer.getName() + ChatColor.GOLD + "] : " + ChatColor.WHITE + arguments.toString());
					otherPlayer.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_RED + player.getName() + ChatColor.GOLD + " -> " + ChatColor.RED + "me" + ChatColor.GOLD + "] : " + ChatColor.WHITE + arguments.toString());
				}
				return true;
			}else if (command.getName().equalsIgnoreCase(cmd2)) {
				Player player = (Player) sender;
				if (args.length == 0) {
					player.sendMessage(ChatColor.WHITE + "Quickly reply to the last person you message or who messaged you.");
					return false;
				}
				if (!playerMap.containsKey(player.getUniqueId())) {
					player.sendMessage(ChatColor.RED + "You haven't privately talked to anyone yet to reply to.");
					return true;
				}
				Player otherPlayer = Bukkit.getServer().getPlayer(playerMap.get(player.getUniqueId()));
				if (otherPlayer == null) {
					player.sendMessage(ChatColor.RED + "Player not found.");
					return false;
				}
				StringBuilder arguments = new StringBuilder();
				for (int i = 0 ; i < args.length ; i++) {
					arguments.append(args[i]).append(" ");
				}
				player.sendMessage(ChatColor.GOLD + "[" + ChatColor.RED + "me" + ChatColor.GOLD + " -> " + ChatColor.DARK_RED + otherPlayer.getName() + ChatColor.GOLD + "] : " + ChatColor.WHITE + arguments.toString());
				otherPlayer.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_RED + player.getName() + ChatColor.GOLD + " -> " + ChatColor.RED + "me" + ChatColor.GOLD + "] : " + ChatColor.WHITE + arguments.toString());
			}else if (command.getName().equalsIgnoreCase(cmd3)) {
				Player player = (Player) sender;
				if (player.hasPermission("mcdt.amute")) {
					if (plugin.getAdminChatMap().contains(player.getUniqueId())) {
						player.sendMessage(ChatColor.GOLD + "Leaving admin chat");
						plugin.getAdminChatMap().remove(player.getUniqueId());
					}else {
						player.sendMessage(ChatColor.GOLD + "Entering admin chat");
						plugin.getAdminChatMap().add(player.getUniqueId());
					}
				}
			}else if (command.getName().equalsIgnoreCase(cmd4) || command.getName().equalsIgnoreCase(cmd7)) {
				Player player = (Player) sender;
				if (player.hasPermission("mcdt.adminchat")) {
					if (args.length == 0) {
						return true;
					}
					StringBuilder message = new StringBuilder();
					message.append(ChatColor.AQUA).append("[").append(ChatColor.WHITE).append(player.getName()).append(ChatColor.AQUA).append("]: ");
					message.append(args[0]);
					for (int i = 1 ; i < args.length ; i++) {
						message.append(" ").append(args[i]);
					}
					Set<UUID> playerList = plugin.getAdminChatMap();
					for (UUID uuid : playerList) {
						Player nextPlayer = Bukkit.getPlayer(uuid);
						if (nextPlayer == null) {
							plugin.getAdminChatMap().remove(uuid);
							playerList.remove(uuid);
							continue;
						}
						nextPlayer.sendMessage(message.toString());
					}
				}
			}
			return true;
		}else {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "You have to be a player to use this command");
		}
		return false;
	}
}
