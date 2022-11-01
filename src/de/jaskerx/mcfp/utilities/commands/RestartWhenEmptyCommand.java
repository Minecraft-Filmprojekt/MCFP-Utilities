package de.jaskerx.mcfp.utilities.commands;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.jaskerx.mcfp.utilities.constants.Constants;

public class RestartWhenEmptyCommand implements CommandExecutor {
	
	boolean restartWhenEmpty = false;
	Timer t;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
		
		if(!sender.hasPermission("mcfp.utilities.restart")) {
			sender.sendMessage(Constants.CHAT_PREFIX + Constants.CHAT_MESSAGE_COLOR_ERROR +
								"Du hast keine Berechtigung, diesen Command auszuführen!");
			return false;
		}
		restartWhenEmpty = !restartWhenEmpty;
		if(restartWhenEmpty) {
			t = new Timer();
			t.schedule(new TimerTask() {
				@Override
				public void run() {
					if(Bukkit.getOnlinePlayers().size() == 0) {
						Bukkit.getServer().spigot().restart();
					}
				}
			}, 0, 60000);
			sender.sendMessage(Constants.CHAT_PREFIX + Constants.CHAT_MESSAGE_COLOR +
								"Der Server wird nun neugestartet, wenn kein Spieler mehr online ist.");
		} else {
			t.cancel();
			sender.sendMessage(Constants.CHAT_PREFIX + Constants.CHAT_MESSAGE_COLOR +
								"Der Server wird nicht mehr automatisch neugestartet.");
		}
		
		return true;
	}

}
