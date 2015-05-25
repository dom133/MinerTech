package MySQL;

import org.bukkit.scheduler.BukkitRunnable;

import com.dom133.minertech.MinerTech;

public class MySQL_Reconnect extends BukkitRunnable{
	
	private MinerTech plugin;
	public MySQL_Reconnect(MinerTech plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		plugin.con.disconnect();
		plugin.con.ConnectToMysql(MinerTech.mysql_ip, MinerTech.mysql_user, MinerTech.mysql_password, MinerTech.mysql_port, MinerTech.mysql_database);
	}

}