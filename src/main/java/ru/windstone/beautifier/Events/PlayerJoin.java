package ru.windstone.beautifier.Events;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.windstone.beautifier.BeautifierPlugin;

public class PlayerJoin implements Listener {

    private final BeautifierPlugin instance;

    public PlayerJoin(BeautifierPlugin instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerJoinMessage = PlaceholderAPI.setPlaceholders(event.getPlayer(), instance.config.getSection("messages").getString("join"));
        String playerFirstJoinMessage = PlaceholderAPI.setPlaceholders(event.getPlayer(), instance.config.getSection("messages").getString("first-join"));

        Player player = event.getPlayer();
        Server server = instance.getServer();

        if (player.hasPlayedBefore()) {
            event.joinMessage(MiniMessage.miniMessage().deserialize(playerJoinMessage));
            server.dispatchCommand(server.getConsoleSender(), instance.config.getSection("commands").getString("join"));
        }
        else {
            event.joinMessage(MiniMessage.miniMessage().deserialize(playerFirstJoinMessage));
            server.dispatchCommand(server.getConsoleSender(), instance.config.getSection("commands").getString("first-join"));
        }
    }
}