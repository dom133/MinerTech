package Players;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.Inventory;

import Lib.Config;

import com.dom133.minertech.MinerTech;

public class PlayerEvents implements Listener {
	
	private MinerTech plugin;
	public PlayerEvents(MinerTech plugin)
	{
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void addExp(Player player, Material block)
	{
		int exp = MinerTech.getExps().get(player.getDisplayName());
		if(MinerTech.getExpMax().get(MinerTech.getLevels().get(player.getDisplayName()))!=null | MinerTech.getLevels().get(player.getDisplayName())==0)
		{
			if(player.hasPermission("mt.vip"))
			{
				exp+=MinerTech.getExpBlockVip().get(block);
			}
			else
			{
				exp+=MinerTech.getExpBlock().get(block);
			}
			MinerTech.getExps().put(player.getDisplayName(), exp);
		}
	}
		
	public void addLvl(Player player)
	{
		int exp = MinerTech.getExps().get(player.getDisplayName());
		int lvl = MinerTech.getLevels().get(player.getDisplayName());
		//player.sendMessage("Exp:"+exp+" Lvl:"+lvl+" Exp:"+plugin.exp_max.get(lvl+1));
		
		if(MinerTech.getExpMax().get(lvl+1)!=null)
		{
			if(exp>=MinerTech.getExpMax().get(lvl+1) & exp<MinerTech.getExpMax().get(lvl+2) & lvl!=lvl+1)
			{
				MinerTech.getLevels().put(player.getDisplayName(), lvl+1);
				if(player.hasPermission("mt.vip"))
				{
					if(MinerTech.getPlayerRewards().get(player.getDisplayName())==null | MinerTech.getPlayerRewards().get(player.getDisplayName())=="empty")MinerTech.getPlayerRewards().put(player.getDisplayName(), MinerTech.getExpReward().get(lvl+1).toString()+"["+MinerTech.getExpRvalue().get(lvl+1)+"]"+","+MinerTech.getVipExpReward().get(lvl+1).toString()+"["+MinerTech.getVipExpRvalue().get(lvl+1)+"]");
					else
					{
						String rewards = MinerTech.getPlayerRewards().get(player.getDisplayName());
						MinerTech.getPlayerRewards().put(player.getDisplayName(), MinerTech.getExpReward().get(lvl+1).toString()+"["+MinerTech.getExpRvalue().get(lvl+1)+"]"+","+MinerTech.getVipExpReward().get(lvl+1).toString()+"["+MinerTech.getVipExpRvalue().get(lvl+1)+"]"+","+rewards);
					}
				}
				else
				{
					if(MinerTech.getPlayerRewards().get(player.getDisplayName())==null | MinerTech.getPlayerRewards().get(player.getDisplayName())=="empty")MinerTech.getPlayerRewards().put(player.getDisplayName(), MinerTech.getExpReward().get(lvl+1).toString()+"["+MinerTech.getExpRvalue().get(lvl+1)+"]");
					else
					{
						String rewards = MinerTech.getPlayerRewards().get(player.getDisplayName());
						MinerTech.getPlayerRewards().put(player.getDisplayName(), MinerTech.getExpReward().get(lvl+1).toString()+"["+MinerTech.getExpRvalue().get(lvl+1)+"]"+","+rewards);
					}
				}
				player.sendMessage(MinerTech.prefix+"You have a new lvl, to collect the prize entry /mt reward");	
			}
		}		
	}
	
	public void addPlayerFile(Player player)
	{
		Config.getConfig("player").set(player.getUniqueId().toString()+".nick", player.getDisplayName());
		Config.getConfig("player").set(player.getUniqueId().toString()+".lvl", 0);
		Config.getConfig("player").set(player.getUniqueId().toString()+".exp", 0);
		Config.getConfig("player").set(player.getUniqueId().toString()+".rewards", "empty");
		Config.save("player");
		Config.load("player");
	}
	
	public void addPlayerDB(Player player)
	{
		String query = "INSERT INTO minertech (UUID, FIRST_NICK, EXP, LVL, REWARDS)"+
						"VALUES ('"+player.getUniqueId().toString()+"', '"+player.getDisplayName()+"', '0', '0', '')";
		plugin.con.execute(query);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		if(MinerTech.type.equals("mysql"))
		{
			String ufdb = plugin.con.Querry("SELECT * FROM minertech WHERE `UUID` = '"+player.getUniqueId().toString()+"';", 1);
			if(ufdb==null)
			{
				MinerTech.getExps().put(player.getDisplayName(), 0);
				MinerTech.getLevels().put(player.getDisplayName(), 0);
				MinerTech.getPlayerRewards().put(player.getDisplayName(), "empty");
				//System.out.println("Player: "+player.getDisplayName()+" Exp: "+MinerTech.getExp(player)+" Lvl: "+MinerTech.getLvl(player)+" Reward: "+MinerTech.getPlayerReward(player));
				addPlayerDB(player);
			}
			else
			{
				Integer exp = Integer.valueOf(plugin.con.Querry("SELECT * FROM minertech WHERE `UUID` = '"+player.getUniqueId().toString()+"';", 4));
				Integer lvl = Integer.valueOf(plugin.con.Querry("SELECT * FROM minertech WHERE `UUID` = '"+player.getUniqueId().toString()+"';", 5));
				String rewards = plugin.con.Querry("SELECT * FROM minertech WHERE `UUID` = '"+player.getUniqueId().toString()+"';", 6);
				MinerTech.getExps().put(player.getDisplayName(), exp);
				MinerTech.getLevels().put(player.getDisplayName(), lvl);
				MinerTech.getPlayerRewards().put(player.getDisplayName(), rewards);
				//System.out.println("Player: "+player.getDisplayName()+" Exp: "+MinerTech.getExp(player)+" Lvl: "+MinerTech.getLvl(player)+" Reward: "+MinerTech.getPlayerReward(player));
				//System.out.println("Player: "+player.getDisplayName()+" Exp: "+exp+ "Lvl: "+lvl+"Reward: "+rewards);
			}
		}
		else
		{
			Config.load("player");
			if(Config.getConfig("player").getString(player.getUniqueId().toString())!=null)
			{
				MinerTech.getExps().put(player.getDisplayName(), Config.getConfig("player").getInt(player.getUniqueId().toString()+".exp"));
				MinerTech.getLevels().put(player.getDisplayName(), Config.getConfig("player").getInt(player.getUniqueId().toString()+".lvl"));
				MinerTech.getPlayerRewards().put(player.getDisplayName(), Config.getConfig("player").getString(player.getUniqueId().toString()+".rewards"));
			}
			else
			{
				MinerTech.getExps().put(player.getDisplayName(), 0);
				MinerTech.getLevels().put(player.getDisplayName(), 0);
				MinerTech.getPlayerRewards().put(player.getDisplayName(), "empty");
				addPlayerFile(player);
			}
		}
	}
	
	@EventHandler
	public void onExit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		if(MinerTech.type.equals("mysql"))
		{
			//plugin.getExp(player);
			plugin.con.update("UPDATE minertech SET `EXP`='"+MinerTech.getExps().get(player.getDisplayName())+"' WHERE `UUID`='"+player.getUniqueId().toString()+"'");
			plugin.con.update("UPDATE minertech SET `LVL`='"+MinerTech.getLevels().get(player.getDisplayName())+"' WHERE `UUID`='"+player.getUniqueId().toString()+"'");
			plugin.con.update("UPDATE minertech SET `REWARDS`='"+MinerTech.getPlayerRewards().get(player.getDisplayName())+"' WHERE `UUID`='"+player.getUniqueId().toString()+"'");
		}
		else
		{
			Config.getConfig("player").set(player.getUniqueId().toString()+".lvl", MinerTech.getLevels().get(player.getDisplayName()));
			Config.getConfig("player").set(player.getUniqueId().toString()+".exp", MinerTech.getExps().get(player.getDisplayName()));
			Config.getConfig("player").set(player.getUniqueId().toString()+".rewards", MinerTech.getPlayerRewards().get(player.getDisplayName()));
			Config.save("player");
		}	
		//plugin.levels.remove(player.getDisplayName());
		//plugin.exps.remove(player.getDisplayName());
		//plugin.player_rewards.remove(player.getDisplayName());
	}
		
