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
        // Кэширование результатов
        Section messagesSection = instance.config.getSection("messages");
        UUID uuid = UUID.randomUUID();
        // Присвоение переменной рандомного UUID
        String uuidAsString = uuid.toString();
        double damage = event.getDamage();

        // Определение переменной
        String damagePlainMessage;

        // Округление числа до десятых
        // Увы, но другого способа не нашёл
        String damageFormat = String.format("%.1f", damage);

        // Форматирование текста зависит от количества нанесённого урона
        if (damage < 2.5) {
            damagePlainMessage = messagesSection.getString("entity-damage-low");
        } else if (damage >= 2.5 && damage < 7.5) {
            damagePlainMessage = messagesSection.getString("entity-damage-medium");
        } else {
            damagePlainMessage = messagesSection.getString("entity-damage-high");
        }

        // Замена переменной %beautifier_damage% на damageFormat
        damagePlainMessage = damagePlainMessage.replace("%beautifier_damage%", damageFormat);

        // Присвоение строки списку, т.к. того требует DecentHolograms
        List<String> line = Collections.singletonList(damagePlainMessage);

        // Определение сущности, который получил урон от рук игрока
        Entity damagee = event.getEntity();
        // Определение местоположения damagee
        Location location = damagee.getLocation();

        // Рандом
        double x = nextDouble(0.0, 0.5);
        double y = nextDouble(0.1, 0.6) + damagee.getHeight();
        double z = nextDouble(0.0, 0.5);
        location.add(x, y, z);

        // Спавн голограммы с рандомным UUID для решения проблемы со спавном голограммы с уже существующим именем
        DHAPI.createHologram(uuidAsString, location, line);

        // Удаление голограммы после 1 секунды
        instance.getServer().getScheduler().scheduleSyncDelayedTask(
                instance, () -> DHAPI.removeHologram(uuidAsString), 20L
        );
    }
}
