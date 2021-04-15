package me.nykorrin.eula.listeners;

import me.nykorrin.eula.Eula;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EntityListener implements Listener {

    private Eula plugin;
    private Set<UUID> cheatedEntities;

    public EntityListener(Eula plugin) {
        this.plugin = plugin;
        this.cheatedEntities = new HashSet<>();
    }

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();

        // Spawn reason is either via spawn egg or custom
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
            // Cycle entity types
            for (EntityType type : EntityType.values()) {
                // Entity type is type
                if (entity.getType() == type) {
                    // Config contains entity
                    if (this.plugin.getConfig().contains("monsters." + type.name().toLowerCase())) {
                        cheatedEntities.add(entity.getUniqueId());
                        this.plugin.getLogger().info("Cheated entity detected. UUID of " + entity.getName() + " is " + entity.getUniqueId());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player player = entity.getKiller();
        String name = entity.getName();
        double amount = 0;

        // Cycle entity types
        for (EntityType type : EntityType.values()) {
            // Entity type is type
            if (entity.getType() == type) {
                // Config contains entity
                if (this.plugin.getConfig().contains("monsters." + type.name().toLowerCase())) {
                    // Entity is cheated entity
                    if (cheatedEntities.contains(entity.getUniqueId())) {
                        this.plugin.getLogger().info("Cheated entity killed. UUID of " + entity.getName() + " was " + entity.getUniqueId());
                        cheatedEntities.remove(entity.getUniqueId());
                        return;
                    }

                    amount = this.plugin.getConfig().getDouble("monsters." + type.name().toLowerCase());
                }
            }
        }

        // Player is null
        if (player == null) {
            return;
        }

        // Amount is 0
        if (amount == 0) {
            return;
        }

        // Ammount is greater 0
        if (amount > 0) {
            Eula.getEconomy().depositPlayer(player, amount).transactionSuccess();
            player.sendMessage(ChatColor.GREEN + "You earned $" + amount + " for killing a " + name + ".");
            // Amount is less than 0
        } else {
            Eula.getEconomy().withdrawPlayer(player, Math.abs(amount)).transactionSuccess();
            player.sendMessage(ChatColor.RED + "You lost $" + Math.abs(amount) + " for killing a " + name + ".");
        }

    }
}
