package ru.windstone.beautifier.Commands;

import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import ru.windstone.beautifier.BeautifierPlugin;
import revxrsal.commands.annotation.Command;

@Command("beautifier")
public class ReloadConfig {

    private final BeautifierPlugin instance;

    public ReloadConfig(BeautifierPlugin instance) {
        this.instance = instance;
    }

    @Subcommand("reload")
    @Description("Reload the config")
    //@CommandPermission("beautifier.admin.reload")
    public void reloadConfigCommand() {
        instance.reloadConfig();
    }
}
