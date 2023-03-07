package ru.windstone.beautifier;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import ru.windstone.beautifier.Commands.ReloadConfig;
import ru.windstone.beautifier.Events.*;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public final class BeautifierPlugin extends JavaPlugin {

    public Logger log = getLogger();
    private static BeautifierPlugin instance;
    final PluginManager instanceManager = getServer().getPluginManager();

    public BeautifierPlugin() throws IOException {
    }

    public void onLoad() {
        instance = this;

        log.info("Beautifier был успешно загружен.");
    }
    @Override
    public void onEnable() {
        instance = this;

        try {
            writeConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info(ChatColor.YELLOW + "Проверяю включённые модули в конфиге...");

        Section modulesSection = config.getSection("modules");

        if (modulesSection.getBoolean("player-chat-enabled")) {
            log.info(ChatColor.YELLOW + "Модуль PlayerChat включён! Пытаюсь найти PlaceholderAPI...");

            if (instanceManager.getPlugin("PlaceholderAPI") != null) {
                log.info(ChatColor.GREEN + "PlaceholderAPI был найден.");

                instanceManager.registerEvents(new PlayerListener(this), this);
            }
            else {
                log.warning("PlaceholderAPI не был найден! Пожалуйста, установите этот плагин.");
            }
        }

        if (modulesSection.getBoolean("damage-indicator-enabled")) {
            log.info(ChatColor.YELLOW + "Модуль DamageIndicator включён! Пытаюсь найти DecentHolograms...");

            if (instanceManager.getPlugin("DecentHolograms") != null) {
                log.info(ChatColor.GREEN + "DecentHolograms был найден.");

                instanceManager.registerEvents(new DamageListener(this), this);
            }
            else {
                log.warning("DecentHolograms не был найден! Пожалуйста, установите этот плагин.");
            }
        }

        BukkitCommandHandler commandHandler = BukkitCommandHandler.create(this);
        commandHandler.register(new ReloadConfig(this));

        log.info(ChatColor.GREEN + "Beautifier был успешно включён.");
    }

    public static BeautifierPlugin getPlugin() {
        return instance;
    }

    @Override
    public void onDisable() {
        log.info(ChatColor.GREEN + "Beautifier был успешно выключен.");
    }

    public YamlDocument config = YamlDocument.create(new File(getDataFolder(), "config.yml"), getResource("config.yml"));
    public void writeConfig() throws IOException {
        if (config.getSection("modules") == null) {
            Section modulesSection = config.createSection("modules");
            Section messagesSection = config.createSection("messages");

            modulesSection.addComment(" Данный плагин использует библиотеку MiniMessage для форматирования текста.");
            modulesSection.addComment(" Ознакомиться с ней можно здесь https://docs.advntr.dev/minimessage/index.html");
            modulesSection.addComment(" Для более удобного форматирования текста рекомендую сайт https://webui.advntr.dev/");

            modulesSection.addComment(" Здесь можно включить или выключить модули плагина.");

            modulesSection.set("player-chat-enabled", true);
            modulesSection.set("damage-indicator-enabled", true);

            messagesSection.addComment(" Здесь можно изменить сообщения, которые отправляются при выполнении ивента.");
            messagesSection.addComment(" К сожалению, названия и описания достижений не работают с градиентом.");
            messagesSection.addComment(" Для голограмм формат MiniMessage пока не доступен.");
            messagesSection.addComment(" Используйте сайт https://wiki.decentholograms.eu/general/format-and-colors/colors");

            messagesSection.set("first-join", "%player_name% впервые зашел на сервер");
            messagesSection.set("join", "<color:#ffc182><hover:show_text:' <newline>   <gradient:#DF45DF:#123456>%player_last_join_date%</gradient>   <newline> '>%player_name%</hover> зашёл на сервер</color>");
            messagesSection.set("quit", "<blue>%player_name% вышел из игры");

            messagesSection.set("story-advancement", "%player_name% выполнил story <hover:show_text:\"%beautifier_title%<newline>%beautifier_description%\"><#1FFBB2>%beautifier_title%</hover>");
            messagesSection.set("adventure-advancement", "%player_name% выполнил adventure <hover:show_text:\"%beautifier_title%<newline>%beautifier_description%\"><#1FFBB2>%beautifier_title%</hover>");
            messagesSection.set("nether-advancement", "%player_name% выполнил nether <hover:show_text:\"%beautifier_title%<newline>%beautifier_description%\"><#1FFBB2>%beautifier_title%</hover>");
            messagesSection.set("end-advancement", "%player_name% выполнил end <hover:show_text:\"%beautifier_title%<newline>%beautifier_description%\"><#1FFBB2>%beautifier_title%</hover>");
            messagesSection.set("husbandry-advancement", "%player_name% выполнил husbandry <hover:show_text:\"%beautifier_title%<newline>%beautifier_description%\"><#1FFBB2>%beautifier_title%</hover>");

            messagesSection.set("entity-damage-low", "<#FBEE44>⚔ %beautifier_damage%</#FBEE44>");
            messagesSection.set("entity-damage-medium", "<#FB8B30>⚔ %beautifier_damage%</#FB8B30>");
            messagesSection.set("entity-damage-high", "<#FB2E71>⚔ %beautifier_damage%</#FB2E71>");

            config.save();
        }
    }

    public void reloadConfig() {
        try {
            config.reload();
            log.info(ChatColor.GOLD + "Конфигурационный файл был успешно перезагружен!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
