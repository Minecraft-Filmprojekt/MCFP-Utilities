package de.jaskerx.mcfp.utilities.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.jaskerx.mcfp.utilities.commands.RestartWhenEmptyCommand;
import de.jaskerx.mcfp.utilities.listeners.PlayerCommandPreprocessListener;

public class Main extends JavaPlugin {

	@Override
	public void onEnable() {
		
		getCommand("restartwhenempty").setExecutor(new RestartWhenEmptyCommand());
		
		Bukkit.getPluginManager().registerEvents(new PlayerCommandPreprocessListener(), this);
	}
	
}
