package me.nykorrin.amber.listeners;

import me.nykorrin.amber.Amber;
import me.nykorrin.sucrose.SucroseAPI;
import me.nykorrin.sucrose.events.EventType;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EntityListener implements Listener {

    private Amber plugin;
    private Set<UUID> cheatedEntities;

    public EntityListener(Amber plugin) {
        this.plugin = plugin;
        this.cheatedEntities = new HashSet<>();
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onNPCSpawn(NPCSpawnEvent event) {
        NPC npc = event.getNPC();
        LivingEntity entity = (LivingEntity) npc.getEntity();

        if (!SucroseAPI.getManagerHandler().getEventManager().getKokushibo().getNPCs().isEmpty()) {
            if (SucroseAPI.getManagerHandler().getEventManager().getKokushibo().getNPCs().contains(npc)) {
                this.plugin.getLogger().info("Kokushibo entity detected. UUID of NPC is " + entity.getUniqueId());
            }
        }

        if (!SucroseAPI.getManagerHandler().getEventManager().getDoma().getNPCs().isEmpty()) {
            if (SucroseAPI.getManagerHandler().getEventManager().getDoma().getNPCs().contains(npc)) {
                this.plugin.getLogger().info("Doma entity detected. UUID of NPC is " + entity.getUniqueId());
            }
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onNPCDeath(NPCDeathEvent event) {
        NPC npc = event.getNPC();
        LivingEntity entity = (LivingEntity) npc.getEntity();
        Player player = entity.getKiller();

        if (player == null) {
            return;
        }

        if (SucroseAPI.getManagerHandler().getEventManager().getKokushibo().getNPCs().contains(npc)) {
            this.plugin.getLogger().info("Kokushibo entity killed. UUID of Kokushibo was " + entity.getUniqueId());

            double amount = this.plugin.getConfig().getDouble("events.kokushibo.kokushibo");

            Amber.getEconomy().depositPlayer(player, amount).transactionSuccess();
            Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + player.getName() + " earned $" + amount + " for killing Upper Moon One: Kokushibō.");
        }

        if (SucroseAPI.getManagerHandler().getEventManager().getDoma().getNPCs().contains(npc)) {
            this.plugin.getLogger().info("Doma entity killed. UUID of Doma was " + entity.getUniqueId());

            double amount = this.plugin.getConfig().getDouble("events.doma.doma");

            Amber.getEconomy().depositPlayer(player, amount).transactionSuccess();
            Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + player.getName() + " earned $" + amount + " for killing Upper Moon Two: Dōma.");
        }

        if (SucroseAPI.getManagerHandler().getEventManager().getDoma().getChildren().contains(npc)) {
            this.plugin.getLogger().info("Doma entity killed. UUID of Crystalline Divine Child was " + entity.getUniqueId());

            double amount = this.plugin.getConfig().getDouble("events.doma.crystalline_divine_child");

            Amber.getEconomy().depositPlayer(player, amount).transactionSuccess();
            Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + player.getName() + " earned $" + amount + " for killing a Crystalline Divine Child.");
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onEntitySpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();

        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
            if (SucroseAPI.getManagerHandler().getEventManager().getBloodmoon().getEntites().contains(entity.getCustomName())) {
                this.plugin.getLogger().info("Bloodmoon entity detected. UUID of " + entity.getType().name() + " is " + entity.getUniqueId());
            }

            if (SucroseAPI.getManagerHandler().getEventManager().getNetherRaid().getEntites().contains(entity.getCustomName())) {
                this.plugin.getLogger().info("Nether Raid entity detected. UUID of " + entity.getType().name() + " is " + entity.getUniqueId());
            }
        }

        
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            for (EntityType type : EntityType.values()) {
                if (entity.getType() == type) {
                    if (this.plugin.getConfig().contains("monsters." + type.name().toLowerCase())) {
                        cheatedEntities.add(entity.getUniqueId());
                        this.plugin.getLogger().info("Cheated entity detected. UUID of " + entity.getName() + " is " + entity.getUniqueId());
                    }
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player player = entity.getKiller();
        String name = entity.getName();
        double amount = 0;

        if (player == null) {
            return;
        }

        if (entity.getCustomName() != null) {
            if (SucroseAPI.getManagerHandler().getEventManager().getBloodmoon().getEntites().contains(entity.getCustomName())) {
                this.plugin.getLogger().info("Bloodmoon entity killed. UUID of " + entity.getName() + " was " + entity.getUniqueId());

                if (entity.getCustomName().contains("Bloodmoon-infused Giant Zombie")) {
                    amount = this.plugin.getConfig().getDouble("events.bloodmoon.bloodmoon_giant");
                }

                if (entity.getCustomName().contains("Bloodmoon-infused Zombie")) {
                    amount = this.plugin.getConfig().getDouble("events.bloodmoon.bloodmoon_zombie");
                }

                if (entity.getCustomName().contains("Bloodmoon-infused Skeleton")) {
                    amount = this.plugin.getConfig().getDouble("events.bloodmoon.bloodmoon_skeleton");
                }

                Amber.getEconomy().depositPlayer(player, amount).transactionSuccess();
                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + player.getName() + " earned $" + amount + " for killing a " + entity.getName() + ChatColor.LIGHT_PURPLE + ".");
                return;
            }

            if (SucroseAPI.getManagerHandler().getEventManager().getNetherRaid().getEntites().contains(entity.getCustomName())) {
                this.plugin.getLogger().info("Nether Raid entity killed. UUID of " + entity.getName() + " was " + entity.getUniqueId());

                if (entity.getCustomName().contains("Piglin Brute Raider")) {
                    amount = this.plugin.getConfig().getDouble("events.nether_raid.piglin_brute");
                }

                if (entity.getCustomName().contains("Wither Skeleton Guard")) {
                    amount = this.plugin.getConfig().getDouble("events.nether_raid.wither_skeleton_guard");
                }

                if (entity.getCustomName().contains("Piglin Raider")) {
                    amount = this.plugin.getConfig().getDouble("events.nether_raid.piglin_raider");
                }

                if (entity.getCustomName().contains("Piglin Thief")) {
                    amount = this.plugin.getConfig().getDouble("events.nether_raid.piglin_thief");
                }

                if (entity.getCustomName().contains("Blaze Raider")) {
                    amount = this.plugin.getConfig().getDouble("events.nether_raid.blaze_raider");
                }

                if (entity.getCustomName().contains("Zombie Piglin Minion")) {
                    amount = this.plugin.getConfig().getDouble("events.nether_raid.zombie_piglin_minion");
                }

                Amber.getEconomy().depositPlayer(player, amount).transactionSuccess();
                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + player.getName() + " earned $" + amount + " for killing a " + entity.getName() + ChatColor.LIGHT_PURPLE + ".");
                return;
            }
        }

        for (EntityType type : EntityType.values()) {
            if (entity.getType() == type) {
                if (this.plugin.getConfig().contains("monsters." + type.name().toLowerCase())) {
                    if (cheatedEntities.contains(entity.getUniqueId())) {
                        this.plugin.getLogger().info("Cheated entity killed. UUID of " + entity.getName() + " was " + entity.getUniqueId());
                        cheatedEntities.remove(entity.getUniqueId());
                        return;
                    }

                    amount = this.plugin.getConfig().getDouble("monsters." + type.name().toLowerCase());
                }
            }
        }

        if (amount == 0) {
            return;
        }

        if (SucroseAPI.getManagerHandler().getEventManager().getEventActivity(EventType.BLOOD_MOON)) {
            amount = amount * 2;
        }

        if (amount > 0) {
            Amber.getEconomy().depositPlayer(player, amount).transactionSuccess();

            if (SucroseAPI.getPlugin().getDataConfig().getCachedYML().getBoolean("options." + player.getUniqueId() + ".economy_messages")) {
                player.sendMessage(ChatColor.GREEN + "You earned $" + amount + " for killing a " + name + ".");
            }
        } else {
            Amber.getEconomy().withdrawPlayer(player, Math.abs(amount)).transactionSuccess();

            if (SucroseAPI.getPlugin().getDataConfig().getCachedYML().getBoolean("options." + player.getUniqueId() + ".economy_messages")) {
                player.sendMessage(ChatColor.RED + "You lost $" + Math.abs(amount) + " for killing a " + name + ".");
            }
        }
    }
}
