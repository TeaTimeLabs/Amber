package me.nykorrin.eula.listeners;

import me.nykorrin.eula.Eula;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityListener implements Listener {

    private Eula plugin;

    public EntityListener(Eula plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        // Killer is a player
        if (entity.getKiller() instanceof Player) {
            Player player = entity.getKiller();

            // Entity is a living entity
            if (event.getEntity() instanceof LivingEntity) {
                String name = entity.getName();
                double amount = 0;

                // Player is null
                if (player == null) {
                    return;
                }

                for (EntityType type : EntityType.values()) {
                    if (entity.getType() == type) {
                        if (this.plugin.getConfig().contains("monsters." + type.name().toLowerCase())) {
                            amount = this.plugin.getConfig().getDouble("monsters." + type.name().toLowerCase());
                        }
                    }
                }

                if (amount == 0) {
                    return;
                }

                if (amount > 0) {
                    Eula.getEconomy().depositPlayer(player, amount).transactionSuccess();
                    player.sendMessage(ChatColor.GREEN + "You earned $" + amount + " for killing a " + name + ".");
                } else {
                    Eula.getEconomy().withdrawPlayer(player, Math.abs(amount)).transactionSuccess();
                    player.sendMessage(ChatColor.RED + "You lost $" + Math.abs(amount) + " for killing a " + name + ".");
                }
            }
        }
    }
}
