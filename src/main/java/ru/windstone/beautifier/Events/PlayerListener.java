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
        // Кэширование результатов
        Player player = event.getPlayer();
        Section messagesSection = instance.config.getSection("messages");

        // Парсинг переменных из PlaceholderAPI
        String stringPlayerJoin = PlaceholderAPI.setPlaceholders(
                player,
                messagesSection.getString("join")
        );
        String stringPlayerFirstJoin = PlaceholderAPI.setPlaceholders(
                player,
                messagesSection.getString("first-join")
        );

        // Строки будут переведены в тип компонента с форматированием текста по MiniMessage
        Component componentPlayerJoin = MiniMessage.miniMessage().deserialize(stringPlayerJoin);
        Component componentPlayerFirstJoin = MiniMessage.miniMessage().deserialize(stringPlayerFirstJoin);

        // Проверка на первый заход игрока на сервер
        if (player.hasPlayedBefore()) {
            event.joinMessage(componentPlayerJoin);
        }
        else {
            event.joinMessage(componentPlayerFirstJoin);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Кэширование результатов
        Player player = event.getPlayer();
        Section messagesSection = instance.config.getSection("messages");

        // Парсинг переменных из PlaceholderAPI
        String stringPlayerQuit = PlaceholderAPI.setPlaceholders(
                player,
                messagesSection.getString("quit")
        );

        // Строка будет переведена в тип компонента с форматированием текста по MiniMessage
        Component componentPlayerQuit = MiniMessage.miniMessage().deserialize(stringPlayerQuit);

        // Замена сообщения о выходе игрока на componentPlayerQuit
        event.quitMessage(componentPlayerQuit);
    }

    @EventHandler
    public void onPlayerGetsAdvancement(PlayerAdvancementDoneEvent event) {
        // Кэширование результатов
        Player player = event.getPlayer();
        Section messagesSection = instance.config.getSection("messages");
        Advancement advancement = event.getAdvancement();
        // Получение строки на подобие этой: "story/workbench"
        String key = advancement.getKey().getKey();

        // Игнорирование всех достижений, которые являются рецептами
        // Пример: игрок добывает алмазы, и помимо обычной ачивки за них, у игрока открываются новые рецепты
        // Без этого блока кода они будут выводиться в чате
        if (key.contains("recipes") || key.contains("root")) {
            return;
        }

        // Определение переменной
        // Почему null? Потому что выводится ошибка
        // "Variable 'configAdvancementType' might not have been initialized"
        String configAdvancementType = null;

        // Присвоение переменной в зависимости от ветви достижений
        /*
         * Ответвления достижений (в клиенте они находятся во вкладках достижений):
         *
         * story/
         * adventure/
         * nether/
         * end/
         * husbandry/
         *
         * После / идут названия достижений
         */
        // Ветка end - самое маленькое слово, поэтому проверяются лишь первые три символа
        switch (key.substring(0, 3)) {
            // story - вкладка "Minecraft"
            case "sto" -> configAdvancementType = messagesSection.getString("story-advancement");
            // adventure - вкладка "Приключения"
            case "adv" -> configAdvancementType = messagesSection.getString("adventure-advancement");
            // nether - вкладка "Незер"
            case "net" -> configAdvancementType = messagesSection.getString("nether-advancement");
            // end - вкладка "Энд"
            case "end" -> configAdvancementType = messagesSection.getString("end-advancement");
            // husbandry - вкладка "Сельское хозяйство"
            case "hus" -> configAdvancementType = messagesSection.getString("husbandry-advancement");
            default -> System.out.println("Что-то не так...");
        }

        // Кэширование результата
        AdvancementDisplay advancementDisplay = advancement.getDisplay();

        // Парсинг переменных из PlaceholderAPI
        String stringAdvancement = PlaceholderAPI.setPlaceholders(
                player,
                configAdvancementType
        );

        // Строка будет переведена в тип компонента с форматированием текста по MiniMessage
        Component inputComponentAdvancement = MiniMessage.miniMessage().deserialize(stringAdvancement);

        Component advancementDisplayTitle = advancementDisplay.title();
        Component advancementDisplayDescription = advancementDisplay.description();

        // Замена переменной <title> на компонент advancementDisplayTitle
        Component betweenComponentAdvancement = inputComponentAdvancement.replaceText(replacementBuilder ->
                replacementBuilder
                        .matchLiteral("<title>")
                        .replacement(advancementDisplayTitle)
        );
        // Замена переменной <description> на компонент advancementDisplayDescription
        Component outputAdvancementComponent = betweenComponentAdvancement.replaceText(replacementBuilder ->
                replacementBuilder
                        .matchLiteral("<description>")
                        .replacement(advancementDisplayDescription)
        );

        // Отправка итогового сообщения ЛИШЬ игроку, получившему достижение
        player.sendMessage(outputAdvancementComponent);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Кэширование результатов
        Player player = event.getPlayer();
        Section messagesSection = instance.config.getSection("messages");

        // Парсинг переменных из PlaceholderAPI
        String stringDeath = PlaceholderAPI.setPlaceholders(
                player,
                messagesSection.getString("death")
        );

        // Строка будет переведена в тип компонента с форматированием текста по MiniMessage
        Component inputComponentDeathMessage = MiniMessage.miniMessage().deserialize(stringDeath);

        // Берётся сообщение со смертью игрока и в нём убирается никнейм игрока
        Component componentDeathCause = event.deathMessage().replaceText(replacementBuilder ->
                replacementBuilder
                        .matchLiteral(player.getName())
                        .replacement("")
        );

        // Замена переменной <death_cause> на componentDeathCause
        Component outputComponentDeathMessage = inputComponentDeathMessage.replaceText(replacementBuilder ->
                replacementBuilder
                        .matchLiteral("<death_cause>")
                        .replacement(componentDeathCause)
        );

        // Замена сообщения о смерти игрока на outputComponentDeathMessage
        event.deathMessage(outputComponentDeathMessage);
    }
}
