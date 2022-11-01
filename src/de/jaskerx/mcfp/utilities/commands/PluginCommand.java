package de.jaskerx.mcfp.utilities.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.Plugin;

import de.jaskerx.mcfp.utilities.constants.Constants;
import de.jaskerx.mcfp.utilities.main.Main;

public class PluginCommand implements CommandExecutor, TabExecutor {
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if(!sender.hasPermission("mcfp.utilities.plugins.manage")) {
			sender.sendMessage(Constants.CHAT_PREFIX + Constants.CHAT_MESSAGE_COLOR_ERROR +
								"Du hast keine Berechtigung, diesen Command auszuführen!");
			return false;
		}
		
		if(validateArgs(args)) {
			
			try {
				Statement stat = Main.con.createStatement();
				ResultSet rs;
				int rows;
				
				switch(args[2].toLowerCase()) {
				
					case "set":
						rows = stat.executeUpdate("UPDATE plugins_management SET `group` = '" + args[3] + "' WHERE name = '" + args[0] + "'");
						if(rows != 0) {
							sender.sendMessage(Constants.CHAT_PREFIX +
												"Das Plugin " + args[0] + " gehört nun zu: §o" + args[3]);
						}
						break;
				
					case "get":
						rs = stat.executeQuery("SELECT `group` FROM plugins_management WHERE name = '" + args[0] + "'");
						if(rs.next()) {
							sender.sendMessage(Constants.CHAT_PREFIX +
												"Das Plugin " + args[0] + " gehört zu: " + (rs.getString("group") == null ? "-" : "§o" + rs.getString("group")));
						}
						break;
						
					case "clear":
						rows = stat.executeUpdate("UPDATE plugins_management SET `group` = NULL WHERE name = '" + args[0] + "'");
						if(rows != 0) {
							sender.sendMessage(Constants.CHAT_PREFIX +
												"Das Plugin " + args[0] + " gehört nun zu keiner Gruppe!");
						}
						break;
					
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		} else {
			
			sender.sendMessage(Constants.CHAT_PREFIX + Constants.CHAT_MESSAGE_COLOR_ERROR +
								"Bitte nutze den Command folgendermaßen:");
			return false;
		}
		
		return true;
	}
	
	
	
	private boolean validateArgs(String[] args) {
				
		if(!(args.length >= 4 || (args.length == 3 && (args[2].equalsIgnoreCase("get") || args[2].equalsIgnoreCase("clear"))))) return false;
		
		boolean isValid = false;
		for(Plugin p : Bukkit.getServer().getPluginManager().getPlugins()) {
			if(p.getName().equals(args[0])) {
				isValid = true;
				break;
			}
		}
		if(!isValid) return false;
		
		if(!args[1].equalsIgnoreCase("group")) return false;
		
		if(!(args[2].equalsIgnoreCase("set") || args[2].equalsIgnoreCase("get") || args[2].equalsIgnoreCase("clear"))) return false;
		
//		isValid = false;
//		try {
//			Statement stat = Main.con.createStatement();
//			ResultSet rs = stat.executeQuery("SELECT name FROM plugins_groups");
//			
//			while(rs.next()) {
//				if(args.length == 3 || args[3].equals(rs.getString("name"))) {
//					isValid = true;
//					break;
//				}
//			}
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		if(!isValid) return false;
		
		if(args.length >= 4) {
			try {
				Statement stat = Main.con.createStatement();
				ResultSet rs = stat.executeQuery("SELECT name FROM plugins_groups WHERE name = '" + args[3] + "'");
				
				if(!rs.next()) return false;
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return true;
	}

	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

		List<String> res = new ArrayList<>();
		
		switch(args.length) {
		
			case 1:
				for(Plugin p : Bukkit.getServer().getPluginManager().getPlugins()) {
					if(p.getName().toLowerCase().startsWith(args[0].toLowerCase()))
						res.add(p.getName());
				}
				break;
				
			case 2:
				res.add("group");
				break;
				
			case 3:
				if("set".startsWith(args[2].toLowerCase()))
					res.add("set");
				if("get".startsWith(args[2].toLowerCase()))
					res.add("get");
				if("clear".startsWith(args[2].toLowerCase()))
					res.add("clear");
				break;
				
			case 4:
				try {
					Statement stat = Main.con.createStatement();
					ResultSet rs = stat.executeQuery("SELECT name FROM plugins_groups");
					
					while(rs.next()) {
						if(rs.getString("name").toLowerCase().startsWith(args[3].toLowerCase()))
							res.add(rs.getString("name"));
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
		
		}
		
		return res;
	}
	
	

}
