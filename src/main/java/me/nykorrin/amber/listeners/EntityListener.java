package me.nykorrin.amber.listeners;

import me.nykorrin.amber.Amber;
import me.nykorrin.ayaka.AyakaAPI;
import me.nykorrin.ganyu.GanyuAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;
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
        if (!this.plugin.getConfig().contains("monsters." + entity.getType().name().toLowerCase())) return;

        if (this.plugin.getManagerHandler().getEntityManager().isCheatedEntity(entity.getUniqueId())) {
            this.plugin.getManagerHandler().getEntityManager().removeCheatedEntity(entity.getUniqueId());
            return;
        }

        double amount = this.plugin.getConfig().getDouble("monsters." + entity.getType().name().toLowerCase());

        if (amount == 0) return;

        if (this.plugin.getManagerHandler().getEntityManager().isSpawnerEntity(entity.getUniqueId())) {
            event.setDroppedExp(event.getDroppedExp() / 2);
            amount /= 2;
        }

        switch (entity.getType()) {
            case SILVERFISH -> {
                Player player = entity.getKiller();

                if (!player.getInventory().getItemInMainHand().hasItemMeta()) return;
                if (!player.getInventory().getItemInMainHand().getItemMeta().hasEnchants()) return;

                if (player.getInventory().getItemInMainHand().getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_MOBS)) {
                    int level = player.getInventory().getItemInMainHand().getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_MOBS);

                    if (ThreadLocalRandom.current().nextInt(10) <= level) {
                        event.getDrops().add(new ItemStack(Material.SLIME_BALL, ThreadLocalRandom.current().nextInt(level)));
                    }
                }
            }
            case ENDERMAN -> {
                if (entity.getWorld().getEnvironment() == World.Environment.THE_END) {
                    event.setDroppedExp(event.getDroppedExp() / 2);
                    amount /= 2;
                }
            }
            case PIGLIN, ZOMBIFIED_PIGLIN -> {
                if (entity.getWorld().getEnvironment() == World.Environment.NETHER && entity.getLocation().getY() >= 128) {
                    event.setDroppedExp(event.getDroppedExp() / 3);
                    amount /= 3;
                }
            }
        }

        if (GanyuAPI.getEventManager().getBloodMoon().isActive()) {
            if (GanyuAPI.getEventManager().getBloodMoon().getEntityList().contains(entity.getUniqueId())) {
                switch (entity.getType()) {
                    case GIANT -> amount = this.plugin.getConfig().getDouble("monsters.events.blood-moon.giant-zombie");
                    case ZOMBIE -> amount = this.plugin.getConfig().getDouble("monsters.events.blood-moon.zombie");
                    case SKELETON -> amount = this.plugin.getConfig().getDouble("monsters.events.blood-moon.skeleton");
                }
            }
        }
        
        if (GanyuAPI.getEventManager().getNetherRaid().isActive()) {
            if (GanyuAPI.getEventManager().getNetherRaid().getEntityList().contains(entity.getUniqueId())) {
                switch (entity.getType()) {
                    case PIGLIN_BRUTE -> amount = this.plugin.getConfig().getDouble("monsters.events.nether-raid.piglin-brute");
                    case WITHER_SKELETON -> amount = this.plugin.getConfig().getDouble("monsters.events.nether-raid.wither-skeleton");
                    case PIGLIN -> {
                        if (entity.getName().contains("raider")) {
                            amount = this.plugin.getConfig().getDouble("monsters.events.nether-raid.piglin-raider");
                        } else {
                            amount = this.plugin.getConfig().getDouble("monsters.events.nether-raid.piglin-thief");
                        }
                    }
                    case BLAZE -> amount = this.plugin.getConfig().getDouble("monsters.events.nether-raid.blaze");
                    case ZOMBIFIED_PIGLIN -> amount = this.plugin.getConfig().getDouble("monsters.events.nether-raid.zombified-piglin");
                }
            }
        }*/

        Player player = entity.getKiller();
        BigDecimal decimal = new BigDecimal(amount).setScale(2, RoundingMode.HALF_EVEN);

        if (amount > 0) {
            if (AyakaAPI.getDataHandler().getCachedYML().getBoolean("options." + player.getUniqueId() + ".economy-visible")) {
                player.sendMessage(ChatColor.GREEN + "You earned $" + decimal.doubleValue() + " for killing a " + entity.getName() + ".");
            }
            Amber.getEconomy().depositPlayer(player, decimal.doubleValue()).transactionSuccess();
        } else {
            if (AyakaAPI.getDataHandler().getCachedYML().getBoolean("options." + player.getUniqueId() + ".economy-visible")) {
                player.sendMessage(ChatColor.RED + "You lost $" + Math.abs(decimal.doubleValue()) + " for killing a " + entity.getName() + ".");
            }
            Amber.getEconomy().withdrawPlayer(player, Math.abs(decimal.doubleValue())).transactionSuccess();
        }
    }
}
