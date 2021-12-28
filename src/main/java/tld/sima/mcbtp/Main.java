package tld.sima.mcbtp;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.GroupManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import net.md_5.bungee.api.ChatColor;
import tld.sima.mcbtp.commands.AdminCmds;
import tld.sima.mcbtp.commands.HomeCmds;
import tld.sima.mcbtp.commands.InventoryCmds;
import tld.sima.mcbtp.commands.MessageCmds;
import tld.sima.mcbtp.commands.PlayerCmds;
import tld.sima.mcbtp.commands.SpawnCmds;
import tld.sima.mcbtp.commands.TeleportCmds;
import tld.sima.mcbtp.files.PlayerStorageManager;
import tld.sima.mcbtp.files.ServerSettingStorageManager;

public class Main extends JavaPlugin {
	
	private final HashMap<UUID, Location> locBackUUID = new HashMap<UUID, Location>();
	private final HashMap<UUID, HashMap<String, Location>> homeMap = new HashMap<UUID, HashMap<String, Location>>();
	private final HashMap<UUID, Location> tempLocationUUIDMap = new HashMap<UUID, Location>();
	private final HashMap<UUID, UUID> tpaRequest = new HashMap<UUID, UUID> ();
	private final HashMap<String, Location> spawnLoc = new HashMap<String, Location>();
	private final Set<UUID> adminChat = new HashSet<UUID>();
	private final Set<UUID> godmode = new HashSet<UUID>();
	private final Set<UUID> pvpflag = new HashSet<UUID>();
	private LoginLogoutMsg loginLogoutMsg;
	public final HashMap<UUID, BukkitTask> pvptask = new HashMap<UUID, BukkitTask>();
	public LuckPerms api = null;

	@Override
	public void onEnable() {
		
		AdminCmds adminCMD = new AdminCmds();
		this.getCommand(adminCMD.cmd1).setExecutor(adminCMD);
		this.getCommand(adminCMD.cmd2).setExecutor(adminCMD);
		this.getCommand(adminCMD.cmd3).setExecutor(adminCMD);
		this.getCommand(adminCMD.cmd4).setExecutor(adminCMD);
		this.getCommand(adminCMD.cmd5).setExecutor(adminCMD);
		this.getCommand(adminCMD.cmd6).setExecutor(adminCMD);
		
		SpawnCmds spawnCMD = new SpawnCmds();
		this.getCommand(spawnCMD.spawnCmd).setExecutor(spawnCMD);
		this.getCommand(spawnCMD.setSpawnCmd).setExecutor(spawnCMD);
		this.getCommand(spawnCMD.mcbConfirmCmd).setExecutor(spawnCMD);
		this.getCommand(spawnCMD.delSpawnCmd).setExecutor(spawnCMD);
		
		MessageCmds msgCMD = new MessageCmds();
		this.getCommand(msgCMD.cmd1).setExecutor(msgCMD);
		this.getCommand(msgCMD.cmd2).setExecutor(msgCMD);
		this.getCommand(msgCMD.cmd3).setExecutor(msgCMD);
		this.getCommand(msgCMD.cmd4).setExecutor(msgCMD);
		this.getCommand(msgCMD.cmd5).setExecutor(msgCMD);
		this.getCommand(msgCMD.cmd6).setExecutor(msgCMD);
		this.getCommand(msgCMD.cmd7).setExecutor(msgCMD);
		this.getCommand(msgCMD.setJoinMsg).setExecutor(msgCMD);
		this.getCommand(msgCMD.setLeaveMsg).setExecutor(msgCMD);
		
		HomeCmds homecmd = new HomeCmds();
		this.getCommand(homecmd.cmd1).setExecutor(homecmd);
		this.getCommand(homecmd.cmd2).setExecutor(homecmd);
		this.getCommand(homecmd.cmd3).setExecutor(homecmd);
		this.getCommand(homecmd.cmd4).setExecutor(homecmd);

		InventoryCmds invcmd = new InventoryCmds();
		this.getCommand(invcmd.cmd1).setExecutor(invcmd);
		this.getCommand(invcmd.cmd2).setExecutor(invcmd);
		this.getCommand(invcmd.cmd3).setExecutor(invcmd);
		
		TeleportCmds tpcmd = new TeleportCmds();
		this.getCommand(tpcmd.tppos).setExecutor(tpcmd);
		this.getCommand(tpcmd.getpos).setExecutor(tpcmd);
		this.getCommand(tpcmd.tpa).setExecutor(tpcmd);
		this.getCommand(tpcmd.tpaccept).setExecutor(tpcmd);
		this.getCommand(tpcmd.tpdeny).setExecutor(tpcmd);
		this.getCommand(tpcmd.tpall).setExecutor(tpcmd);
		
		PlayerCmds playercmd = new PlayerCmds();
		this.getCommand(playercmd.cmd1).setExecutor(playercmd);
		this.getCommand(playercmd.cmd2).setExecutor(playercmd);
		this.getCommand(playercmd.cmd3).setExecutor(playercmd);
		this.getCommand(playercmd.cmd4).setExecutor(playercmd);
		this.getCommand(playercmd.cmd5).setExecutor(playercmd);
		this.getCommand(playercmd.cmd6).setExecutor(playercmd);
		
		getServer().getPluginManager().registerEvents(new EventManager(), this);
		
		getHomes();

		ServerSettingStorageManager sssm = new ServerSettingStorageManager();
		sssm.setup();

		if(this.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
			RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
			if (provider != null) {
			    api = LuckPermsProvider.get();
			    
			    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "LuckPerms found!");
			    GroupManager groups = api.getGroupManager();
			    sssm.getAllLocations(groups);
			    
			    if (!spawnLoc.containsKey("default")) {
			    	Location loc = Bukkit.getWorlds().get(0).getSpawnLocation();
			    	spawnLoc.put("default", loc);
			    }
			}
		}

		this.loginLogoutMsg = new LoginLogoutMsg(sssm.getDefaultLoginMsg(), sssm.getDefaultLogoutMsg(), sssm.getLoginPrefixMsg(), sssm.getLogoutPrefixMsg());
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.hasPermission("mcdt.adminchat") || player.isOp()) {
				this.getAdminChatMap().add(player.getUniqueId());
			}
		}
		
		this.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "MCBDT Enabled");
	}
	
	@Override
	public void onDisable() {
		Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
		for (Player player : players) {
			PlayerStorageManager smgr = new PlayerStorageManager(player);
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
			PlayerStorageManager smgr = new PlayerStorageManager(player);
			HashMap<String, Location> locMap = smgr.getMap();
			this.homeMap.put(player.getUniqueId(), locMap);
			if(smgr.getPVPflag()) {
				pvpflag.add(player.getUniqueId());
			}
		}
	}

	public LoginLogoutMsg getLoginLogoutMsg(){
		return loginLogoutMsg;
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
