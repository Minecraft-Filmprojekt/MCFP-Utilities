package de.jaskerx.mcfp.utilities.listeners;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import de.jaskerx.mcfp.utilities.constants.Constants;
import de.jaskerx.mcfp.utilities.main.Main;

public class PlayerCommandPreprocessListener implements Listener {
	
	ArrayList<String> groups = new ArrayList<>();
	int amount;
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		
		if(event.getMessage().startsWith("/plugins")) {
			
			event.setCancelled(true);
			amount = 0;
			
	        try {
	        	//get
	        	Statement stat = Main.con.createStatement();
	        	
	        	ResultSet rsG = stat.executeQuery("SELECT name FROM plugins_groups");
	        	event.getPlayer().sendMessage(Constants.CHAT_PREFIX +
												"Folgende Plugins sind auf unserem Server aktiv:");
	        	
	        	while(rsG.next()) {
	        		//get
	        		groups.add(rsG.getString("name"));
	        		Statement stat2 = Main.con.createStatement();
	        		ResultSet rsM = stat2.executeQuery("SELECT name FROM plugins_management WHERE `group` = '" + rsG.getString("name") + "'");
	        		
	        		//send
	        		boolean isNext = rsM.next();
	        		if(isNext) event.getPlayer().sendMessage(Constants.CHAT_MESSAGE_COLOR +
	        												" §l" + rsG.getString("name") + ":");
	        		while(isNext || rsM.next()) {
	        			event.getPlayer().sendMessage(Constants.CHAT_MESSAGE_COLOR +
	        											" - " + rsM.getString("name"));
	        			amount++;
	        			isNext = false;
	        		}
	        	}
	        	
	        	//get
	        	ResultSet rs = stat.executeQuery("SELECT name FROM plugins_management WHERE `group` IS NULL");
	        	
	        	//send
	        	boolean isNext = rs.next();
	        	if(isNext) event.getPlayer().sendMessage(Constants.CHAT_MESSAGE_COLOR +
														" §lKeine Gruppe:");
	        	while(isNext || rs.next()) {
	        		event.getPlayer().sendMessage(Constants.CHAT_MESSAGE_COLOR +
							" - " + rs.getString("name"));
	        		amount++;
	        		isNext = false;
	        	}
	        	
	        	event.getPlayer().sendMessage(Constants.CHAT_MESSAGE_COLOR +
						"§o-> Anzahl Plugins: " + amount);
	        	
	        } catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}
