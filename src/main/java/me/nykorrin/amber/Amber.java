package me.nykorrin.amber;

import me.nykorrin.amber.listeners.EntityListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Amber extends JavaPlugin {

    private static Amber instance;

    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;

    @Override
    public void onEnable() {
        long timeStart = System.currentTimeMillis();
        instance = this;

        getConfig().options().copyDefaults(true);
        saveConfig();

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new EntityListener(this), this);

        long timeEnd = System.currentTimeMillis();
        getLogger().info("Successfully enabled. (" + (timeEnd - timeStart) + "ms)");
        System.out.println(" _______   ___  ___  ___       ________     \n" +
                "|\\  ___ \\ |\\  \\|\\  \\|\\  \\     |\\   __  \\    \n" +
                "\\ \\   __/|\\ \\  \\\\\\  \\ \\  \\    \\ \\  \\|\\  \\   \n" +
                " \\ \\  \\_|/_\\ \\  \\\\\\  \\ \\  \\    \\ \\   __  \\  \n" +
                "  \\ \\  \\_|\\ \\ \\  \\\\\\  \\ \\  \\____\\ \\  \\ \\  \\ \n" +
                "   \\ \\_______\\ \\_______\\ \\_______\\ \\__\\ \\__\\\n" +
                "    \\|_______|\\|_______|\\|_______|\\|__|\\|__|\n" +
                "                                            ");
    }

    @Override
    public void onDisable() {
        instance = null;

        saveConfig();

        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
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

    public static Economy getEconomy() {
        return econ;
    }

    public static Amber getInstance() {
        return instance;
    }
}
