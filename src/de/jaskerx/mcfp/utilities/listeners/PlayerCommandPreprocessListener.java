package de.jaskerx.mcfp.utilities.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import de.jaskerx.mcfp.utilities.constants.Constants;

public class PlayerCommandPreprocessListener implements Listener {

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		
		if(event.getMessage().startsWith("/plugins")) {
			
			event.setCancelled(true);
			
			
			if(event.getMessage().split(" ").length > 1) {
				
				event.getPlayer().sendMessage(Constants.CHAT_PREFIX + Constants.CHAT_MESSAGE_COLOR +
											"Folgende Plugins sind auf unserem Server aktiv:");
				
				for(Plugin p : Bukkit.getPluginManager().getPlugins()) {
					event.getPlayer().sendMessage(" - " + Constants.CHAT_MESSAGE_COLOR +
													p.getName());
				}
				
				
				return;
			}
			
			
			event.getPlayer().sendMessage("Ok");
		}
	}
	
}
