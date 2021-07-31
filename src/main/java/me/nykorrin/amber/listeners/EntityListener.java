package me.nykorrin.amber.listeners;

import me.nykorrin.amber.Amber;
import me.nykorrin.ayaka.AyakaAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.logging.Level;

public class EntityListener implements Listener {

    private final Amber plugin;

    public EntityListener(Amber plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntitySpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            if (this.plugin.getConfig().contains("monsters." + event.getEntityType().name().toLowerCase())) {
                this.plugin.getManagerHandler().getEntityManager().addCheatedEntity(entity.getUniqueId());
                this.plugin.debug(Level.INFO, "Registering cheated entity " + entity.getUniqueId());
            }
        }

        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
            if (this.plugin.getConfig().contains("monsters." + event.getEntityType().name().toLowerCase())) {
                this.plugin.getManagerHandler().getEntityManager().addSpawnerEntity(entity.getUniqueId());
                this.plugin.debug(Level.INFO, "Registering spawner entity " + entity.getUniqueId());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.getKiller() == null) return;
        if (!this.plugin.getConfig().contains("monsters." + event.getEntityType().name().toLowerCase())) return;

        if (this.plugin.getManagerHandler().getEntityManager().isCheatedEntity(entity.getUniqueId())) {
            this.plugin.getManagerHandler().getEntityManager().removeCheatedEntity(entity.getUniqueId());
            return;
        }
        double amount = this.plugin.getConfig().getDouble("monsters." + event.getEntityType().name().toLowerCase());

        if (amount == 0) return;

        if (this.plugin.getManagerHandler().getEntityManager().isSpawnerEntity(entity.getUniqueId())) {
            amount = amount / 2;
        }

        Player player = entity.getKiller();
        if (amount > 0) {
            Amber.getEconomy().depositPlayer(player, amount).transactionSuccess();
            if (AyakaAPI.getDataHandler().getCachedYML().getBoolean("options." + player.getUniqueId() + ".economy-messages")) {
                player.sendMessage(ChatColor.GREEN + "You earned $" + amount + " for killing a " + entity.getName() + ".");
            }
        } else {
            Amber.getEconomy().withdrawPlayer(player, Math.abs(amount)).transactionSuccess();
            if (AyakaAPI.getDataHandler().getCachedYML().getBoolean("options." + player.getUniqueId() + ".economy-messages")) {
                player.sendMessage(ChatColor.RED + "You lost $" + Math.abs(amount) + " for killing a " + entity.getName() + ".");
            }
        }
    }
}
