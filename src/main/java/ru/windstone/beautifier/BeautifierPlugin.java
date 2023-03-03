package ru.windstone.beautifier;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import me.clip.placeholderapi.libs.kyori.adventure.platform.facet.Facet;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.graalvm.shadowed.org.jcodings.Config;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import ru.windstone.beautifier.Commands.ReloadConfig;
import ru.windstone.beautifier.Events.EntityDamageByPlayer;
import ru.windstone.beautifier.Events.PlayerGetsAdvancement;
import ru.windstone.beautifier.Events.PlayerJoin;
import ru.windstone.beautifier.Events.PlayerQuit;

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
        // Plugin startup logic
        instance = this;

        try {
            writeConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info(ChatColor.YELLOW + "Пытаюсь найти PlaceholderAPI...");

        if (instanceManager.getPlugin("PlaceholderAPI") != null) {
            /*
             * We register the EventListener here, when PlaceholderAPI is installed.
             * Since all events are in the main class (this class), we simply use "this"
             */
            log.info(ChatColor.GREEN + "PlaceholderAPI был найден!");

            Section modulesConfig = config.getSection("modules");

            if (modulesConfig.getString("player-connection").equals("enabled")) {
                instanceManager.registerEvents(new PlayerJoin(this), this);
                instanceManager.registerEvents(new PlayerQuit(this), this);

                log.info("Был включён модуль PlayerConnection");
            }

            if (modulesConfig.getString("player-advancements").equals("enabled")) {
                instanceManager.registerEvents(new PlayerGetsAdvancement(this), this);

                log.info("Был включён модуль PlayerAdvancements");
            }

            // TODO: Попробовать с try-catch
            if (modulesConfig.getString("entity-damage-indicator").equals("enabled") && instanceManager.getPlugin("DecentHolograms") != null) {
                log.info(ChatColor.YELLOW + "Пытаюсь найти DecentHolograms..." + "");

                instanceManager.registerEvents(new EntityDamageByPlayer(this), this);

                log.info("DecentHolograms найден, был включён модуль EntityDamage.");
            }
            else if (modulesConfig.getString("entity-damage-indicator").equals("disabled")) {
                log.info("Модуль EntityDamage отключён.");
            }
            else {
                log.info(ChatColor.YELLOW + "Пытаюсь найти DecentHolograms..." + "");

                log.info(ChatColor.RED + "DecentHolograms не был найден. Модуль EntityDamage не будет работать!");

                modulesConfig.set("entity-damage-indicator", "disabled");

                try {
                    config.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            BukkitCommandHandler commandHandler = BukkitCommandHandler.create(this);
            commandHandler.register(new ReloadConfig(this));
        } else {
            /*
             * We inform about the fact that PlaceholderAPI isn't installed and then
             * disable this plugin to prevent issues.
             */
            log.warning(ChatColor.RED + "Не могу найти PlaceholderAPI! Установите его, чтобы использовать этот плагин.");
            instanceManager.disablePlugin(this);
        }

        log.info(ChatColor.GREEN + "Beautifier был успешно включён.");
    }

    public static BeautifierPlugin getPlugin() {
        return instance;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        log.info(ChatColor.GREEN + "Beautifier был успешно выключен.");
    }

    public YamlDocument config = YamlDocument.create(new File(getDataFolder(), "config.yml"), getResource("config.yml"));
    public void writeConfig() throws IOException {
        if (config.getSection("modules") == null) {
            Section modulesSection = config.createSection("modules");
            Section commandsSection = config.createSection("commands");
            Section messagesSection = config.createSection("messages");

            modulesSection.addComment(" Данный плагин использует библиотеку MiniMessage для форматирования текста.");
            modulesSection.addComment(" Ознакомиться с ней можно здесь https://docs.advntr.dev/minimessage/index.html");
            modulesSection.addComment(" Для более удобного форматирования текста рекомендую сайт https://webui.advntr.dev/");
            modulesSection.addComment(" Здесь можно включить или выключить модули.");
            commandsSection.addComment(" Здесь можно вписать команды, которые отправляются при выполнении ивента.");
            messagesSection.addComment(" Здесь можно изменить сообщения, которые отправляются при выполнении ивента.");
            messagesSection.addComment(" К сожалению, для голограмм формат MiniMessage пока не доступен.");
            messagesSection.addComment(" Используйте сайт https://wiki.decentholograms.eu/general/format-and-colors/colors");

            modulesSection.set("player-connection", "enabled");
            modulesSection.set("player-advancements", "enabled");
            modulesSection.set("entity-damage-indicator", "enabled");

            commandsSection.set("first-join", "give @r diamond 1");
            commandsSection.set("join", "give @r diamond 1");
            commandsSection.set("quit", "give @r diamond 1");
            commandsSection.set("advancement", "give @r diamond 1");
            commandsSection.set("entity-damage", "give @r diamond 1");

            messagesSection.set("first-join", "%player_name% впервые зашел на сервер");
            messagesSection.set("join", "<color:#ffc182><hover:show_text:' <newline>   <gradient:#DF45DF:#123456>%player_last_join_date%</gradient>   <newline> '>%player_name%</hover> зашёл на сервер</color>");
            messagesSection.set("quit", "<blue>%player_name% вышел из игры");
            messagesSection.set("advancement", "%player_name% выполнил <gradient:#1FFBB2:#10FDF6:#208DFF:#4F1BFF>%beautifier_advancement%</gradient>");
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
