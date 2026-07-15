package me.antiop;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

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

            if (getConfig().getBoolean("webhook.enabled")) {
                sendWebhook(event.getPlayer().getName(), event.getMessage());
            }

            if (getConfig().getBoolean("log-attempts")) {
                getLogger().warning(event.getPlayer().getName() + " tried to use /op.");
            }
        }
    }

    private void sendWebhook(String player, String command) {
        String webhook = getConfig().getString("webhook.url");

        if (webhook == null || webhook.isEmpty()) {
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL(webhook);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                String json = "{"
                        + "\"embeds\":[{"
                        + "\"title\":\"🚨 AntiOP Alert\","
                        + "\"description\":\"**Player:** " + player
                        + "\\n**Command:** `" + command + "`\","
                        + "\"color\":16711680"
                        + "}]"
                        + "}";

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(json.getBytes(StandardCharsets.UTF_8));
                }

                connection.getResponseCode();
                connection.disconnect();

            } catch (Exception e) {
                getLogger().warning("Failed to send webhook: " + e.getMessage());
            }
        }).start();
    }
}
