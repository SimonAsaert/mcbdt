package tld.sima.mcbtp;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import tld.sima.mcbtp.files.PlayerStorageManager;

public class EventManager implements Listener {
	
	private Main plugin = Main.getPlugin(Main.class);
	
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
		if ((event.getDamager() instanceof Player) && (event.getEntity() instanceof Player)) {
			Player damager = (Player) event.getDamager();
			Player damagee = (Player) event.getEntity();
			if(plugin.getPVPSet().contains(damager.getUniqueId()) || plugin.getPVPSet().contains(damagee.getUniqueId())) {
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
	public void onFoodChante(FoodLevelChangeEvent event) {
		if ((event.getEntity() instanceof Player) && plugin.getGodSet().contains(event.getEntity().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		PlayerStorageManager smgr = new PlayerStorageManager();
		smgr.setup(player);
		HashMap<String, Location> locMap = smgr.getMap();
		plugin.getHomeMap().put(player.getUniqueId(), locMap);
		
		if (player.hasPermission("mcdt.adminchat") || player.isOp()) {
			plugin.getAdminChatMap().add(player.getUniqueId());
		}
	}
	
	@EventHandler
	public void onLogoff(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PlayerStorageManager smgr = new PlayerStorageManager();
		smgr.setup(player);
		smgr.finalSave(plugin.getHomeMap().get(player.getUniqueId()));
		
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
			if ((plugin.api != null) && (plugin.api.getUser(player.getUniqueId()).getPrimaryGroup() != null)) {
				player.sendMessage(plugin.api.getUser(player.getUniqueId()).getPrimaryGroup()
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
		ItemStack[] checker = new ItemStack[33];
		checker[0] = new ItemStack(Material.FLINT_AND_STEEL);
		checker[1] = new ItemStack(Material.BOAT);
		checker[2] = new ItemStack(Material.BOAT_ACACIA);
		checker[3] = new ItemStack(Material.BOAT_BIRCH);
		checker[4] = new ItemStack(Material.BOAT_DARK_OAK);
		checker[5] = new ItemStack(Material.BOAT_JUNGLE);
		checker[6] = new ItemStack(Material.BOAT_SPRUCE);
		checker[7] = new ItemStack(Material.MINECART);
		checker[8] = new ItemStack(Material.COMMAND_MINECART);
		checker[9] = new ItemStack(Material.EXPLOSIVE_MINECART);
		checker[10] = new ItemStack(Material.HOPPER_MINECART);
		checker[11] = new ItemStack(Material.POWERED_MINECART);
		checker[12] = new ItemStack(Material.STORAGE_MINECART);
		checker[14] = new ItemStack(Material.EGG);
		checker[15] = new ItemStack(Material.EXP_BOTTLE);
		checker[16] = new ItemStack(Material.SPLASH_POTION);
		checker[17] = new ItemStack(Material.LINGERING_POTION);
		checker[18] = new ItemStack(Material.EYE_OF_ENDER);
		checker[19] = new ItemStack(Material.FIREWORK);
		checker[20] = new ItemStack(Material.ITEM_FRAME);
		checker[21] = new ItemStack(Material.LEASH);
		checker[22] = new ItemStack(Material.MONSTER_EGG);
		checker[23] = new ItemStack(Material.MONSTER_EGGS);
		checker[24] = new ItemStack(Material.STRING);
		checker[27] = new ItemStack(Material.WOOD_AXE);
		checker[26] = new ItemStack(Material.STONE_AXE);
		checker[25] = new ItemStack(Material.GOLD_AXE);
		checker[28] = new ItemStack(Material.DIAMOND_AXE);
		checker[29] = new ItemStack(Material.SHEARS);
		checker[30] = new ItemStack(Material.BUCKET);
		checker[31] = new ItemStack(Material.LAVA_BUCKET);
		checker[32] = new ItemStack(Material.WATER_BUCKET);
		for (ItemStack check : checker) {
			if(item.isSimilar(check)) {
				return true;
			}
		}
		return false;
	}
}
