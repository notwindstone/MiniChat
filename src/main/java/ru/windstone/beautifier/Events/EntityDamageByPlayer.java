package ru.windstone.beautifier.Events;

import eu.decentsoftware.holograms.api.DHAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import ru.windstone.beautifier.BeautifierPlugin;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.random.RandomGenerator;

import static org.apache.commons.lang3.RandomUtils.nextDouble;


public class EntityDamageByPlayer implements Listener {

    private final BeautifierPlugin instance;

    public EntityDamageByPlayer(BeautifierPlugin instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onEntityDamageByPlayer(EntityDamageByEntityEvent event) {
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        double damage = event.getDamage();
        Server server = instance.getServer();

        String damagePlainMessage;
        String damageFormat = String.format("%.1f", damage);

        if (damage < 2.5) {
            damagePlainMessage = instance.config.getSection("messages").getString("entity-damage-low");
        } else if (damage >= 2.5 && damage < 7.5) {
            damagePlainMessage = instance.config.getSection("messages").getString("entity-damage-medium");
        } else {
            damagePlainMessage = instance.config.getSection("messages").getString("entity-damage-high");
        }

        damagePlainMessage = damagePlainMessage.replace("%beautifier_damage%", damageFormat);

        // Component damageMessage = MiniMessage.miniMessage().deserialize(damagePlainMessage);
        Entity damagee = event.getEntity();

        List<String> line = Collections.singletonList(damagePlainMessage);
        Location location = damagee.getLocation();

        double x = nextDouble(0.0, 0.5);
        double y = nextDouble(0.1, 0.6) + damagee.getHeight();
        double z = nextDouble(0.0, 0.5);

        location.add(x, y, z);

        DHAPI.createHologram(uuidAsString, location, line);
        server.dispatchCommand(server.getConsoleSender(), instance.config.getSection("commands").getString("entity-damage"));

        instance.getServer().getScheduler().scheduleSyncDelayedTask(
                instance, () -> DHAPI.removeHologram(uuidAsString), 20L
        );
    }
}