	@EventHandler
	public void onBreak(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		if(player.hasPermission("mt.exp"))
		{
			Block block = event.getBlock();
			addExp(player, block.getType());
			addLvl(player);
		}
	}
	
	@EventHandler
 	public void onInv(InventoryCloseEvent event)
 	{
		Inventory inv = event.getInventory();
		if(inv.getName().equals(MinerTech.inv.getName()))
		{
			inv.clear();
			event.getPlayer().sendMessage(MinerTech.prefix+"The reward was received");
		}
 	}
	
	@EventHandler
	public void onServerCommand(ServerCommandEvent event)
	{
		if(event.getCommand().equals("stop") | event.getCommand().equals("reload"))
		{
			System.out.println("[MinerTech] Saving players");
			for(Player player : Bukkit.getOnlinePlayers())
			{
				if(MinerTech.type.equals("mysql"))
				{
					plugin.con.update("UPDATE minertech SET `EXP`='"+MinerTech.getExps().get(player.getDisplayName())+"' WHERE `UUID`='"+player.getUniqueId().toString()+"'");
					plugin.con.update("UPDATE minertech SET `LVL`='"+MinerTech.getLevels().get(player.getDisplayName())+"' WHERE `UUID`='"+player.getUniqueId().toString()+"'");
					plugin.con.update("UPDATE minertech SET `REWARDS`='"+MinerTech.getPlayerRewards().get(player.getDisplayName())+"' WHERE `UUID`='"+player.getUniqueId().toString()+"'");
				}
				else
				{
					Config.getConfig("player").set(player.getUniqueId().toString()+".lvl", MinerTech.getExps().get(player.getDisplayName()));
					Config.getConfig("player").set(player.getUniqueId().toString()+".exp", MinerTech.getLevels().get(player.getDisplayName()));
					Config.getConfig("player").set(player.getUniqueId().toString()+".rewards", MinerTech.getPlayerRewards().get(player.getDisplayName()));
					Config.save("player");
				}	
			}
		}
	}
}