package ru.windstone.beautifier.Events;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.windstone.beautifier.BeautifierPlugin;

public class PlayerQuit implements Listener {
    private final BeautifierPlugin instance;

    public PlayerQuit(BeautifierPlugin instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        String playerQuitMessage = PlaceholderAPI.setPlaceholders(event.getPlayer(), instance.config.getSection("messages").getString("quit"));
        Server server = instance.getServer();

        event.quitMessage(MiniMessage.miniMessage().deserialize(playerQuitMessage));
        server.dispatchCommand(server.getConsoleSender(), instance.config.getSection("commands").getString("quit"));
    }
}
