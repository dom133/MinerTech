package MySQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.dom133.minertech.MinerTech;


public class MySQL
{
			@SuppressWarnings("unused")
			private MinerTech plugin;
			public MySQL(MinerTech plugin)
			{
				this.plugin = plugin;
			}
public Connection connect = null;
public Statement statement = null;
  public String host;
			public String port;
  public String user;
  public String passwort;
  public String database;
  
  public void log(String prefix, String msg)
  {
    System.out.println("[" + prefix + "] " + msg);
  }
  
  public boolean connect()
  {
    try
    {
      Class.forName("com.mysql.jdbc.Driver");
      this.connect = 
        DriverManager.getConnection("jdbc:mysql://"+ this.host + ":"+this.port+"/" + 
        this.database+"?autoreconnect=true?useUnicode=yes", this.user, this.passwort);
      if (!this.connect.isClosed())
      {
        this.statement = this.connect.createStatement();
        return true;
      }
    }
    catch (Exception ex)
    {
      log("MinerTech-MySQL", 
        "Connection Error:");
      log("MinerTech-MySQL", ex.getMessage());
    }
    return false;
  }
  
  private void checkConnection()
  {
    if (!isConnected()) {
      connect();
    }
  }
  
  public void execute(String query)
  {
    try
    {
      this.statement.execute(query);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public ResultSet getQuery(String query)
  {
    try
    {
      return this.statement.executeQuery(query);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return null;
  }
  
  public void insert(String query)
  {
    try
    {
      this.statement.execute(query);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public int update(String query)
  {
    try
    {
      return this.statement.executeUpdate(query);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return 0;
  }
  
  public boolean countToBoolean(String query)
  {
    checkConnection();
    ResultSet r = getQuery(query);
    int i = 0;
    if (r != null) {
      try
      {
        while (r.next()) {
          i = r.getInt(1);
        }
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
    if (i == 1) {
      return true;
    }
    return false;
  }
  
  public int getResultAsInt(String query)
  {
    checkConnection();
    ResultSet r = getQuery(query);
    int i = 0;
    if (r != null) {
      try
      {
        while (r.next()) {
          i = r.getInt(1);
        }
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
    return i;
  }
  
  public String getResultAsString(String query)
  {
    checkConnection();
    ResultSet r = getQuery(query);
    String i = "";
    if (r != null) {
      try
      {
        while (r.next()) {
          i = r.getString(1);
        }
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
    return i;
  }
  
  public boolean isConnected()
  {
    try
    {
      return !this.connect.isClosed();
    }
    catch (Exception localException) {}
    return false;
  }
  
  public void disconnect()
  {
    try
    {
      if ((this.connect != null) && (!this.connect.isClosed())) {
        this.connect.close();
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
public boolean ConnectToMysql(String host,  String user,  String password, String port, String database){     
    
    this.host = host;
	this.user = user;
	this.port = port;
	this.passwort = password;
	this.database = database;
	
	this.connect();
	
	if(this.isConnected()==true)
	{
		return true;
	}
	else
	{
		return false;
	}
	
}
  
public void Execute(String cl)
{	
	this.execute(cl);
}

public String Querry(String cl, int kol)
{ 		
	ResultSet rs = this.getQuery(cl);
	String re = null;
	try {
		while(rs.next())
		{
			re = rs.getString(kol);
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return re;
}

}
