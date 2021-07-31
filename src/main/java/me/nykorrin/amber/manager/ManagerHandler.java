package me.nykorrin.amber.manager;

import me.nykorrin.amber.Amber;
import me.nykorrin.amber.managers.EntityManager;

public class ManagerHandler {

    private final Amber plugin;

    private final EntityManager entityManager;

    public ManagerHandler(Amber plugin) {
        this.plugin = plugin;

        this.entityManager = new EntityManager(this);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public Amber getPlugin() {
        return plugin;
    }
}
