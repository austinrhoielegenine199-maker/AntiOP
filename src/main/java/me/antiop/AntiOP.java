package me.antiop;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiOP extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("AntiOP enabled!");
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (!getConfig().getBoolean("enabled")) return;

        String cmd = event.getMessage().toLowerCase();

        if (cmd.equals("/op") || cmd.startsWith("/op ")
                || cmd.equals("/minecraft:op") || cmd.startsWith("/minecraft:op ")) {

            String bypass = getConfig().getString("bypass-permission");

            if (bypass != null && event.getPlayer().hasPermission(bypass)) {
                return;
            }

            event.setCancelled(true);

            String message = getConfig().getString("message", "&cYou cannot use /op!");
            event.getPlayer().sendMessage(message.replace("&", "§"));

            if (getConfig().getBoolean("log-attempts")) {
                getLogger().warning(event.getPlayer().getName() + " tried to use /op.");
            }
        }
    }
}
