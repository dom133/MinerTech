package Players;

import java.util.Map.Entry;
import java.util.SortedMap;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import Lib.Config;

import com.dom133.minertech.MinerTech;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;

public class Commands implements CommandExecutor {
	
	private MinerTech plugin;
	public Commands(MinerTech plugin)
	{
		this.plugin = plugin;
	}
		
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("mt"))
		{
			if(sender instanceof Player)
			{
				Player player = (Player) sender;
				if(args.length > 0)
				{
					switch(args[0])
					{
						case "help":
						{
							if(player.hasPermission("mt.help"))
							{
								player.sendMessage(ChatColor.GOLD+"=======MinerTech=======");
								if(player.hasPermission("mt.stats"))player.sendMessage(ChatColor.GRAY+"/mt stats - Checking your stats");
								if(player.hasPermission("mt.stats.player"))player.sendMessage(ChatColor.GRAY+"/mt stats <player> - Check the stats for the player");
								player.sendMessage(ChatColor.GRAY+"/mt reward - Receiving rewards received");
								player.sendMessage(ChatColor.GRAY+"/mt rank - Checking the top ten rankings");
								if(player.hasPermission("mt.addlvl"))player.sendMessage(ChatColor.GRAY+"/mt addlvl <value> - Adding yourself the level");
								if(player.hasPermission("mt.addlvl.player"))player.sendMessage(ChatColor.GRAY+"/mt addlvl <player> <value> - The specified level adds player");
								if(player.hasPermission("mt.reset"))player.sendMessage(ChatColor.GRAY+"/mt reset - Restarts your stats");
								if(player.hasPermission("mt.reset.player"))player.sendMessage(ChatColor.GRAY+"/mt reset <player> - Restarts the player's statistics");
								player.sendMessage(ChatColor.GOLD+"=======================");
								
								return true;
							}
							else
							{
								player.sendMessage(MinerTech.nonperm);
								return true;
							}
						}
						
						case "reward":
						{
							if(MinerTech.getPlayerRewards().get(player.getDisplayName())!="empty" | MinerTech.getPlayerRewards().get(player.getDisplayName())!=null)
							{
								String reward = MinerTech.getPlayerRewards().get(player.getDisplayName());
								int lenght = reward.length();
								String rv=null;
								String rp=null;
								for(int i=0; i<=lenght-1; i++)
								{
									if(rv==null)rv="";
									if(rp==null)rp="";
									
									if(plugin.isInteger(String.valueOf(reward.charAt(i)))==true)
									{
										rp+=String.valueOf(reward.charAt(i));
									}
									else if(plugin.isInteger(String.valueOf(reward.charAt(i)))==false & String.valueOf(reward.charAt(i)).equals("[")!=true & String.valueOf(reward.charAt(i)).equals("]")!=true & String.valueOf(reward.charAt(i)).equals(",")!=true)
									{
										rv+=String.valueOf(reward.charAt(i));
									}		
									else if(String.valueOf(reward.charAt(i)).equals(",") | lenght-2==i-1)		
									{
										if(i>MinerTech.inv_v ) MinerTech.inv_v = i;
										MinerTech.inv.addItem(new ItemStack(Material.getMaterial(rv), Integer.valueOf(rp)));
										rv = "";
										rp = "";
									}
								}		
								MinerTech.getPlayerRewards().put(player.getDisplayName(), "empty");
								player.openInventory(MinerTech.inv);
								return true;
							}
							else
							{
								player.sendMessage(MinerTech.prefix+"You do not have to pick up any rewards!!!");
								return true;
							}
						}
						case "reset":
						{
							if(args.length >= 2)
							{
								if(player.hasPermission("mt.reset.player"))
								{
									Player player1 = Bukkit.getPlayer(args[1]);
									if(player1==null)
									{
										OfflinePlayer player2 = Bukkit.getOfflinePlayer(args[1]);
										if(MinerTech.type.equals("mysql"))
										{
											if(plugin.con.Querry("SELECT * FROM minertech WHERE `UUID` = '"+player2.getUniqueId().toString()+"';", 1)==null)
											{
												player.sendMessage(MinerTech.prefix+ChatColor.RED+"Given the player does not exist");
												return true;
											}
											else
											{
												plugin.con.update("DELETE FROM minertech WHERE UUID='"+player2.getUniqueId().toString()+"'");
												player.sendMessage(MinerTech.prefix+ChatColor.GOLD+"Player stats correctly been removed "+args[1]);
												return true;
											}
										}
										else
										{
											if(Config.getConfig("player").getString(player2.getUniqueId().toString())==null)
											{
												player.sendMessage(MinerTech.prefix+ChatColor.RED+"Given the player does not exist");
												return true;
											}
											else
											{
												Config.getConfig("player").set(player2.getUniqueId().toString()+".nick", player2.getPlayer().getDisplayName());
												Config.getConfig("player").set(player2.getUniqueId().toString()+".lvl", 0);
												Config.getConfig("player").set(player2.getUniqueId().toString()+".exp", 0);
												Config.getConfig("player").set(player2.getUniqueId().toString()+".rewards", null);
												Config.save("player");
												Config.load("player");
												player.sendMessage(MinerTech.prefix+ChatColor.GOLD+"Player stats correctly been removed "+args[1]);
												return true;
											}
										}
									}
									else
									{
										MinerTech.getLevels().put(player1.getDisplayName(), 0);
										MinerTech.getExps().put(player1.getDisplayName(), 0);
										MinerTech.getPlayerRewards().put(player1.getDisplayName(), "empty");
										player.sendMessage(MinerTech.prefix+ChatColor.GOLD+"Player stats correctly been removed "+args[1]);
										return true;
									}
								}
								else
								{
									player.sendMessage(MinerTech.nonperm);
									return true;
								}
							}
							else
							{
								if(player.hasPermission("mt.reset"))
								{
									MinerTech.getLevels().put(player.getDisplayName(), 0);
									MinerTech.getExps().put(player.getDisplayName(), 0);
									MinerTech.getPlayerRewards().put(player.getDisplayName(), "empty");
									player.sendMessage(MinerTech.prefix+ChatColor.GOLD+"Properly removed for your stats");
									return true;
								}
								else
								{
									player.sendMessage(MinerTech.nonperm);
									return true;
								}
							}
						}
						case "stats":
						{
							if(args.length > 1)
							{
								if(player.hasPermission("mt.stats.player"))
								{
									Player player1 = Bukkit.getPlayer(args[1]);
									if(player1==null)
									{
										OfflinePlayer player2 = Bukkit.getOfflinePlayer(args[1]);
										if(MinerTech.type.equals("mysql"))
										{
											if(plugin.con.Querry("SELECT * FROM minertech WHERE `UUID` = '"+player2.getUniqueId().toString()+"';", 1)==null)
											{
												player.sendMessage(MinerTech.prefix+ChatColor.RED+"Given the player does not exist");
												return true;
											}
											else
											{
												player.sendMessage(ChatColor.GOLD+"=========Stats=========");
												player.sendMessage(ChatColor.GOLD+"Nick: "+ChatColor.GREEN+player2.getPlayer().getDisplayName());
												player.sendMessage(ChatColor.GOLD+"Lvl: "+ChatColor.DARK_GRAY+plugin.con.Querry("SELECT * FROM minertech WHERE `UUID` = '"+player2.getUniqueId().toString()+"';", 5));
												player.sendMessage(ChatColor.GOLD+"Exp: "+ChatColor.DARK_GRAY+plugin.con.Querry("SELECT * FROM minertech WHERE `UUID` = '"+player2.getUniqueId().toString()+"';", 4));
												String nreward = MinerTech.getExpReward().get(plugin.con.Querry("SELECT * FROM minertech WHERE `UUID` = '"+player2.getUniqueId().toString()+"';", 5)+1).toString();
												if(MinerTech.getExpReward().get(plugin.con.Querry("SELECT * FROM minertech WHERE `UUID` = '"+player2.getUniqueId().toString()+"';", 5)+1)==null)nreward = "Lack";
												player.sendMessage(ChatColor.GOLD+"Next reward: "+ChatColor.DARK_GRAY+nreward);
												player.sendMessage(ChatColor.GOLD+"Rank: "+ChatColor.DARK_GRAY+"WIP");
												player.sendMessage(ChatColor.GOLD+"=======================");
												return true;
											}
										}
										else
										{
											if(Config.getConfig("player").getString(player2.getUniqueId().toString())==null)
											{
												player.sendMessage(MinerTech.prefix+ChatColor.RED+"Given the player does not exist");
												return true;
											}
											else
											{
												player.sendMessage(ChatColor.GOLD+"=========Stats=========");
												player.sendMessage(ChatColor.GOLD+"Nick: "+ChatColor.GREEN+player2.getPlayer().getDisplayName());
												player.sendMessage(ChatColor.GOLD+"Lvl: "+ChatColor.DARK_GRAY+Config.getConfig("player").getString(player2.getUniqueId().toString()+".lvl"));
												player.sendMessage(ChatColor.GOLD+"Exp: "+ChatColor.DARK_GRAY+Config.getConfig("player").getString(player2.getUniqueId().toString()+".exp"));
												String nreward = MinerTech.getExpReward().get(Config.getConfig("player").getString(player2.getUniqueId().toString()+".lvl")+1).toString();
												if(MinerTech.getExpReward().get(Config.getConfig("player").getString(player2.getUniqueId().toString()+".lvl")+1)==null)nreward = "Lack";
												player.sendMessage(ChatColor.GOLD+"Next reward: "+ChatColor.DARK_GRAY+nreward);
												player.sendMessage(ChatColor.GOLD+"Rank: "+ChatColor.DARK_GRAY+"WIP");
												player.sendMessage(ChatColor.GOLD+"=======================");
												return true;
											}
										}
									}
									else
									{
										player.sendMessage(ChatColor.GOLD+"=========Stats=========");
										player.sendMessage(ChatColor.GOLD+"Nick: "+ChatColor.GREEN+player1.getDisplayName());
										player.sendMessage(ChatColor.GOLD+"Lvl: "+ChatColor.DARK_GRAY+MinerTech.getLevels().get(player.getDisplayName()));
										player.sendMessage(ChatColor.GOLD+"Exp: "+ChatColor.DARK_GRAY+MinerTech.getExps().get(player.getDisplayName()));
										String nreward = MinerTech.getExpReward().get(MinerTech.getLevels().get(player.getDisplayName())+1).toString();
										if(MinerTech.getExpReward().get(MinerTech.getLevels().get(player.getDisplayName())+1)==null)nreward = "Lack";
										player.sendMessage(ChatColor.GOLD+"Next reward: "+ChatColor.DARK_GRAY+nreward);
										player.sendMessage(ChatColor.GOLD+"Rank: "+ChatColor.DARK_GRAY+"WIP");
										player.sendMessage(ChatColor.GOLD+"=======================");
										return true;										
									}
								}
								else
								{
									player.sendMessage(MinerTech.nonperm);
									return true;
								}
							}
							else
							{
								if(player.hasPermission("mt.stats"))
								{
									player.sendMessage(ChatColor.GOLD+"=========Stats=========");
									player.sendMessage(ChatColor.GOLD+"Nick: "+ChatColor.GREEN+player.getDisplayName());
									player.sendMessage(ChatColor.GOLD+"Lvl: "+ChatColor.DARK_GRAY+MinerTech.getLevels().get(player.getDisplayName()));
									player.sendMessage(ChatColor.GOLD+"Exp: "+ChatColor.DARK_GRAY+MinerTech.getExps().get(player.getDisplayName()));
									player.sendMessage(ChatColor.GOLD+"Next Level: "+ChatColor.DARK_GRAY+MinerTech.getExpMax().get(MinerTech.getLevels().get(player.getDisplayName())+1));
									String nreward = MinerTech.getExpReward().get(MinerTech.getLevels().get(player.getDisplayName())+1).toString();
									if(MinerTech.getExpReward().get(MinerTech.getLevels().get(player.getDisplayName())+1)==null)nreward = "Lack";
									player.sendMessage(ChatColor.GOLD+"Next reward: "+ChatColor.DARK_GRAY+nreward);
									player.sendMessage(ChatColor.GOLD+"Rank: "+ChatColor.DARK_GRAY+"WIP");
									player.sendMessage(ChatColor.GOLD+"=======================");
									return true;
								}
								else
								{
									player.sendMessage(MinerTech.nonperm);
									return true;
								}		
							}
						}
						case "rank":
						{
							player.sendMessage(ChatColor.GOLD+"=========Rank=========");
							SortedMap<String, Integer> sortedMap = ImmutableSortedMap.copyOf(MinerTech.getMapLevel(), Ordering.natural().reverse().onResultOf(Functions.forMap(MinerTech.getMapLevel())).compound(Ordering.natural().reverse()));
							int pos = 1;
							for (Entry<String, Integer> entry : sortedMap.entrySet()) {
							    player.sendMessage(ChatColor.GOLD+Integer.toString(pos++) + ". " + ChatColor.GREEN+entry.getKey() +ChatColor.GOLD+" Lvl: "+ChatColor.GRAY + entry.getValue().toString());
							    if(pos==10) break;
							}
							player.sendMessage(ChatColor.GOLD+"======================");
							return true;
						}
						case "addlvl":
						{
							if(player.hasPermission("mt.addlvl"))
							{
								if(args.length > 1)
								{
									if(plugin.isInteger(args[1])==true)
									{
										int lvl = MinerTech.getLevels().get(player.getDisplayName());
										
										if(MinerTech.getExps().get(player.getDisplayName())!=MinerTech.getExpMax().get(Integer.valueOf(args[1])) | MinerTech.getExpMax().get(Integer.valueOf(args[1]))!=null)
										{
											for(int i=lvl+1; i<=Integer.valueOf(Integer.valueOf(args[1])); i++)
											{
												player.sendMessage("Player EXP: "+MinerTech.getExps().get(player.getDisplayName()));//Debug
												player.sendMessage("Max EXP: "+MinerTech.getExpMax().get(Integer.valueOf(args[1])));//Debug
												player.sendMessage("Lvl: "+i);//Debug
												player.sendMessage("Exp: "+MinerTech.getExps().get(player.getDisplayName()));//Debug
												
												MinerTech.getLevels().put(player.getDisplayName(), i);
												MinerTech.getExps().put(player.getDisplayName(), MinerTech.getExpMax().get(i));
												if(player.hasPermission("mt.vip"))
												{
													if(MinerTech.getPlayerRewards().get(player.getDisplayName()).equals("empty") | MinerTech.getPlayerRewards().get(player.getDisplayName())=="empty")MinerTech.getPlayerRewards().put(player.getDisplayName(), MinerTech.getExpReward().get(i).toString()+"["+MinerTech.getExpRvalue().get(i)+"]"+","+MinerTech.getVipExpReward().get(i).toString()+"["+MinerTech.getVipExpRvalue().get(i)+"]");
													else
													{
														String rewards = MinerTech.getPlayerRewards().get(player.getDisplayName());
														MinerTech.getPlayerRewards().put(player.getDisplayName(), MinerTech.getExpReward().get(i)+"["+MinerTech.getExpRvalue().get(i)+"]"+","+MinerTech.getVipExpReward().get(i)+"["+MinerTech.getVipExpRvalue().get(i)+"]"+","+rewards);
													}
												}
												else
												{
													if(MinerTech.getPlayerRewards().get(player.getDisplayName()).equals("empty") | MinerTech.getPlayerRewards().get(player.getDisplayName())=="empty")MinerTech.getPlayerRewards().put(player.getDisplayName(), MinerTech.getExpReward().get(i).toString()+"["+MinerTech.getExpRvalue().get(i)+"]");
													else
													{
														String rewards = MinerTech.getPlayerRewards().get(player.getDisplayName());
														MinerTech.getPlayerRewards().put(player.getDisplayName(), MinerTech.getExpReward().get(i)+"["+MinerTech.getExpRvalue().get(i)+"]"+","+rewards);
													}
												}
											}
											player.sendMessage(MinerTech.prefix+ChatColor.GOLD+"Correctly added to yourself "+args[1]+" level");
											return true;
										}
										else
										{
											player.sendMessage(MinerTech.prefix+ChatColor.RED+"You already have the maximum level");
											return true;
										}
									}
									else
									{
										if(plugin.isInteger(args[2])==true)
										{
											Player player1 = Bukkit.getPlayer(args[1]);
											if(player1==null)
											{
												OfflinePlayer player2 = Bukkit.getOfflinePlayer(args[1]);
												if(MinerTech.type.equals("mysql"))
												{
													if(plugin.con.Querry("SELECT * FROM minertech WHERE `UUID` = '"+player2.getUniqueId().toString()+"';", 1)==null)
													{
														player.sendMessage(MinerTech.prefix+ChatColor.RED+"Given the player does not exist");
														return true;
													}
													else
													{
														
													}
												}
												else
												{
													if(Config.getConfig("player").getString(player.getUniqueId().toString())==null)
													{
														player.sendMessage(MinerTech.prefix+ChatColor.RED+"Given the player does not exist");
														return true;
													}
													else
													{
														
													}
												}
												
											}
											else
											{
												int lvl = MinerTech.getLevels().get(player1.getDisplayName());
												
												if(MinerTech.getExps().get(player1.getDisplayName())!=MinerTech.getExpMax().get(Integer.valueOf(args[1])) | MinerTech.getExpMax().get(Integer.valueOf(args[1]))!=null)
												{
													for(int i=lvl+1; i<=Integer.valueOf(args[2]); i++)
													{
														MinerTech.getLevels().put(player1.getDisplayName(), i);
														MinerTech.getExps().put(player1.getDisplayName(), MinerTech.getExpMax().get(i));
														if(player.hasPermission("mt.vip"))
														{
															if(MinerTech.getPlayerRewards().get(player1.getDisplayName()).equals("empty") | MinerTech.getPlayerRewards().get(player1.getDisplayName())=="empty")MinerTech.getPlayerRewards().put(player1.getDisplayName(), MinerTech.getExpReward().get(i).toString()+"["+MinerTech.getExpRvalue().get(i)+"]"+","+MinerTech.getVipExpReward().get(i).toString()+"["+MinerTech.getVipExpRvalue().get(i)+"]");
															else
															{
																String rewards = MinerTech.getPlayerRewards().get(player1.getDisplayName());
																MinerTech.getPlayerRewards().put(player1.getDisplayName(), MinerTech.getExpReward().get(i).toString()+"["+MinerTech.getExpRvalue().get(i)+"]"+","+MinerTech.getVipExpReward().get(i).toString()+"["+MinerTech.getVipExpRvalue().get(i)+"]"+","+rewards);
															}
														}
														else
														{
															if(MinerTech.getPlayerRewards().get(player1.getDisplayName()).equals("empty") | MinerTech.getPlayerRewards().get(player1.getDisplayName())=="empty")MinerTech.getPlayerRewards().put(player1.getDisplayName(), MinerTech.getExpReward().get(i).toString()+"["+MinerTech.getExpRvalue().get(i)+"]");
															else
															{
																String rewards = MinerTech.getPlayerRewards().get(player1.getDisplayName());
																MinerTech.getPlayerRewards().put(player1.getDisplayName(), MinerTech.getExpReward().get(i).toString()+"["+MinerTech.getExpRvalue().get(i)+"]"+","+rewards);
															}
														}
													}
													player.sendMessage(MinerTech.prefix+ChatColor.GOLD+"Correctly added to "+args[1]+" "+args[2]+" level");
													return true;
												}
												else
												{
													player.sendMessage(MinerTech.prefix+ChatColor.GRAY+args[1]+ChatColor.RED+" already have the maximum level");
													return true;
												}
											}
										}
										else
										{
											player.sendMessage(ChatColor.GOLD+"Level must be a number");
											return true;
										}
									}
								}
								else
								{
									player.sendMessage(MinerTech.prefix+ChatColor.RED+"You have not entered a required argument");
									return true;
								}
							}
							else
							{
								player.sendMessage(MinerTech.nonperm);
								return true;
							}
						}
						default:
						{
							player.sendMessage(MinerTech.prefix+ChatColor.GOLD+"To see the available commands enter /mt help");
							return true;
						}
					}
				}
				else
				{
					player.sendMessage(MinerTech.prefix+ChatColor.GOLD+"To see the available commands enter /mt help");
					return true;
				}
			}
			else
			{
				System.out.println("You can not perform this command in the console");
				return true;
			}
		}
		else if(cmd.getName().equalsIgnoreCase("minertech"))
			if(sender instanceof Player)
			{
				Player player = (Player) sender;
				player.sendMessage(MinerTech.prefix+ChatColor.GOLD+"To see the available commands enter /mt help");
				return true;
			}
			else
			{
				System.out.println("To see the available commands enter /mt help");
				return true;
			}
		return false;
	}
}