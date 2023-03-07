package ru.windstone.beautifier.Events;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import io.papermc.paper.advancement.AdvancementDisplay;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.windstone.beautifier.BeautifierPlugin;

public class PlayerListener implements Listener {
    private final BeautifierPlugin instance;

    public PlayerListener(BeautifierPlugin instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Section messagesSection = instance.config.getSection("messages");

        String stringPlayerJoin = PlaceholderAPI.setPlaceholders(
                player,
                messagesSection.getString("join")
        );
        String stringPlayerFirstJoin = PlaceholderAPI.setPlaceholders(
                player,
                messagesSection.getString("first-join")
        );

        Component componentPlayerJoin = MiniMessage.miniMessage().deserialize(stringPlayerJoin);
        Component componentPlayerFirstJoin = MiniMessage.miniMessage().deserialize(stringPlayerFirstJoin);

        if (player.hasPlayedBefore()) {
            event.joinMessage(componentPlayerJoin);
        }
        else {
            event.joinMessage(componentPlayerFirstJoin);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Section messagesSection = instance.config.getSection("messages");

        String stringPlayerQuit = PlaceholderAPI.setPlaceholders(
                player,
                messagesSection.getString("quit")
        );

        Component componentPlayerQuit = MiniMessage.miniMessage().deserialize(stringPlayerQuit);

        event.quitMessage(componentPlayerQuit);
    }

    @EventHandler
    public void onPlayerGetsAdvancement(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        Section messagesSection = instance.config.getSection("messages");

        Advancement advancement = event.getAdvancement();
        String key = advancement.getKey().getKey();

        if (key.contains("root") || key.contains("recipes")) {
            return;
        }

        String configAdvancementType = null;

        switch (key.substring(0, 3)) {
            case "sto" -> configAdvancementType = messagesSection.getString("story-advancement");
            case "adv" -> configAdvancementType = messagesSection.getString("adventure-advancement");
            case "net" -> configAdvancementType = messagesSection.getString("nether-advancement");
            case "end" -> configAdvancementType = messagesSection.getString("end-advancement");
            case "hus" -> configAdvancementType = messagesSection.getString("husbandry-advancement");
            default -> System.out.println("Что-то не так...");
        }

        AdvancementDisplay advancementDisplay = advancement.getDisplay();

        String stringAdvancement = PlaceholderAPI.setPlaceholders(
                player,
                configAdvancementType
        );

        Component inputComponentAdvancement = MiniMessage.miniMessage().deserialize(stringAdvancement);

        Component advancementDisplayTitle = advancementDisplay.title();
        Component advancementDisplayDescription = advancementDisplay.description();

        Component betweenComponentAdvancement = inputComponentAdvancement.replaceText(replacementBuilder ->
                replacementBuilder
                        .matchLiteral("%beautifier_title%")
                        .replacement(advancementDisplayTitle)
        );
        Component outputAdvancementComponent = betweenComponentAdvancement.replaceText(replacementBuilder ->
                replacementBuilder
                        .matchLiteral("%beautifier_description%")
                        .replacement(advancementDisplayDescription)
        );

        player.sendMessage(outputAdvancementComponent);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        Section messagesSection = instance.config.getSection("messages");

        String stringDeath = PlaceholderAPI.setPlaceholders(
                player,
                messagesSection.getString("join")
        );

        Component componentDeath = MiniMessage.miniMessage().deserialize(stringDeath);

        event.deathMessage(componentDeath);
    }
}
