package ru.windstone.beautifier.Events;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Server;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import ru.windstone.beautifier.BeautifierPlugin;

public class PlayerGetsAdvancement implements Listener {

    private final BeautifierPlugin instance;

    public PlayerGetsAdvancement(BeautifierPlugin instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerGetsAdvancement(PlayerAdvancementDoneEvent event) {
        String playerAdvancementMessage = PlaceholderAPI.setPlaceholders(event.getPlayer(), instance.config.getSection("messages").getString("advancement"));
        Server server = instance.getServer();

        Player player = event.getPlayer();
        Advancement advancement = event.getAdvancement();
        String key = advancement.getKey().toString();

        if (key.contains("root") || key.contains("recipes")) {
            return;
        }

        Component advancementComponent = advancement.getDisplay().title();
        String advancementMessage = PlainTextComponentSerializer.plainText().serialize(advancementComponent);
        playerAdvancementMessage = playerAdvancementMessage.replace("%beautifier_advancement%", advancementMessage);

        player.sendMessage(MiniMessage.miniMessage().deserialize(playerAdvancementMessage));
        server.dispatchCommand(server.getConsoleSender(), instance.config.getSection("commands").getString("advancement"));
    }
}