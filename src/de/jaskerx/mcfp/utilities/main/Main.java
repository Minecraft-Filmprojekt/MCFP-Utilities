package de.jaskerx.mcfp.utilities.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import de.jaskerx.mcfp.utilities.commands.PluginCommand;
import de.jaskerx.mcfp.utilities.commands.PluginGroupsCommand;
import de.jaskerx.mcfp.utilities.commands.RestartWhenEmptyCommand;
import de.jaskerx.mcfp.utilities.listeners.PlayerCommandPreprocessListener;

public class Main extends JavaPlugin {

	public static Connection con;
	
	@Override
	public void onEnable() {
		
		this.saveDefaultConfig();
		
		initDb();
		
		getCommand("restartwhenempty").setExecutor(new RestartWhenEmptyCommand());
		getCommand("plugin").setExecutor(new PluginCommand());
		getCommand("plugingroups").setExecutor(new PluginGroupsCommand());
		
		Bukkit.getPluginManager().registerEvents(new PlayerCommandPreprocessListener(), this);
	}
	
	@Override
	public void onDisable() {
		
		try {
			con.close();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void initDb() {
		
		try {
			con = DriverManager.getConnection("jdbc:sqlite:plugins/" + this.getDataFolder().getName() + "/plugins.db");
			
			Statement stat = con.createStatement();
	        stat.executeUpdate("CREATE TABLE IF NOT EXISTS plugins_management ('name' TEXT NOT NULL UNIQUE, 'group' TEXT, PRIMARY KEY('name'))");
	        stat.executeUpdate("CREATE TABLE IF NOT EXISTS plugins_groups ('name' TEXT NOT NULL UNIQUE, PRIMARY KEY('name'))");
	        
	        for(Plugin p : this.getServer().getPluginManager().getPlugins()) {
	        	stat.executeUpdate("INSERT OR IGNORE INTO plugins_management (name) VALUES ('" + p.getName() + "')");
	        }
	        
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
