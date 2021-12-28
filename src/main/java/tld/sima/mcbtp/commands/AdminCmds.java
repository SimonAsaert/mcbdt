package tld.sima.mcbtp.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import tld.sima.mcbtp.Main;

public class AdminCmds implements CommandExecutor{

	Main plugin = Main.getPlugin(Main.class);
	public String cmd1 = "GameMode";
	public String cmd2 = "gm";
	public String cmd3 = "gmc";
	public String cmd4 = "gma";
	public String cmd5 = "gms";
	public String cmd6 = "gmsp";
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if (command.getName().equalsIgnoreCase(cmd1)) {
				if(args.length > 0) {
					String arg = args[0];
					GameMode mode = GameMode.valueOf(arg.toUpperCase());
					if(mode != null) {
						if(args.length > 1 && p.hasPermission("mcdt.admin.gamemode.other")) {
							Player q = Bukkit.getPlayer(args[1].toString());
							if(q != null) {
								p = q;
							}else {
								p.sendMessage(ChatColor.RED + "Unable to find player!");
								return true;
							}
						}
						
						p.setGameMode(mode);
						return true;
					}
				}
				StringBuilder builder = new StringBuilder();
				builder.append(ChatColor.GREEN + "GameModes available: " + ChatColor.WHITE).append(GameMode.values()[0].toString().toLowerCase());
				for(int i = 1 ; i < GameMode.values().length ; i++) {
					builder.append(" | ").append(GameMode.values()[i].toString().toLowerCase());
				}
				p.sendMessage(builder.toString());
			}else if (command.getName().equalsIgnoreCase(cmd2)) {
				if(p.getGameMode().equals(GameMode.SURVIVAL)) {
					p.setGameMode(GameMode.CREATIVE);
					p.sendMessage(ChatColor.GREEN + "Changed GameMode to " + ChatColor.WHITE + "Creative");
				}else {
					p.setGameMode(GameMode.SURVIVAL);
					p.sendMessage(ChatColor.GREEN + "Changed GameMode to " + ChatColor.WHITE + "Survival");
				}
			}else if (command.getName().equalsIgnoreCase(cmd3)) {
				if(args.length > 0 && p.hasPermission("mcdt.admin.gamemode.other")) {
					Player q = Bukkit.getPlayer(args[1]);
					if(p.equals(null)) {
						p.sendMessage(ChatColor.RED + "Unable to find player!");
						return true;
					}else {
						p.sendMessage(ChatColor.GREEN + "Changed " + ChatColor.WHITE + args[0] + ChatColor.GREEN + "'s mode to Creative");
						p = q;
					}
				}
				p.setGameMode(GameMode.CREATIVE);
				p.sendMessage(ChatColor.GREEN + "Changed mode to " + ChatColor.WHITE + "Creative");
			}else if (command.getName().equalsIgnoreCase(cmd4)) {
				if(args.length > 0 && p.hasPermission("mcdt.admin.gamemode.other")) {
					Player q = Bukkit.getPlayer(args[1]);
					if(p.equals(null)) {
						p.sendMessage(ChatColor.RED + "Unable to find player!");
						return true;
					}else {
						p.sendMessage(ChatColor.GREEN + "Changed " + ChatColor.WHITE + args[0] + ChatColor.GREEN + "'s mode to Adventure");
						p = q;
					}
				}
				p.setGameMode(GameMode.ADVENTURE);
				p.sendMessage(ChatColor.GREEN + "Changed mode to " + ChatColor.WHITE + "Adventure");
			}else if (command.getName().equalsIgnoreCase(cmd5)) {
				if(args.length > 0 && p.hasPermission("mcdt.admin.gamemode.other")) {
					Player q = Bukkit.getPlayer(args[1]);
					if(p.equals(null)) {
						p.sendMessage(ChatColor.RED + "Unable to find player!");
						return true;
					}else {
						p.sendMessage(ChatColor.GREEN + "Changed " + ChatColor.WHITE + args[0] + ChatColor.GREEN + "'s mode to Survival");
						p = q;
					}
				}
				p.setGameMode(GameMode.SURVIVAL);
				p.sendMessage(ChatColor.GREEN + "Changed mode to " + ChatColor.WHITE + "Survival");
			}else if (command.getName().equalsIgnoreCase(cmd6)) {
				if(args.length > 0 && p.hasPermission("mcdt.admin.gamemode.other")) {
					Player q = Bukkit.getPlayer(args[1]);
					if(p.equals(null)) {
						p.sendMessage(ChatColor.RED + "Unable to find player!");
						return true;
					}else {
						p.sendMessage(ChatColor.GREEN + "Changed " + ChatColor.WHITE + args[0] + ChatColor.GREEN + "'s mode to Spectator");
						p = q;
					}
				}
				p.setGameMode(GameMode.SPECTATOR);
				p.sendMessage(ChatColor.GREEN + "Changed mode to " + ChatColor.WHITE + "Spectator");
			}
		}
		return true;
	}
}
