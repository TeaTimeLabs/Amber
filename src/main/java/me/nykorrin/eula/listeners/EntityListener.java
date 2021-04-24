package me.nykorrin.eula.listeners;

import me.nykorrin.eula.Eula;
import me.nykorrin.sucrose.SucroseAPI;
import org.bukkit.Bukkit;
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

        if (entity.getCustomName() != null) {
            if (SucroseAPI.getManagerHandler().getEventManager().getBloodmoon().getEntites().contains(entity.getCustomName())) {
                this.plugin.getLogger().info("Bloodmoon entity detected. UUID of " + entity.getType().name() + " is " + entity.getUniqueId());
                return;
            }
        }

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

        // Player is null
        if (player == null) {
            return;
        }

        // Entity has a name
        if (entity.getCustomName() != null) {
            // Bloodmoon entities contains entity
            if (SucroseAPI.getManagerHandler().getEventManager().getBloodmoon().getEntites().contains(entity.getCustomName())) {
                this.plugin.getLogger().info("Bloodmoon entity killed. UUID of " + entity.getType().name() + " was " + entity.getUniqueId());

                if (entity.getCustomName().contains("Bloodmoon-infused Giant Zombie")) {
                    amount = this.plugin.getConfig().getDouble("events.bloodmoon.bloodmoon_giant");
                }

                if (entity.getCustomName().contains("Bloodmoon-infused Zombie")) {
                    amount = this.plugin.getConfig().getDouble("events.bloodmoon.bloodmoon_zombie");
                }

                if (entity.getCustomName().contains("Bloodmoon-infused Skeleton")) {
                    amount = this.plugin.getConfig().getDouble("events.bloodmoon.bloodmoon_skeleton");
                }

                Eula.getEconomy().depositPlayer(player, amount).transactionSuccess();
                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + player.getName() + " earned $" + amount + " for killing a " + entity.getName() + ChatColor.LIGHT_PURPLE + ".");
                return;
            }
            // TODO: Add the rest of the events here
        }

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
