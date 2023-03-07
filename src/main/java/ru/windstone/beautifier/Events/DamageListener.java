package ru.windstone.beautifier.Events;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import ru.windstone.beautifier.BeautifierPlugin;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.RandomUtils.nextDouble;


public class DamageListener implements Listener {

    private final BeautifierPlugin instance;

    public DamageListener(BeautifierPlugin instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onEntityDamageByPlayer(EntityDamageByEntityEvent event) {
        Section messagesSection = instance.config.getSection("messages");

        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        double damage = event.getDamage();

        String damagePlainMessage;
        String damageFormat = String.format("%.1f", damage);

        if (damage < 2.5) {
            damagePlainMessage = messagesSection.getString("entity-damage-low");
        } else if (damage >= 2.5 && damage < 7.5) {
            damagePlainMessage = messagesSection.getString("entity-damage-medium");
        } else {
            damagePlainMessage = messagesSection.getString("entity-damage-high");
        }

        damagePlainMessage = damagePlainMessage.replace("%beautifier_damage%", damageFormat);

        Entity damagee = event.getEntity();

        List<String> line = Collections.singletonList(damagePlainMessage);
        Location location = damagee.getLocation();

        double x = nextDouble(0.0, 0.5);
        double y = nextDouble(0.1, 0.6) + damagee.getHeight();
        double z = nextDouble(0.0, 0.5);

        location.add(x, y, z);

        DHAPI.createHologram(uuidAsString, location, line);

        instance.getServer().getScheduler().scheduleSyncDelayedTask(
                instance, () -> DHAPI.removeHologram(uuidAsString), 20L
        );
    }
}
