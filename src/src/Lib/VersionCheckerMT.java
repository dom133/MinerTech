package Lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

import com.dom133.minertech.MinerTech;


public class VersionCheckerMT {
	private MinerTech plugin;
	
	public VersionCheckerMT(MinerTech plugin)
	{
		this.plugin = plugin;
		currentVersion = plugin.getDescription().getVersion();
	}
	
	private String currentVersion;
	private String readurl = "https://rawgit.com/dom133/MinerTech/master/version.txt";
	
	public void checker()
	{
		if(Config.getConfig("config").getString("Update.checker")==null)
		{
			Config.getConfig("config").set("Update.checker", true);
			Config.save("config");
		}
		MinerTech.checker = Config.getConfig("config").getBoolean("Update.checker");
	}
			
	
	public void startUpdateCheck() {
    	checker();
        if (MinerTech.checker==true) {
            Logger log = plugin.getLogger();
            try {
                log.info("Checking is avaible new version...");
                URL url = new URL(readurl);
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                String str;
                while ((str = br.readLine()) != null) {
                	String line = str;
                	if(line.equals(currentVersion)==false)
                	{
                		log.info("New version is avaible!!!");
                	}
                }
                br.close();
            } catch (IOException e) {
                log.severe("The UpdateChecker URL is invalid! Please let me know!");
            }
        }
    }
	
	public String rawMessage(String url1) {
            try {
                URL url = new URL(url1);
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                String str;
                while ((str = br.readLine()) != null) {
                	String line = str;
                	return line;
                }
                br.close();
            } catch (IOException e) {
            	return null;
            }
		return null;
    }
}
