package de.jaskerx.mcfp.utilities.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import de.jaskerx.mcfp.utilities.constants.Constants;
import de.jaskerx.mcfp.utilities.main.Main;

public class PluginGroupsCommand implements CommandExecutor, TabExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if(!sender.hasPermission("mcfp.utilities.plugins.manage")) {
			sender.sendMessage(Constants.CHAT_PREFIX + Constants.CHAT_MESSAGE_COLOR_ERROR +
								"Du hast keine Berechtigung, diesen Command auszuführen!");
			return false;
		}
		
		if(validateArgs(args)) {
			
			Statement stat;
			try {
				stat = Main.con.createStatement();
				ResultSet rs;
				int rows;
				boolean isNext;
				
				switch(args[0].toLowerCase()) {
				
					case "add":
						rows = stat.executeUpdate("INSERT INTO plugins_groups (name) VALUES ('" + args[1] + "')");
						
						if(rows != 0) {
							sender.sendMessage(Constants.CHAT_PREFIX +
												"Die Gruppe " + args[1] + " wurde hinzugefügt.");
						}
						break;
						
					case "get":
						rs = stat.executeQuery("SELECT name FROM plugins_groups");
						
						isNext = rs.next();
						if(isNext) sender.sendMessage(Constants.CHAT_PREFIX +
														"Es gibt folgende Gruppen:");
						
						while(isNext || rs.next()) {
							sender.sendMessage(Constants.CHAT_MESSAGE_COLOR +
												" - " + rs.getString("name"));
							isNext = false;
						}
						break;
						
					case "remove":
						if(args.length >= 3 && args[2].equalsIgnoreCase("ignore")) {
							sender.sendMessage(removeGroup(args[1]));
						} else {
							rs = stat.executeQuery("SELECT * FROM plugins_management WHERE `group` = '" + args[1] + "'");
							
							isNext = rs.next();
							if(isNext) {
								sender.sendMessage(Constants.CHAT_PREFIX + Constants.CHAT_MESSAGE_COLOR_ERROR +
															"Bitte entferne oder ändere die Gruppe folgender Plugins oder füge dem Command 'ignore' hinzu, um diese automatisch zu entfernen:");
							} else {
								sender.sendMessage(removeGroup(args[1]));
							}
							
							while(isNext || rs.next()) {
								sender.sendMessage(Constants.CHAT_MESSAGE_COLOR_ERROR +
													" - " + rs.getString("name"));
								isNext = false;
							}
						}
						break;
			
				}
				
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
		} else {
			
			sender.sendMessage(Constants.CHAT_PREFIX + Constants.CHAT_MESSAGE_COLOR_ERROR +
					"Bitte nutze den Command folgendermaßen:");
			return false;
		}
		
		return true;
	}
	
	
	
	private boolean validateArgs(String[] args) {
		
		if(!(args.length >= 2 || (args.length == 1 && args[0].equalsIgnoreCase("get")))) return false;
		
		if(!(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("remove"))) return false;
		
		if(args[0].equalsIgnoreCase("remove")) {
			try {
				Statement stat = Main.con.createStatement();
				ResultSet rs = stat.executeQuery("SELECT name FROM plugins_groups WHERE name = '" + args[1] + "'");
				
				if(!rs.next()) return false;
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
	private String removeGroup(String name) throws SQLException {
		
		Statement stat = Main.con.createStatement();
		int rows = stat.executeUpdate("UPDATE plugins_management SET `group` = NULL WHERE `group` = '" + name + "'");
		
		stat.executeUpdate("DELETE FROM plugins_groups WHERE name = '" + name + "'");
		
		return Constants.CHAT_PREFIX + "Die Gruppe " + name + " wurde gelöscht." + (rows != 0 ? " Die Gruppe wurde von " + rows + " Plugins entfernt." : "");
	}
	
	

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		List<String> res = new ArrayList<>();
		
		switch(args.length) {
		
			case 1:
				if("add".startsWith(args[0].toLowerCase()))
					res.add("add");
				if("get".startsWith(args[0].toLowerCase()))
					res.add("get");
				if("remove".startsWith(args[0].toLowerCase()))
					res.add("remove");
				break;
				
			case 2:
				if(args[0].equalsIgnoreCase("remove")) {
					try {
						Statement stat = Main.con.createStatement();
						ResultSet rs = stat.executeQuery("SELECT name FROM plugins_groups");
						
						while(rs.next()) {
							if(rs.getString("name").toLowerCase().startsWith(args[1].toLowerCase()))
								res.add(rs.getString("name"));
						}
						
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				break;
		
		}
		
		return res;
	}

}
