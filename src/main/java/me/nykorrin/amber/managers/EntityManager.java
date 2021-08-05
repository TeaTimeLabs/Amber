package me.nykorrin.amber.managers;

import me.nykorrin.amber.manager.Manager;
import me.nykorrin.amber.manager.ManagerHandler;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class EntityManager extends Manager {

    private final List<UUID> cheatedList;
    private final List<UUID> spawnerList;

    public EntityManager(ManagerHandler managerHandler) {
        super(managerHandler);
        cheatedList = new ArrayList<>();
        spawnerList = new ArrayList<>();
        loadEntities();
    }

    private void loadEntities() {
        if (!this.managerHandler.getPlugin().getDataHandler().getCachedYML().getStringList("cheated-entities").isEmpty()) {
            for (String uuid : this.managerHandler.getPlugin().getDataHandler().getCachedYML().getStringList("cheated-entities")) {
                cheatedList.add(UUID.fromString(uuid));
                this.managerHandler.getPlugin().debug(Level.INFO, "Registering cheated entity " + uuid);
            }
            for (String uuid : this.managerHandler.getPlugin().getDataHandler().getCachedYML().getStringList("spawner-entities")) {
                spawnerList.add(UUID.fromString(uuid));
                this.managerHandler.getPlugin().debug(Level.INFO, "Registering spawner entity " + uuid);
            }
        }
    }

    public boolean isCheatedEntity(UUID uuid) {
        return cheatedList.contains(uuid);
    }

    public boolean isSpawnerEntity(UUID uuid) {
        return spawnerList.contains(uuid);
    }

    public void addCheatedEntity(UUID uuid) {
        cheatedList.add(uuid);
    }

    public void addSpawnerEntity(UUID uuid) {
        spawnerList.add(uuid);
    }

    public void removeCheatedEntity(UUID uuid) {
        cheatedList.remove(uuid);
    }

    public void removeSpawnerEntity(UUID uuid) {
        spawnerList.remove(uuid);
    }

    public List<UUID> getCheatedList() {
        return cheatedList;
    }

    public void save() {
        List<String> saveCheated = new ArrayList<>();
        for (UUID uuid : cheatedList) {
            if (Bukkit.getEntity(uuid) != null) {
                saveCheated.add(uuid.toString());
                this.managerHandler.getPlugin().debug(Level.INFO, "Saving cheated entity " + uuid.toString());
            }
        }
        this.managerHandler.getPlugin().getDataHandler().getCachedYML().set("cheated-entities", saveCheated);
        List<String> saveSpawner = new ArrayList<>();
        for (UUID uuid : spawnerList) {
            if (Bukkit.getEntity(uuid) != null) {
                saveSpawner.add(uuid.toString());
                this.managerHandler.getPlugin().debug(Level.INFO, "Saving spawner entity " + uuid.toString());
            }
        }
        this.managerHandler.getPlugin().getDataHandler().getCachedYML().set("spawner-entities", saveSpawner);
        this.managerHandler.getPlugin().getDataHandler().saveYML();
    }
}
