package com.dom133.minertech;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.mcstats.Metrics;

import Lib.AutoSave;
import Lib.Config;
import Lib.VersionCheckerMT;
import MySQL.MySQL;
import MySQL.MySQL_Reconnect;
import Players.Commands;
import Players.PlayerEvents;

public class MinerTech extends JavaPlugin implements Listener {
	
	private static final Map<String, Integer> player_exp = new HashMap<String, Integer>();
	private static final Map<Integer, Integer> exp_max = new HashMap<Integer, Integer>();
	private static final Map<Integer, Material> exp_reward = new HashMap<Integer, Material>();
	private static final Map<Integer, Integer> exp_rvalue = new HashMap<Integer, Integer>();
	private static final Map<Integer, Material> vip_exp_reward = new HashMap<Integer, Material>();
	private static final Map<Integer, Integer> vip_exp_rvalue = new HashMap<Integer, Integer>();
	private static final Map<String, String> player_rewards = new HashMap<String, String>();
	private static final Map<String, Integer> player_level = new HashMap<String, Integer>();
	private static final Map<Material, Integer> exp_block = new HashMap<Material, Integer>();
	private static final Map<Material, Integer> exp_block_vip = new HashMap<Material, Integer>();
	
	public MySQL con = new MySQL(this);
	String sql = "CREATE TABLE IF NOT EXISTS minertech " +
            "(ID INTEGER NOT NULL AUTO_INCREMENT, " +
            " UUID VARCHAR(255), " + 
            " FIRST_NICK VARCHAR(255), " +
            " EXP VARCHAR(255), " + 
            " LVL VARCHAR(255), " + 
            " REWARDS VARCHAR(255), " +
            " ACHIEVEMENT VARCHAR(255), " +
            " PRIMARY KEY ( ID ))";
	public static String mysql_user;
	public static String mysql_ip;
	public static String mysql_password;
	public static String mysql_port;
	public static String mysql_database;
	public static String type;
	public static boolean checker;
	public VersionCheckerMT vc = new VersionCheckerMT(this);
	public static int inv_v = 9;
	public static Inventory inv = Bukkit.createInventory(null, inv_v, "Reward");
	public static PlayerEvents pe;
	//Messages
	public static String prefix = ChatColor.BLUE+"["+ChatColor.GREEN+"MinerTech"+ChatColor.BLUE+"]";
	public static String nonperm = ChatColor.RED+"Do not have an sufficient permission";
	
	public static final Map<String, String> getPlayerRewards() {
		return player_rewards;
	}

	public static Map<String, Integer> getExps() {
		return player_exp;
	}

	public static final Map<String, Integer> getLevels() {
		return player_level;
	}

	public static final Map<Integer, Integer> getVipExpRvalue() {
		return vip_exp_rvalue;
	}

	public static final Map<Integer, Material> getVipExpReward() {
		return vip_exp_reward;
	}

	public static final Map<Integer, Integer> getExpRvalue() {
		return exp_rvalue;
	}

	public static final Map<Material, Integer> getExpBlock() {
		return exp_block;
	}

	public static final Map<Integer, Integer> getExpMax() {
		return exp_max;
	}

	public static final Map<Material, Integer> getExpBlockVip() {
		return exp_block_vip;
	}

	public static final Map<Integer, Material> getExpReward() {
		return exp_reward;
	}

	public static final Map<String, Integer> getMapLevel()
	{
		return getLevels();
	}
	
	public boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
	
	public void Metrics()
	{
		try {
	        Metrics metrics = new Metrics(this);
	        metrics.start();
	    } catch (IOException e) {
	        // Failed to submit the stats :-(
	    }
	}
	
	@SuppressWarnings("deprecation")
	public void LoadBlocks()
	{
		int i = 1;
		System.out.println("[MinerTech] Loading blocks confiig");
		while(true)
		{

			Material name = Material.getMaterial(i);
			getExpBlock().put(name, Config.getConfig("blocks").getInt(name+".exp"));
			getExpBlockVip().put(name, Config.getConfig("blocks").getInt(name+".vip"));
			i++;
			if(name==null)break;
		}		
	}
	
	@SuppressWarnings("deprecation")
	public void LoadLvls()
	{
		int i = 1;
		System.out.println("[MinerTech] Loading levels confiig");
		while(true)
		{
			if(Config.getConfig("lvl").getString("Levels."+i)!=null)
			{
				getExpMax().put(i, Config.getConfig("lvl").getInt("Levels."+i+".max"));
				getExpReward().put(i, Material.getMaterial(Config.getConfig("lvl").getInt("Levels."+i+".reward")));
				getExpRvalue().put(i, Config.getConfig("lvl").getInt("Levels."+i+".value"));
				getVipExpReward().put(i, Material.getMaterial(Config.getConfig("lvl").getInt("Levels."+i+".vip_reward")));
				getVipExpRvalue().put(i, Config.getConfig("lvl").getInt("Levels."+i+".vip_value"));
				i++;
			}
			else break;
		}	
	}
	
