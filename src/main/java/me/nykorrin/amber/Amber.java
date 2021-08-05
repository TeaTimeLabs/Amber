package me.nykorrin.amber;

import me.nykorrin.amber.listeners.EntityListener;
import me.nykorrin.amber.manager.ManagerHandler;
import me.nykorrin.ayaka.util.DataHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.logging.Level;

public class Amber extends JavaPlugin {

    private static Amber instance;

    private ManagerHandler managerHandler;
    private static Economy econ = null;

    private DataHandler.YML dataHandler;

    @Override
    public void onEnable() {
        long timeStart = System.currentTimeMillis();

        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        instance = this;
        dataHandler = new DataHandler.YML(this, "/data.yml");

        getConfig().options().copyDefaults(true);
        dataHandler.getCachedYML().options().copyDefaults(true);
        saveConfig();
        dataHandler.saveYML();

        registerManagers();
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);

        long timeEnd = System.currentTimeMillis();
        getLogger().info("Successfully enabled in " + (timeEnd - timeStart) + "ms");
    }

    @Override
    public void onDisable() {
        long timeStart = System.currentTimeMillis();

        if (getConfig().getBoolean("server.clear-cheated")) {
            for (UUID uuid : managerHandler.getEntityManager().getCheatedList()) {
                if (Bukkit.getEntity(uuid) != null) {
                    Bukkit.getEntity(uuid).remove();
                }
            }
        }

        saveConfig();
        managerHandler.getEntityManager().save();

        instance = null;
        long timeEnd = System.currentTimeMillis();
        getLogger().info("Successfully disabled in " + (timeEnd - timeStart) + "ms");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public void debug(Level level, String debug) {
        if (getConfig().getBoolean("server.debug")) {
            getLogger().log(level, debug);
        }
    }

    private void registerManagers() {
        managerHandler = new ManagerHandler(this);
    }

    public static Amber getInstance() {
        return instance;
    }

    public ManagerHandler getManagerHandler() {
        return managerHandler;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public DataHandler.YML getDataHandler() {
        return dataHandler;
    }
}
