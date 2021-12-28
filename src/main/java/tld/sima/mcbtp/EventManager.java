package tld.sima.mcbtp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import tld.sima.mcbtp.files.PlayerStorageManager;

public class EventManager implements Listener {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if (plugin.getHomeMapUser(player.getUniqueId()).keySet().size() != 0) {
			ArrayList<String> homenames = new ArrayList<String>(plugin.getHomeMapUser(player.getUniqueId()).keySet());
			if (homenames.contains("bed") && (player.getBedSpawnLocation() != null)) {
				event.setRespawnLocation(plugin.getHomeMapUser(player.getUniqueId()).get("bed"));
				return;
			}else if (homenames.contains("bed")) {
				plugin.getHomeMapUser(player.getUniqueId()).remove("bed");
			}
		}
		World world = Bukkit.getWorlds().get(0);
		event.setRespawnLocation(world.getSpawnLocation());
	}
	
	@EventHandler
	public void onPVP(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof Player) {
			UUID damagee = event.getEntity().getUniqueId();
			UUID damager = null;
			if(event.getDamager() instanceof Player) {
				damager = event.getDamager().getUniqueId();
			}else if (event.getDamager() instanceof Arrow) {
				if(((Arrow)event.getDamager()).getShooter() instanceof Player) {
					damager = ((Player)((Arrow)event.getDamager()).getShooter()).getUniqueId();
				}
			}else if (event.getDamager() instanceof Trident) {
				if(((Trident)event.getDamager()).getShooter() instanceof Player) {
					damager = ((Player)((Trident)event.getDamager()).getShooter()).getUniqueId();
				}
			}
			if(damager == null) {
				return;
			}
			
			if(!plugin.getPVPSet().contains(damager) || !plugin.getPVPSet().contains(damagee)) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if ((event.getEntity() instanceof Player) && plugin.getGodSet().contains(event.getEntity().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent event) {
		if ((event.getEntity() instanceof Player) && plugin.getGodSet().contains(event.getEntity().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		PlayerStorageManager smgr = new PlayerStorageManager(player);
		HashMap<String, Location> locMap = smgr.getMap();
		plugin.getHomeMap().put(player.getUniqueId(), locMap);
		
		if (player.hasPermission("mcdt.adminchat") || player.isOp()) {
			plugin.getAdminChatMap().add(player.getUniqueId());
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		Player player = e.getPlayer();
		PlayerStorageManager smgr = new PlayerStorageManager(player);
		if((player.hasPermission("mcdt.custommsg") || player.isOp()) && !smgr.getLoginMsg(player).isEmpty()){
			e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLoginLogoutMsg().getLoginPrefix() + smgr.getLoginMsg(player)));
		}else{
			e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLoginLogoutMsg().getLoginPrefix() + plugin.getLoginLogoutMsg().getLoginMsg(player)));
		}
	}
	
	@EventHandler
	public void onLogoff(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PlayerStorageManager smgr = new PlayerStorageManager(player);
		smgr.finalSave(plugin.getHomeMap().get(player.getUniqueId()));
		if((player.hasPermission("mcdt.custommsg") || player.isOp()) && !smgr.getLogoutMsg(player).isEmpty()){
			event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLoginLogoutMsg().getLogoutPrefix() + smgr.getLogoutMsg(player)));
		}else{
			event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLoginLogoutMsg().getLogoutPrefix() + plugin.getLoginLogoutMsg().getLogoutMsg(player)));
		}
	}
	
	@EventHandler
	public void onBedLeave(PlayerBedLeaveEvent event) {
		Player player = event.getPlayer();
		Location loc = player.getLocation();
		plugin.getHomeMapUser(player.getUniqueId()).put("bed", loc);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (player == null) {
			return;
		}
		if (!player.hasPermission("modifyworld")) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You do not have modifyworld permission");
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (player == null) {
			return;
		}
		if (!player.hasPermission("modifyworld")) {
			event.setCancelled(true);
			if ((plugin.api != null) && (plugin.api.getUserManager().getUser(player.getUniqueId()).getPrimaryGroup() != null)) {
				player.sendMessage(plugin.api.getUserManager().getUser(player.getUniqueId()).getPrimaryGroup()
						+ ChatColor.GOLD + "'s aren't able to modify the world! Join our Discord to get ranked up by an Admin!"
						+ ChatColor.WHITE + " https://discord.gg/0yqnmBJp5owYsJKM");
			}else {
				player.sendMessage(ChatColor.RED + "You do not have modifyworld permission");
			}
		}
	}
	
	@EventHandler
	public void onItemUse(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (!player.hasPermission("modifyworld") && !player.getEquipment().getItemInMainHand().equals(null)) {
			ItemStack item = player.getEquipment().getItemInMainHand();
			if (checkItem(item)){
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You do not have modifyworld permission");
			}
		}
	}

	@EventHandler
	public void onEntityRightClick(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		
		if (!player.hasPermission("modifyworld")) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You do not have modifyworld permission");
		}
	}
	
	private boolean checkItem(ItemStack item) {
		Material mat = item.getType();
		switch(mat) {
		case FLINT_AND_STEEL:
		case OAK_BOAT:
		case ACACIA_BOAT:
		case BIRCH_BOAT:
		case SPRUCE_BOAT:
		case JUNGLE_BOAT:
		case DARK_OAK_BOAT:
		case MINECART:
		case COMMAND_BLOCK_MINECART:
		case TNT_MINECART:
		case CHEST_MINECART:
		case FURNACE_MINECART:
		case HOPPER_MINECART:
		case EGG:
		case EXPERIENCE_BOTTLE:
		case SPLASH_POTION:
		case LINGERING_POTION:
		case ENDER_EYE:
		case FIREWORK_ROCKET:
		case ITEM_FRAME:
		case LEAD:
		case STRING:
		case WOODEN_AXE:
		case STONE_AXE:
		case GOLDEN_AXE:
		case DIAMOND_AXE:
		case SHEARS:
		case BUCKET:
		case LAVA_BUCKET:
		case WATER_BUCKET:
			return true;
		default:
			return false;
		}
	}
}
