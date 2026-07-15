package me.antiop;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiOP extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("AntiOP enabled!");
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage().toLowerCase();

        if (cmd.equals("/op") || cmd.startsWith("/op ")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou cannot use /op!");
        }
    }
}
