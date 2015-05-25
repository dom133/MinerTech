package Lib;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.dom133.minertech.MinerTech;

public class AutoSave extends BukkitRunnable{
	
	private MinerTech plugin;
	
	public AutoSave(MinerTech plugin)
	{
		this.plugin = plugin;
	}
	
	
	@Override
	public void run() {
		if(MinerTech.type.equals("mysql"))
		{
			for(Player player : Bukkit.getOnlinePlayers())
			{
				plugin.con.update("UPDATE minertech SET `EXP`='"+MinerTech.getExps().get(player.getDisplayName())+"' WHERE `UUID`='"+player.getUniqueId().toString()+"'");
				plugin.con.update("UPDATE minertech SET `LVL`='"+MinerTech.getLevels().get(player.getDisplayName())+"' WHERE `UUID`='"+player.getUniqueId().toString()+"'");
				plugin.con.update("UPDATE minertech SET `REWARDS`='"+MinerTech.getPlayerRewards().get(player.getDisplayName())+"' WHERE `UUID`='"+player.getUniqueId().toString()+"'");
			}
		}
		else
		{
			for(Player player : Bukkit.getOnlinePlayers())
			{
				Config.getConfig("player").set(player.getUniqueId().toString()+".lvl", MinerTech.getExps().get(player.getDisplayName()));
				Config.getConfig("player").set(player.getUniqueId().toString()+".exp", MinerTech.getLevels().get(player.getDisplayName()));
				Config.getConfig("player").set(player.getUniqueId().toString()+".rewards", MinerTech.getPlayerRewards().get(player.getDisplayName()));
				Config.save("player");
				Config.load("player");
			}			
		}
	}

}
