package tld.sima.mcbtp;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.LuckPermsApi;
import net.md_5.bungee.api.ChatColor;
import tld.sima.mcbtp.commands.HomeCmds;
import tld.sima.mcbtp.commands.InventoryCmds;
import tld.sima.mcbtp.commands.MessageCmds;
import tld.sima.mcbtp.commands.PlayerCmds;
import tld.sima.mcbtp.commands.SpawnCmds;
import tld.sima.mcbtp.commands.TeleportCmds;
import tld.sima.mcbtp.files.PlayerStorageManager;
import tld.sima.mcbtp.files.ServerSettingStorageManager;

public class Main extends JavaPlugin {
	
	private HashMap<UUID, Location> locBackUUID = new HashMap<UUID, Location>();
	private HashMap<UUID, HashMap<String, Location>> homeMap = new HashMap<UUID, HashMap<String, Location>>();
	private HashMap<UUID, Location> tempLocationUUIDMap = new HashMap<UUID, Location>();
	private HashMap<UUID, UUID> tpaRequest = new HashMap<UUID, UUID> ();
	private HashMap<String, Location> spawnLoc = new HashMap<String, Location>();
	private Set<UUID> adminChat = new HashSet<UUID>();
	private Set<UUID> godmode = new HashSet<UUID>();
	private Set<UUID> pvpflag = new HashSet<UUID>();
	public HashMap<UUID, BukkitTask> pvptask = new HashMap<UUID, BukkitTask>();
	public LuckPermsApi api = null;

	@Override
	public void onEnable() {
		
		SpawnCmds spawncmd = new SpawnCmds();
		this.getCommand(spawncmd.cmd1).setExecutor(spawncmd);
		this.getCommand(spawncmd.cmd2).setExecutor(spawncmd);
		this.getCommand(spawncmd.cmd3).setExecutor(spawncmd);
		this.getCommand(spawncmd.cmd4).setExecutor(spawncmd);
		
		MessageCmds msgcmd = new MessageCmds();
		this.getCommand(msgcmd.cmd1).setExecutor(msgcmd);
		this.getCommand(msgcmd.cmd2).setExecutor(msgcmd);
		this.getCommand(msgcmd.cmd3).setExecutor(msgcmd);
		this.getCommand(msgcmd.cmd4).setExecutor(msgcmd);
		this.getCommand(msgcmd.cmd5).setExecutor(msgcmd);
		this.getCommand(msgcmd.cmd6).setExecutor(msgcmd);
		this.getCommand(msgcmd.cmd7).setExecutor(msgcmd);
		
		HomeCmds homecmd = new HomeCmds();
		this.getCommand(homecmd.cmd1).setExecutor(homecmd);
		this.getCommand(homecmd.cmd2).setExecutor(homecmd);
		this.getCommand(homecmd.cmd3).setExecutor(homecmd);
		this.getCommand(homecmd.cmd4).setExecutor(homecmd);

		InventoryCmds invcmd = new InventoryCmds();
		this.getCommand(invcmd.cmd1).setExecutor(invcmd);
		this.getCommand(invcmd.cmd2).setExecutor(invcmd);
		
		TeleportCmds tpcmd = new TeleportCmds();
		this.getCommand(tpcmd.cmd1).setExecutor(tpcmd);
		this.getCommand(tpcmd.cmd2).setExecutor(tpcmd);
		this.getCommand(tpcmd.cmd3).setExecutor(tpcmd);
		this.getCommand(tpcmd.cmd4).setExecutor(tpcmd);
		this.getCommand(tpcmd.cmd5).setExecutor(tpcmd);
		this.getCommand(tpcmd.cmd6).setExecutor(tpcmd);
		
		PlayerCmds playercmd = new PlayerCmds();
		this.getCommand(playercmd.cmd1).setExecutor(playercmd);
		this.getCommand(playercmd.cmd2).setExecutor(playercmd);
		this.getCommand(playercmd.cmd3).setExecutor(playercmd);
		this.getCommand(playercmd.cmd4).setExecutor(playercmd);
		this.getCommand(playercmd.cmd5).setExecutor(playercmd);
		this.getCommand(playercmd.cmd6).setExecutor(playercmd);
		
		getServer().getPluginManager().registerEvents(new EventManager(), this);
		
		getHomes();
		
		RegisteredServiceProvider<LuckPermsApi> provider = Bukkit.getServicesManager().getRegistration(LuckPermsApi.class);
		if (provider != null) {
		    api = provider.getProvider();
		    
		    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "LuckyPerms found!");
		    Set<Group> groups = api.getGroups();
		    ServerSettingStorageManager sssm = new ServerSettingStorageManager();
		    sssm.setup();
		    sssm.getAllLocations(groups);
		    
		    if (!spawnLoc.containsKey("default")) {
		    	Location loc = Bukkit.getWorlds().get(0).getSpawnLocation();
		    	spawnLoc.put("default", loc);
		    }
		}
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.hasPermission("mcdt.adminchat") || player.isOp()) {
				this.getAdminChatMap().add(player.getUniqueId());
			}
			player.getStatistic(Statistic.PLAY_ONE_TICK);
		}
		
		this.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "MCBDT Enabled");
	}
	
	@Override
	public void onDisable() {
		Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
		for (Player player : players) {
			PlayerStorageManager smgr = new PlayerStorageManager();
			smgr.setup(player);
			smgr.finalSave(homeMap.get(player.getUniqueId()));
			smgr.savePVPflag(pvpflag.contains(player.getUniqueId()));
		}
		tpaRequest.clear();
		locBackUUID.clear();
		tempLocationUUIDMap.clear();
		adminChat.clear();
		locBackUUID.clear();
		homeMap.clear();
		if (api != null) {
		    ServerSettingStorageManager sssm = new ServerSettingStorageManager();
		    sssm.setup();
		    sssm.saveAllLocations(spawnLoc);
		}
	}
	
	private void getHomes() {
		Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
		for (Player player : players) {
			PlayerStorageManager smgr = new PlayerStorageManager();
			smgr.setup(player);
			HashMap<String, Location> locMap = smgr.getMap();
			this.homeMap.put(player.getUniqueId(), locMap);
			if(smgr.getPVPflag()) {
				pvpflag.add(player.getUniqueId());
			}
		}
	}
	
	public Set<UUID> getPVPSet(){
		return pvpflag;
	}
	
	public HashMap<String, Location> getSpawnMap(){
		return spawnLoc;
	}
	
	public Set<UUID> getGodSet(){
		return godmode;
	}
	
	public Set<UUID> getAdminChatMap(){
		return adminChat;
	}
	
	public HashMap<UUID, HashMap<String, Location>> getHomeMap(){
		return homeMap;
	}
	
	public void setHomeMap(UUID uuid, HashMap<String, Location> map) {
		homeMap.put(uuid, map);
	}
	
	public HashMap<String, Location> getHomeMapUser(UUID uuid){
		return homeMap.get(uuid);
	}
	
	public HashMap<UUID, Location> getBackMap(){
		return locBackUUID;
	}
	
	public HashMap<UUID, Location> getTempMap(){
		return tempLocationUUIDMap;
	}
	
	public HashMap<UUID, UUID> getTpaMap(){
		return tpaRequest;
	}
}