	public void LoadPlayers()
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			if(type.equals("mysql"))
			{
				String ufdb = con.Querry("SELECT * FROM minertech WHERE `UUID` = '"+player.getUniqueId().toString()+"';", 1);
				if(ufdb==null)
				{
					getExps().put(player.getDisplayName(), 0);
					getLevels().put(player.getDisplayName(), 0);
					getPlayerRewards().put(player.getDisplayName(), null);
					pe.addPlayerDB(player);
				}
				else
				{
					Integer exp = Integer.valueOf(con.Querry("SELECT * FROM minertech WHERE `UUID` = '"+player.getUniqueId().toString()+"';", 4));
					Integer lvl = Integer.valueOf(con.Querry("SELECT * FROM minertech WHERE `UUID` = '"+player.getUniqueId().toString()+"';", 5));
					
					getExps().put(player.getDisplayName(), exp );
					getLevels().put(player.getDisplayName(), lvl);
					getPlayerRewards().put(player.getDisplayName(),  con.Querry("SELECT * FROM minertech WHERE `UUID` = '"+player.getUniqueId().toString()+"';", 6));
				}
			}
			else
			{
				Config.load("player");
				if(Config.getConfig("player").getString(player.getUniqueId().toString())!=null)
				{
					getExps().put(player.getDisplayName(), Config.getConfig("player").getInt(player.getUniqueId().toString()+".exp"));
					getLevels().put(player.getDisplayName(), Config.getConfig("player").getInt(player.getUniqueId().toString()+".lvl"));
					getPlayerRewards().put(player.getDisplayName(), Config.getConfig("player").getString(player.getUniqueId().toString()+".rewards"));
				}
				else
				{
					getExps().put(player.getDisplayName(), 0);
					getLevels().put(player.getDisplayName(), 0);
					getPlayerRewards().put(player.getDisplayName(), "empty");
					pe.addPlayerFile(player);
				}
			}
		}		
	}
	
	public void LoadFunction()
	{
		LoadLvls();
		LoadBlocks();
		LoadPlayers();
		Metrics();
		vc.startUpdateCheck();
		@SuppressWarnings("unused")
		BukkitTask task_as = new AutoSave(this).runTaskTimer(this, 1200, 6000);	
		pe = new PlayerEvents(this);
		getCommand("mt").setExecutor(new Commands(this));
		getCommand("minertech").setExecutor(new Commands(this));
	}
			
	@SuppressWarnings("unused")
	@Override
	public void onEnable()
	{
		Config.registerConfig("config", "config.yml", this);
		Config.registerConfig("lvl", "levels.yml", this);
		Config.registerConfig("blocks", "blocks.yml", this);
		Config.loadAll();
		
		if(Config.getConfig("config").getString("DataSource.mySQLSource").toLowerCase().equals("file"))
		{
			Config.registerConfig("player", "players.yml", this);
			Config.load("player");
			type = "file";
			LoadFunction();
		}
		else if(Config.getConfig("config").getString("DataSource.mySQLSource").toLowerCase().equals("mysql"))
		{
			type = "mysql";
	        mysql_ip = Config.getConfig("config").getString("DataSource.mySQLHost");
	        mysql_user = Config.getConfig("config").getString("DataSource.mySQLUsername");
	        mysql_password = Config.getConfig("config").getString("DataSource.mySQLPassword");
	        mysql_port = Config.getConfig("config").getString("DataSource.mySQLPort");
	        mysql_database = Config.getConfig("config").getString("DataSource.mySQLDatabase");
	        
	        getLogger().info("Requesting the mySQL database");
	        if(con.ConnectToMysql(mysql_ip, mysql_user, mysql_password, mysql_port, mysql_database)==true)
	        {
	        	getLogger().info("Properly combined with mySQL database");
	        	con.execute(sql);
	        	LoadFunction();
	        	BukkitTask task_mysql = new MySQL_Reconnect(this).runTaskTimer(this, 1100, 1200);
	        }
	        else
	        {
	        	getLogger().info("Connection with the database impossible, the plugin will be switched off");
	        	getServer().getPluginManager().disablePlugin(this);
	        }
		}
		else
		{
        	getLogger().info("Config is incorrectly configured, the plugin will be switched off");
        	getServer().getPluginManager().disablePlugin(this);
		}
	}
	
	@Override
	public void onDisable()
	{
		if(type.equals("mysql"))
		{
			con.disconnect();
		}
		else
		{
			Config.saveAll();
		}
	}
}
