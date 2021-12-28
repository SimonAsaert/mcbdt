package tld.sima.mcbtp.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import net.md_5.bungee.api.ChatColor;
import tld.sima.mcbtp.Main;

public class InventoryCmds implements CommandExecutor {
	
	private Main plugin = Main.getPlugin(Main.class);

	public String cmd1 = "invsee";
	public String cmd2 = "endersee";
	public String cmd3 = "oi";
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			if (command.getName().equalsIgnoreCase(cmd1)) {
				Player player = (Player) sender;
				if (args.length == 1) {
					Player otherPlayer = Bukkit.getPlayer(args[0]);
					if (otherPlayer == null) {
						player.sendMessage(ChatColor.RED + "Other player not found!");
						return false;
					}
					Inventory other = otherPlayer.getInventory();
					other.setMaxStackSize(45);
					
//					Inventory i = plugin.getServer().createInventory(null, 45, ChatColor.DARK_BLUE + "Inventory of " + ChatColor.WHITE + otherPlayer.getName());
//					for (int j = 0 ; j < 36 ; j++) {
//						i.setItem(j, other.getItem(j));
//					}
					
					if (!(player.getEquipment().getHelmet() == null)) {
						other.setItem(36, otherPlayer.getEquipment().getHelmet().clone());
					}
					if (!(player.getEquipment().getChestplate() == null)) {
						other.setItem(37, otherPlayer.getEquipment().getChestplate().clone());
					}
					if (!(player.getEquipment().getLeggings() == null)) {
						other.setItem(38, otherPlayer.getEquipment().getLeggings().clone());
					}
					if (!(player.getEquipment().getBoots() == null)) {
						other.setItem(39, otherPlayer.getEquipment().getBoots().clone());
					}
					player.openInventory(other);
				}else {
					player.sendMessage(ChatColor.WHITE + "Opens the inventory of another user (Does not allow to interact!)");
					return false;
				}
				return true;
			}else if (command.getName().equalsIgnoreCase(cmd2) || command.getName().equalsIgnoreCase(cmd3)) {
				Player player = (Player) sender;
				if (args.length == 1) {
					Player otherPlayer = Bukkit.getPlayer(args[0]);
					if (otherPlayer == null) {
						player.sendMessage(ChatColor.RED + "Other player not found!");
						return false;
					}
					Inventory i = otherPlayer.getEnderChest();
					player.openInventory(i);
				}
			}
		}
		return false;
	}
}
